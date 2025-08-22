/*
, * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.core.reader;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * test CSVReaderTest
 *
 * @author wuyingshi
 * @version 1.0 - 2010-1-11 created by wuyingshi
 */
public class CSVReaderTest extends SetupEnvTestCase {
    BufferedReader br;
    boolean hasNext = true;
    char separator = '/';
    char quotechar = ';';
    int skipLines = 1;
    boolean linesSkiped = true;
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    public static final int DEFAULT_SKIP_LINES = 0;
    public static final String STR_NULL = "NULL";
    private File file1;

    public void testCSVReader() {
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(file1));
            reader.readAll();
            reader.close();
        } catch (FileNotFoundException e1) {
        } catch (IOException e2) {
        }
    }

    protected void setUp() {
        StringBuilder sb = new StringBuilder();
        sb.append("10996,\"Fernandez Gigi\",\"W\",\"USA\",\"Tennis\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10995,\"Fernandez Ana Ibis\",\"W\",\"CUB\",\"Volleyball\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10994,\"Fernandez Abelardo\",\"M\",\"ESP\",\"Football\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10993,\"Feri Attila\",\"M\",\"HUN\",\"Weightlifting\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10992,\"Felisiak Robert\",\"M\",\"GER\",\"Fencing\"");
        sb.append(StringUtil.NEWLINE);

        sb.append("10991,\"Feklistova Maria\",\"W\",\"RUS\",\"Shooting\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10990,\"Fei Alessandro\",\"M\",\"ITA\",\"Volleyball\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10989,\"Feeney Carol\",\"W\",\"USA\",\"Rowing\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10988,\"Fedtchouk Andri\",\"M\",\"UKR\",\"Boxing\"");
        sb.append(StringUtil.NEWLINE);
        sb.append("10987,\"Fedotova Irina\",\"W\",\"RUS\",\"Rowing\"");

        BufferedWriter fs = null;

        file1 = new File("testcsv.csv");
        try {
            fs = getBufferedWriter(file1);
            fs.write(sb.toString());
            fs.flush();
        } catch (Exception e) {

        } finally {
            try {
                if (fs != null) {
                    fs.close();
                    fs = null;
                }
            } catch (IOException e) {
            }
        }
    }

    protected void tearDown() {
        if (file1 != null && file1.exists()) {
            file1.delete();
        }
    }

    private BufferedWriter getBufferedWriter(File file)
            throws UnsupportedEncodingException, FileNotFoundException {
        BufferedWriter fs = null;
        fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        return fs;
    }
}
