/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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

import com.cubrid.common.core.util.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This type is responsible for writing data to excel 2007 format file.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-11-17 created by lizhiqiang
 */
public class XlsxWriterHelper { // FIXME move this logic to core module
    /**
     * This type is responsible for writing data to a sheet in excel 2007
     *
     * @author lizhiqiang
     * @version 1.0 - 2010-11-17 created by lizhiqiang
     */
    public static class SpreadsheetWriter {
        private final Writer out;
        private int rownum;
        private String charset = "UTF-8";

        /**
         * Constructor
         *
         * @param out the instance of Writer
         */
        public SpreadsheetWriter(Writer out) {
            this.out = out;
        }

        /**
         * Close the stream of Writer
         *
         * @throws IOException the IOException
         */
        public void close() throws IOException {
            if (out != null) {
                out.close();
            }
        }

        /**
         * Writer the header in sheet
         *
         * @throws IOException the instance of IOException
         */
        public void beginSheet() throws IOException {
            out.write(
                    "<?xml version=\"1.0\" encoding=\""
                            + charset
                            + "\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
            out.write("<sheetData>\n");
        }

        /**
         * Writer the end in sheet
         *
         * @throws IOException the instance of IOException
         */
        public void endSheet() throws IOException {
            out.write("</sheetData>");
            out.write("</worksheet>");
        }

        /**
         * writer the start tag of a row
         *
         * @param rownum the row number
         * @throws IOException the instance of IOException
         */
        public void insertRow(int rownum) throws IOException {
            out.write(
                    (new StringBuilder())
                            .append("<row r=\"")
                            .append(rownum + 1)
                            .append("\">\n")
                            .toString());
            this.rownum = rownum;
        }

        /**
         * writer the end tag of a row
         *
         * @throws IOException the instance of IOException
         */
        public void endRow() throws IOException {
            out.write("</row>\n");
        }

        /**
         * writer the cell info
         *
         * @param columnIndex the index of a column
         * @param value the value of a cell
         * @param styleIndex the index of style
         * @throws IOException the instance of IOException
         */
        public void createCell(int columnIndex, String value, int styleIndex) throws IOException {
            String ref = (new CellReference(rownum, columnIndex)).formatAsString();
            out.write(
                    (new StringBuilder())
                            .append("<c r=\"")
                            .append(ref)
                            .append("\" t=\"inlineStr\"")
                            .toString());
            if (styleIndex != -1) {
                out.write(
                        (new StringBuilder())
                                .append(" s=\"")
                                .append(styleIndex)
                                .append("\"")
                                .toString());
            }
            out.write(">");
            out.write(
                    (new StringBuilder())
                            .append("<is><t>")
                            .append(value)
                            .append("</t></is>")
                            .toString());
            out.write("</c>");
        }

        /**
         * writer the cell info
         *
         * @param columnIndex the index of a column
         * @param value the value of a cell
         * @throws IOException the instance of IOException
         */
        public void createCell(int columnIndex, String value) throws IOException {
            createCell(columnIndex, value, -1);
        }

        /**
         * writer the cell info
         *
         * @param columnIndex the index of a column
         * @param value the value of a cell
         * @param styleIndex the index of style
         * @throws IOException the instance of IOException
         */
        public void createCell(int columnIndex, double value, int styleIndex) throws IOException {
            String ref = (new CellReference(rownum, columnIndex)).formatAsString();
            out.write(
                    (new StringBuilder())
                            .append("<c r=\"")
                            .append(ref)
                            .append("\" t=\"n\"")
                            .toString());
            if (styleIndex != -1) {
                out.write(
                        (new StringBuilder())
                                .append(" s=\"")
                                .append(styleIndex)
                                .append("\"")
                                .toString());
            }
            out.write(">");
            out.write((new StringBuilder()).append("<v>").append(value).append("</v>").toString());
            out.write("</c>");
        }

        /**
         * writer the cell info
         *
         * @param columnIndex the index of a column
         * @param value the value of a cell
         * @throws IOException the instance of IOException
         */
        public void createCell(int columnIndex, double value) throws IOException {
            createCell(columnIndex, value, -1);
        }

        /**
         * writer the cell info
         *
         * @param columnIndex the index of a column
         * @param value the value of a cell
         * @param styleIndex the index of style
         * @throws IOException the instance of IOException
         */
        public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException {
            createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
        }

        /** @param charset the charset to set */
        public void setCharset(String charset) {
            this.charset = charset;
        }
    }

