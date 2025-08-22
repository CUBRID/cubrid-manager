/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of the <ORGANIZATION> nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.cubrid.common.core.reader;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.SetupEnvTestCase;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author fulei
 * @version 1.0 - 2012-12-21 created by fulei
 */
public class TxtReaderTest extends SetupEnvTestCase {
    private File file1;
    private File file2;

    protected void setUp() {
        StringBuilder sb = new StringBuilder();
        sb.append("20421,Wrestling,Greco-Roman 97kg,M,1");
        sb.append(StringUtil.NEWLINE);
        sb.append("20420,Wrestling,Greco-Roman 96kg,M,1");
        sb.append(StringUtil.NEWLINE);
        sb.append("20419,Wrestling,Greco-Roman 90kg,M,1");
        sb.append(StringUtil.NEWLINE);
        sb.append("20418,Wrestling,Greco-Roman -90 kg,M,1");
        sb.append(StringUtil.NEWLINE);
        sb.append("20417,Wrestling,Greco-Roman 85kg,M,1");
        BufferedWriter fs = null;

        file1 = new File("file1");
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

        sb = new StringBuilder();
        sb.append(
                "20421,Wrestling,Greco-Roman 97kg,M,1;20420,Wrestling,Greco-Roman 96kg,M,1;20419,Wrestling,Greco-Roman 90kg,M,1;20418,Wrestling,Greco-Roman -90 kg,M,1;20417,Wrestling,Greco-Roman 85kg,M,1;");
        sb.append(StringUtil.NEWLINE);
        sb.append(
                "20421,Wrestling,Greco-Roman 97kg,M,1;20420,Wrestling,Greco-Roman 96kg,M,1;20419,Wrestling,Greco-Roman 90kg,M,1;20418,Wrestling,Greco-Roman -90 kg,M,1;20417,Wrestling,Greco-Roman 85kg,M,1");
        sb.append(StringUtil.NEWLINE);
        sb.append(
                "20421,Wrestling,Greco-Roman 97kg,M,1;20420,Wrestling,Greco-Roman 96kg,M,1;20419,Wrestling,Greco-Roman 90kg,M,1;20418,Wrestling,Greco-Roman -90 kg,M,1;20417,Wrestling,Greco-Roman 85kg,M,1");
        sb.append(
                "20421,Wrestling,Greco-Roman 97kg,M,1;20420,Wrestling,Greco-Roman 96kg,M,1;20419,Wrestling,Greco");
        sb.append(StringUtil.NEWLINE);
        sb.append(
                "-Roman 90kg,M,1;20418,Wrestling,Greco-Roman -90 kg,M,1;20417,Wrestling,Greco-Roman 85kg,M,1; 85kg,M,1");
        file2 = new File("file2");
        try {
            fs = getBufferedWriter(file2);
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

        if (file2 != null && file2.exists()) {
            file2.delete();
        }
    }

    public void testTxtReader() {
        TxtReader txtReader = null;
        try {
            txtReader = new TxtReader(new FileReader(file1), ",", StringUtil.NEWLINE);

            String[] txtLine = txtReader.readNext();
            assertTrue(txtLine.length > 0);

            String[] txtRow = txtReader.readNextRow();
            assertTrue(txtRow.length > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            txtReader = new TxtReader(new FileReader(file1), ",", StringUtil.NEWLINE);
            assertTrue(txtReader.readAll().size() == 5);
            String[] txtRow = txtReader.readNextRow();
            assertNull(txtRow);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            txtReader = new TxtReader(new FileReader(file2), ",", ";");

            String[] txtLine = txtReader.readNext();
            assertTrue(txtLine.length > 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            txtReader = new TxtReader(new FileReader(file2), ",", ";");

            String[] txtLine = txtReader.readNextRow();
            assertTrue(txtLine.length > 0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            txtReader = new TxtReader(new FileReader(file2), ",", ";");
            assertTrue(txtReader.readAll().size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }

        try {
            txtReader = new TxtReader(new FileReader(file2), ";");
            assertTrue(txtReader.readNextRow().length > 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txtReader != null) {
                try {
                    txtReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private BufferedWriter getBufferedWriter(File file)
            throws UnsupportedEncodingException, FileNotFoundException {
        BufferedWriter fs = null;
        fs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        return fs;
    }
}
