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
import com.cubrid.common.ui.cubrid.table.dialog.DataFormatException;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSXImportFileHandler;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
 * This type is extended the type of DefaultHandler, which is responsible for reader excel 2007
 * format file.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-11-17 created by lizhiqiang
 */
public class XlsxReaderHandler extends DefaultHandler { // FIXME move this logic to core module

    private static final Logger LOGGER = LogUtil.getLogger(XlsxReaderHandler.class);

    private SharedStringsTable sharedStringTable;
    private String lastContents;
    private boolean nextIsString;

    private int sheetIndex = -1;
    private final List<String> rowlist = new ArrayList<String>();
    // Current read row in a sheet
    protected int currentRow = 0;
    private int currentCol = 0;
    private int preCol = 0;
    private int titleRow = -1;
    private int colsize = 0;
    private int allRowNum = 0;

    private boolean isEnd;
    private boolean isCancel;

    private final XLSXImportFileHandler importFileHandler;

    /**
     * The constructor
     *
     * @param importFileHandler XLSXImportFileHandler
     */
    public XlsxReaderHandler(XLSXImportFileHandler importFileHandler) {
        this.importFileHandler = importFileHandler;
    }

    /**
     * The method is responsible for the task of read the file using XMLReader
     *
     * @param filename the name of file
     * @throws Exception the Exception
     */
    public void process(String filename) throws Exception {
        try {
            this.sharedStringTable = importFileHandler.getSharedStringsTable();
            Iterator<InputStream> sheets = importFileHandler.getSheets().iterator();

            XMLReader xmlReader =
                    XMLReaderFactory.createXMLReader(
                            "org.apache.xerces.parsers.SAXParser"); //$NON-NLS-1$
            xmlReader.setContentHandler(this);

            while (sheets.hasNext()) {
                currentRow = 0;
                sheetIndex++;
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
                allRowNum += currentRow;
            }
        } catch (OutOfMemoryError error) {
            throw new RuntimeException(error);
        } finally {
            isEnd = true;
        }
    }

    /**
     * response the event start an element
     *
     * @param uri the uri of namespace
     * @param localName the local name
     * @param name the element name
     * @param attributes the instance of Attributes
     * @throws SAXException the SAXException
     */
    public void startElement(String uri, String localName, String name, Attributes attributes)
            throws SAXException {
        if ("c".equals(name)) {
            String cellType = attributes.getValue("t");
            String rowStr = attributes.getValue("r");
            currentCol = this.getColumnIndex(rowStr);
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        lastContents = "";
    }

    /**
     * response the event end an element
     *
     * @param uri the uri of namespace
     * @param localName the local name
     * @param name the element name
     * @throws SAXException the SAXException
     */
    public void endElement(String uri, String localName, String name) throws SAXException {
        if ("v".equals(name) || "t".equals(name)) {
            if (nextIsString) {
                try {
                    int idx = Integer.parseInt(lastContents);
                    lastContents =
                            new XSSFRichTextString(sharedStringTable.getEntryAt(idx)).toString();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            String value = lastContents;
            if ("".equals(value)) {
                return;
            }
            value = "".equals(value) ? " " : value;
            int cols = currentCol - preCol;
            if (cols > 1) {
                for (int i = 0; i < cols - 1; i++) {
                    rowlist.add(preCol, "");
                }
            }
            preCol = currentCol;
            rowlist.add(currentCol - 1, value);
        } else {
            if ("row".equals(name)) {
                if (rowlist.isEmpty()) {
                    return;
                }
                int tmpCols = rowlist.size();
                if (currentRow > this.titleRow && tmpCols < this.colsize) {
                    for (int i = 0; i < this.colsize - tmpCols; i++) {
                        rowlist.add(rowlist.size(), "");
                    }
                }
                try {
                    operateRows(sheetIndex, rowlist);
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (currentRow == this.titleRow) {
                    this.colsize = rowlist.size();
                }
                rowlist.clear();
                currentRow++;
                currentCol = 0;
                preCol = 0;
            }
        }
    }

    /**
     * The sub-type which extends this type should implement this method and do with data in a row
     *
     * @param sheetIndex the index of sheet
     * @param rowlist the info of row
     * @throws SQLException the SQLException
     * @throws DataFormatException the DataFormatException
     */
    public void operateRows(int sheetIndex, List<String> rowlist)
            throws SQLException, DataFormatException {
        // do nothing
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
        lastContents += new String(ch, start, length);
    }

    /**
     * Get the column index
     *
     * @param rowStr the string
     * @return int
     */
    public int getColumnIndex(String rowStr) {
        String columnStr = rowStr.replaceAll("[^A-Z]", "");
        byte[] rowAbc = columnStr.getBytes();
        int len = rowAbc.length;
        float num = 0;
        for (int i = 0; i < len; i++) {
            num += (rowAbc[i] - 'A' + 1) * Math.pow(26, len - i - 1);
        }
        return (int) num;
    }

    /**
     * Get the title row
     *
     * @return the titleRow
     */
    public int getTitleRow() {
        return titleRow;
    }

    /**
     * Set the title row
     *
     * @param titleRow the titleRow to set
     */
    public void setTitleRow(int titleRow) {
        this.titleRow = titleRow;
    }

    /**
     * Get the the number of all rows
     *
     * @return the allRowNum
     */
    public int getAllRowNum() {
        return allRowNum;
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
}
