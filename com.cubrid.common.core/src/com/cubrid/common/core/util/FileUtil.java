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
package com.cubrid.common.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.UUID;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.slf4j.Logger;

/**
 * General filename and filepath manipulation utilities.
 *
 * @author robin
 * @version 1.0 - 2009-6-4 created by robin
 */
public final class FileUtil {
    private static final Logger LOGGER = LogUtil.getLogger(FileUtil.class);

    public static final String CHARSET_UTF8 = "UTF-8";

    /** The Unix separator character. */
    private static final char UNIX_SEPARATOR = '/';

    /** The Windows separator character. */
    private static final char WINDOWS_SEPARATOR = '\\';

    /** Instances should NOT be constructed in standard programming. */
    private FileUtil() {}

    /**
     * Converts all separators to the Unix separator of forward slash.
     *
     * @param path the path to be changed, null ignored
     * @return the updated path
     */
    public static String separatorsToUnix(String path) {
        if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
    }

    /**
     * Converts all separators to the Windows separator of backslash.
     *
     * @param path the path to be changed, null ignored
     * @return the updated path
     */
    public static String separatorsToWindows(String path) {
        if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
            return path;
        }
        return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
    }

    /**
     * Converts all separators to the Windows separator of backslash.
     *
     * @param path the path to be changed, null ignored
     * @param os OsInfoType
     * @return the updated path
     */
    public static String changeSeparatorByOS(String path, OsInfoType os) {
        if (os == OsInfoType.NT) {
            if (path == null || path.indexOf(UNIX_SEPARATOR) == -1) {
                return path;
            }
            return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
        }
        return path;
    }

    /**
     * Get the separator
     *
     * @param os OsInfoType
     * @return String
     */
    public static String getSeparator(OsInfoType os) {
        return os == OsInfoType.NT ? "\\" : "/";
    }

    /**
     * This enum indicates the four type of OS
     *
     * @author sq
     * @version 1.0 - 2009-12-29 created by sq
     */
    public enum OsInfoType {
        NT("NT"),
        LINUX("LINUX"),
        UNIX("UNIX"),
        HPUX("HPUX"),
        AIX("AIX"),
        UNKNOWN("UNKNOWN");

        String text = null;

        OsInfoType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        /**
         * Get the instance of OsInfoType whose text is equals the the given string
         *
         * @param text String
         * @return OsInfoType
         */
        public static OsInfoType eval(String text) {
            if (NT.getText().equalsIgnoreCase(text)) {
                return NT;
            } else if (LINUX.getText().equalsIgnoreCase(text)) {
                return LINUX;
            } else if (UNIX.getText().equalsIgnoreCase(text)) {
                return UNIX;
            } else if (HPUX.getText().equalsIgnoreCase(text)) {
                return HPUX;
            } else if (AIX.getText().equalsIgnoreCase(text)) {
                return AIX;
            } else {
                return UNKNOWN;
            }
        }
    }

    public static boolean writeToFile(String filepath, String data, String fileCharset) {
        return writeToFile(filepath, data, fileCharset, false);
    }

    public static boolean writeToFile(
            String filepath, String data, String fileCharset, boolean append) {
        BufferedWriter fs = null;
        try {
            if (append && !new File(filepath).exists()) {
                append = false;
            }

            if (fileCharset != null && fileCharset.trim().length() > 0) {
                fs =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(filepath, append),
                                        fileCharset.trim()));
            } else {
                fs =
                        new BufferedWriter(
                                new OutputStreamWriter(new FileOutputStream(filepath, append)));
            }

            fs.write(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                fs.close();
            } catch (IOException ignored) {
            }
        }

        return true;
    }

    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            reader = null;
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void close(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            writer = null;
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void close(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            is = null;
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void close(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            os = null;
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static boolean delete(String filepath) {
        if (StringUtil.isEmpty(filepath)) {
            return false;
        }

        return new File(filepath).delete();
    }

    /**
     * Read text from a file.
     *
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readData(String path, String encoding) throws IOException {
        BufferedReader reader = null;
        StringBuilder buf = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
            String row = null;
            while ((row = reader.readLine()) != null) {
                buf.append(row).append(StringUtil.NEWLINE);
            }
        } finally {
            FileUtil.close(reader);
        }

        return buf.toString();
    }

    public static byte[] readBinaryData(File file) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                bis.close();
            } catch (Exception ignored) {
            }
        }
        return bos.toByteArray();
    }

    /**
     * Export the data
     *
     * @param sourceFilePath String
     * @param sourceFileCharset String
     * @param destFilePath String
     * @param destFileCharset String
     * @throws IOException
     */
    public static void exportData(
            String sourceFilePath,
            String sourceFileCharset,
            String destFilePath,
            String destFileCharset)
            throws IOException {

        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(destFilePath, true), destFileCharset));

            reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(sourceFilePath), sourceFileCharset));

            CharBuffer charBuffer = CharBuffer.allocate(512);
            while (reader.read(charBuffer) > 0) {
                charBuffer.flip();
                writer.write(charBuffer.toString());
                charBuffer.clear();
            }

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /**
     * Export the data
     *
     * @param errorMsg String
     * @param destFilePath String
     * @param destFileCharset String
     * @throws IOException
     */
    public static void exportData(String data, String destFilePath, String destFileCharset)
            throws IOException {

        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {
            writer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(destFilePath, true), destFileCharset));
            writer.write(data);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /**
     * Get Buffered Writer
     *
     * @return BufferedWriter
     * @throws UnsupportedEncodingException if failed
     * @throws FileNotFoundException if failed
     */
    public static BufferedWriter getBufferedWriter(String filePath, String charset)
            throws UnsupportedEncodingException, FileNotFoundException {
        BufferedWriter fs = null;
        if (StringUtil.isNotEmpty(charset)) {
            fs =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(new File(filePath)), charset.trim()));
        } else {
            fs =
                    new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(new File(filePath))));
        }
        return fs;
    }

    /**
     * Return the default temporary data file directory
     *
     * @return String the temporary file path
     */
    public static synchronized String getDefaultTempDataFilePath() {
        Location instanceLoc = Platform.getInstanceLocation();
        URL url = instanceLoc.getURL();
        File file = new File(url.getFile());
        String tmpDir = file.getAbsolutePath() + File.separator + "tempdata";
        File tmpFile = new File(tmpDir);
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
        return tmpDir;
    }

    /**
     * Get the temporary file name
     *
     * @return String
     */
    public static String getOnlyTemporaryFile(String tempFileDir) {
        return tempFileDir + File.separator + UUID.randomUUID().toString();
    }
}
