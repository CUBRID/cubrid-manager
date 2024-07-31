/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.ui.cubrid.table.control;

import com.cubrid.common.core.util.LogUtil;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This type is responsible for getting the info of total number and the first line, which extends
 * the type of DefaultHandler and implements the interface of Runnable
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-12-2 created by lizhiqiang
 */
public class XlsxRowNumberHandler extends DefaultHandler { // FIXME move this logic to core module

    private static final Logger LOGGER = LogUtil.getLogger(XlsxRowNumberHandler.class);

    // All rows number in all sheets
    private int numberAllRow;
    // The item row in every sheet
    private List<Integer> itemsNumberOfSheets;
    private final List<String> firstRowLst = new ArrayList<String>();

    private SharedStringsTable sharedStringTable;

    private boolean isCancel;
    private boolean isEnd;

    private final String fileName;

    private boolean nextIsString;
    private String contents;
    private int cols;

    /**
     * The constructor
     *
     * @param fileName String
     */
    public XlsxRowNumberHandler(String fileName) {
        this.fileName = fileName;
    }

    /** the thread method */
    public void process() {
        numberAllRow = 0;
        firstRowLst.clear();
        InputStream stream = null;
        itemsNumberOfSheets = new ArrayList<Integer>();
        try {
            stream = new BufferedInputStream(new FileInputStream(fileName));
            OPCPackage pkg = OPCPackage.open(stream);
            XSSFReader reader = new XSSFReader(pkg);
            sharedStringTable = reader.getSharedStringsTable();

            XMLReader xmlReader =
                    XMLReaderFactory.createXMLReader(
                            "org.apache.xerces.parsers.SAXParser"); //$NON-NLS-1$
            xmlReader.setContentHandler(this);

            Iterator<InputStream> sheets = reader.getSheetsData();
            int sheetNum = 0;
            while (sheets.hasNext()) {
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                try {
                    xmlReader.parse(sheetSource);
                } finally {
                    try {
                        if (sheet != null) {
                            sheet.close();
                        }
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                }
                if (sheetNum == 0) {
                    itemsNumberOfSheets.add(numberAllRow);
                } else {
                    int numberBefore = 0;
                    for (int i = 0; i < itemsNumberOfSheets.size(); i++) {
                        numberBefore += itemsNumberOfSheets.get(i);
                    }
                    int items = numberAllRow - numberBefore;
                    itemsNumberOfSheets.add(items);
                }
                sheetNum++;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            isEnd = true;
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
    }

    /**
     * Response to reading the start of an element
     *
     * @param uri the uri of namespace
     * @param localName the local name
     * @param name the element name
     * @param attributes the instance of Attributes
     * @throws SAXException the SAXException
     */
    public void startElement(String uri, String localName, String name, Attributes attributes)
            throws SAXException {
        if (isCancel) {
            throw new SAXException();
        }
        if ("row".equals(name)) {
            numberAllRow++;
        }
        if (numberAllRow == 1 && "c".equals(name)) {
            String cellType = attributes.getValue("t");
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        contents = "";
    }

    /**
     * Response to reading the end of an element
     *
     * @param uri the uri of namespace
     * @param localName the local name
     * @param name the element name
     * @throws SAXException the SAXException
     */
    public void endElement(String uri, String localName, String name) throws SAXException {

        if (numberAllRow == 1 && ("v".equals(name) || "t".equals(name))) {
            if (nextIsString) {
                try {
                    int idx = Integer.parseInt(contents);
                    contents = new XSSFRichTextString(sharedStringTable.getEntryAt(idx)).toString();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }
            String value = contents.trim();
            if ("".equals(value)) {
                return;
            }
            value = "".equals(value) ? " " : value;

            firstRowLst.add(cols, value);
            cols++;
        }
    }

    /**
     * This method will output the lastContents based on the given value
     *
     * @param ch the given char arrays
     * @param start the start position of the output string
     * @param length the length of the output string
     * @throws SAXException the SAXException
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (numberAllRow == 1) {
            contents += new String(ch, start, length);
        }
    }

    /**
     * Whether is interrupted
     *
     * @return the isInterrupted
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Whether is cancel
     *
     * @return the isCancel
     */
    public boolean isCancel() {
        return isCancel;
    }

    /** @param isCancel the isCancel to set */
    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    /**
     * Get the number of all row
     *
     * @return int
     */
    public int getNumberOfAllRow() {
        return numberAllRow;
    }

    /**
     * Get the info of the first line
     *
     * @return List<String>
     */
    public List<String> getHeadInfo() {
        return firstRowLst;
    }

    /**
     * Get the numbers of all sheet
     *
     * @return List<Integer>
     */
    public List<Integer> getItemsNumberOfSheets() {
        return itemsNumberOfSheets;
    }
}
