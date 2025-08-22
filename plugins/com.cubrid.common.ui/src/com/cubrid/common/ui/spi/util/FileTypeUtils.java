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
package com.cubrid.common.ui.spi.util;

import com.cubrid.cubridmanager.core.cubrid.table.model.DBAttrTypeFormatter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * File type utility can identify the file type by the file path or bytes
 *
 * @author pangqiren
 * @version 1.0 - 2013-6-7 created by pangqiren
 */
public class FileTypeUtils {

    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    static {
        FILE_TYPE_MAP.put("FFD8FF", "jpg"); // JPEG
        FILE_TYPE_MAP.put("89504E47", "png"); // PNG
        FILE_TYPE_MAP.put("47494638", "gif"); // GIF
        FILE_TYPE_MAP.put("49492A00", "tif"); // TIFF
        FILE_TYPE_MAP.put("424D", "bmp"); // Windows Bitmap

        FILE_TYPE_MAP.put("41433130", "dwg"); // CAD
        FILE_TYPE_MAP.put("38425053", "psd"); // Adobe Photoshop
        FILE_TYPE_MAP.put("7B5C727466", "rtf"); // Rich Text Format
        FILE_TYPE_MAP.put("3C3F786D6C", "xml"); // XML
        FILE_TYPE_MAP.put("68746D6C3E", "html"); // HTML
        FILE_TYPE_MAP.put("44656C69766572792D646174653A", "eml"); // Email

        FILE_TYPE_MAP.put("CFAD12FEC5FD746F", "dbx"); // Outlook Express
        FILE_TYPE_MAP.put("2142444E  ", "pst"); // Outlook
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb"); // MS Access
        FILE_TYPE_MAP.put("FF575043", "wpd"); // WordPerfect
        FILE_TYPE_MAP.put("252150532D41646F6265", "ps"); // Postscript - .eps or .ps
        FILE_TYPE_MAP.put("AC9EBD8F", "qdb"); // Quicken
        FILE_TYPE_MAP.put("E3828596", "pwl"); // Windows Password

        FILE_TYPE_MAP.put("D0CF11E0", "doc"); // MS Word/Excel
        FILE_TYPE_MAP.put("504B0304", "docx");
        FILE_TYPE_MAP.put("255044462D312E", "pdf"); // Adobe Acrobat

        FILE_TYPE_MAP.put("52617221", "rar"); // RAR Archive
        FILE_TYPE_MAP.put("1F8B08", "gz");
        FILE_TYPE_MAP.put("504B0304", "zip"); // ZIP Archive

        FILE_TYPE_MAP.put("57415645", "wav"); // Wave
        FILE_TYPE_MAP.put("41564920", "avi"); // AVI
        FILE_TYPE_MAP.put("2E7261FD ", "ram"); // Real Audio
        FILE_TYPE_MAP.put("2E524D46", "rm"); // Real Media
        FILE_TYPE_MAP.put("000001BA", "mpg"); // MPEG
        FILE_TYPE_MAP.put("000001B3", "mpg"); // MPEG
        FILE_TYPE_MAP.put("6D6F6F76", "mov"); // Quicktime
        FILE_TYPE_MAP.put("3026B2758E66CF11", "asf"); // Windows Media
        FILE_TYPE_MAP.put("4D546864", "mid"); // MIDI
    }

    /**
     * Get the file type of the specified file
     *
     * @param filePath String
     * @return String
     */
    public static String getFileType(String filePath) {
        List<Integer> headerLenthList = getHeaderLengthList();
        int maxLength = headerLenthList.get(headerLenthList.size() - 1);
        byte[] bytes = getFileHeader(filePath, maxLength);
        return getFileType(bytes, headerLenthList);
    }

    /**
     * Return this file extension whether is image file type
     *
     * @param fileExt String
     * @return boolean
     */
    public static boolean isImage(String fileExt) {
        String[] exts = {"jpg", "png", "gif", "tif", "bmp"};
        for (String ext : exts) {
            if (ext.equalsIgnoreCase(fileExt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the file type by byte header
     *
     * @param bytes byte[]
     * @return String
     */
    public static String getFileType(byte[] bytes) {
        List<Integer> headerLenthList = getHeaderLengthList();
        return getFileType(bytes, headerLenthList);
    }

    /**
     * Get the length list of file types
     *
     * @return List<Integer>
     */
    private static List<Integer> getHeaderLengthList() {
        List<Integer> headerLenthList = new ArrayList<Integer>();
        Iterator<String> it = FILE_TYPE_MAP.keySet().iterator();
        while (it.hasNext()) {
            int length = it.next().length() / 2;
            if (!headerLenthList.contains(length)) {
                headerLenthList.add(length);
            }
        }
        Collections.sort(headerLenthList);
        return headerLenthList;
    }

    /**
     * Get the file type
     *
     * @param bytes byte[]
     * @param headerLenthList List<Integer>
     * @return String
     */
    private static String getFileType(byte[] bytes, List<Integer> headerLenthList) {
        if (bytes == null) {
            return null;
        }
        for (int headerLength : headerLenthList) {
            if (headerLength > bytes.length) {
                continue;
            }
            byte[] newBytes = new byte[headerLength];
            System.arraycopy(bytes, 0, newBytes, 0, headerLength);
            String header = DBAttrTypeFormatter.getHexString(newBytes).toUpperCase();

            String fileType = FILE_TYPE_MAP.get(header);
            if (fileType != null && fileType.length() > 0) {
                return fileType;
            }
        }
        return null;
    }

    /**
     * Get the file header
     *
     * @param filePath String
     * @return byte[]
     */
    private static byte[] getFileHeader(String filePath, int length) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filePath);
            byte[] bytes = new byte[length];
            in.read(bytes, 0, bytes.length);
            return bytes;
        } catch (Exception ex) {
            // ignore the exception
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore the exception
                }
            }
        }
        return null;
    }
}