    /**
     * create the cell style which is used for the head or date type cell
     *
     * @param workbook the instance of XSSFWorkbook
     * @return Map<String, XSSFCellStyle>
     */
    public Map<String, XSSFCellStyle> getStyles(XSSFWorkbook workbook) {
        Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
        XSSFDataFormat fmt = workbook.createDataFormat();
        XSSFCellStyle datetimeStyle = workbook.createCellStyle();
        datetimeStyle.setAlignment((short) 3);
        datetimeStyle.setDataFormat(fmt.getFormat("yyyy-mmm-dd h:mm:ss.ss"));
        styles.put("datetime", datetimeStyle);

        XSSFCellStyle timestampStyle = workbook.createCellStyle();
        timestampStyle.setAlignment((short) 3);
        timestampStyle.setDataFormat(fmt.getFormat("yyyy-mmm-dd h:mm:ss"));
        styles.put("timestamp", timestampStyle);

        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setAlignment((short) 3);
        dateStyle.setDataFormat(fmt.getFormat("yyyy-mmm-dd"));
        styles.put("date", dateStyle);

        XSSFCellStyle timeStyle = workbook.createCellStyle();
        timeStyle.setAlignment((short) 3);
        timeStyle.setDataFormat(fmt.getFormat("h:mm:ss"));
        styles.put("time", timeStyle);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern((short) 1);
        headerStyle.setFont(headerFont);
        styles.put("header", headerStyle);
        return styles;
    }

    /**
     * Write to the file by fileMap
     *
     * @param zipfile the zip file
     * @param fileMap the instance of Map<String, File>
     * @param out the stream of OutputStream
     * @throws IOException the IOException
     */
    @SuppressWarnings("unchecked")
    public void substitute(File zipfile, Map<String, File> fileMap, OutputStream out)
            throws IOException {
        ZipFile zip = new ZipFile(zipfile);
        ZipOutputStream zos = new ZipOutputStream(out);
        try {
            Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
            while (en.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) en.nextElement();
                boolean isInclude = false;
                for (String key : fileMap.keySet()) {
                    if (zipEntry.getName().equals(key)) {
                        isInclude = true;
                    }
                }
                if (!isInclude) {
                    zos.putNextEntry(new ZipEntry(zipEntry.getName()));
                    InputStream is = zip.getInputStream(zipEntry);
                    try {
                        copyStream(is, zos);
                    } finally {
                        FileUtil.close(is);
                    }
                }
            }
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                String keyRef = entry.getKey();
                File tmpfile = entry.getValue();
                zos.putNextEntry(new ZipEntry(keyRef));
                InputStream is = new FileInputStream(tmpfile);
                try {
                    copyStream(is, zos);
                } finally {
                    FileUtil.close(is);
                }
            }
        } finally {
            FileUtil.close(zos);
        }
    }

    /**
     * copy data from input stream to output stream
     *
     * @param in the instance of InputStream
     * @param out the instance of OutputStream
     * @throws IOException the IOException
     */
    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte chunk[] = new byte[1024];
        int count;
        while ((count = in.read(chunk)) >= 0) {
            out.write(chunk, 0, count);
        }
    }

    /**
     * Write the data to XLSX workbook
     *
     * @param workbook the instance of Workbook
     * @param xlsxWriterhelper the instance of XlsxWriterHelper
     * @param fileMap a map includes the temporary file and its name
     * @param outFile File
     * @throws IOException the exception
     */
    public static void writeWorkbook(
            XSSFWorkbook workbook,
            XlsxWriterHelper xlsxWriterhelper,
            Map<String, File> fileMap,
            File outFile)
            throws IOException {
        String templateFileName = System.nanoTime() + "template.xlsx";
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(templateFileName);
            workbook.write(os);
        } finally {
            FileUtil.close(os);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(outFile);
                if (xlsxWriterhelper != null) {
                    xlsxWriterhelper.substitute(new File(templateFileName), fileMap, out);
                }
            } finally {
                FileUtil.close(out);
                deleteTempFiles(fileMap, templateFileName);
            }
        }
    }

    private static void deleteTempFiles(Map<String, File> fileMap, String templateFileName) {
        boolean isSuccess = true;
        Iterator<File> fileIt = fileMap.values().iterator();
        while (fileIt.hasNext()) {
            File file = fileIt.next();
            isSuccess = file.delete();
        }
        File file = new File(templateFileName);
        try {
            if (file.exists() && isSuccess) {
                isSuccess = file.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write the data to sheet
     *
     * @param sheetWriter the instance of XlsxWriterHelper.SpreadsheetWriter
     * @throws IOException The exception
     */
    public static void writeSheetWriter(XlsxWriterHelper.SpreadsheetWriter sheetWriter)
            throws IOException {
        try {
            if (sheetWriter != null) {
                sheetWriter.endSheet();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (sheetWriter != null) {
                    sheetWriter.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }
}
