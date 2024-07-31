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
package com.cubrid.common.ui.cubrid.table.importhandler;

import com.cubrid.common.ui.cubrid.table.dialog.imp.ImportConfig;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.CSVImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.TxtImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSImportFileHandler;
import com.cubrid.common.ui.cubrid.table.importhandler.handler.XLSXImportFileHandler;
import java.util.Locale;

/**
 * Import Handler Factory.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-3-22 created by Kevin Cao
 */
public final class ImportFileHandlerFactory {

    private ImportFileHandlerFactory() {}

    /**
     * Get the a import handler instance by file's extended name. xls,xlsx,csv supported.
     *
     * @param fileName of the import file
     * @param fileCharset String
     * @return ImportFileHandler
     */
    public static ImportFileHandler getHandler(String fileName, ImportConfig importConfig) {
        String lowerCase = fileName.toLowerCase(Locale.getDefault());
        int importType = importConfig.getImportType();

        if (importType == ImportConfig.IMPORT_FROM_EXCEL) {
            //			if (lowerCase.endsWith(".xlsx")) {
            //				return new XLSXImportFileHandler(fileName);
            //			} else
            if (lowerCase.endsWith(".xls")) {
                return new XLSImportFileHandler(fileName, importConfig.getFilesCharset());
            } else if (lowerCase.endsWith(".csv")) {
                return new CSVImportFileHandler(fileName, importConfig.getFilesCharset());
            }
        } else if (importType == ImportConfig.IMPORT_FROM_TXT) {
            return new TxtImportFileHandler(
                    fileName,
                    importConfig.getFilesCharset(),
                    importConfig.getColumnDelimiter(),
                    importConfig.getRowDelimiter());
        } else if (importType == ImportConfig.IMPORT_FROM_SQL) {

        }
        throw new RuntimeException("Not supported file type.");
    }

    /**
     * Get the a import handler instance by file's extended name. xls,xlsx,csv supported.
     *
     * @param fileName of the import file
     * @param fileCharset String
     * @return ImportFileHandler
     */
    public static ImportFileHandler getHandler(String fileName, String fileCharset) {
        String lowerCase = fileName.toLowerCase(Locale.getDefault());
        if (lowerCase.endsWith(".xlsx")) {
            return new XLSXImportFileHandler(fileName);
        } else if (lowerCase.endsWith(".xls")) {
            return new XLSImportFileHandler(fileName, fileCharset);
        } else if (lowerCase.endsWith(".csv")) {
            return new CSVImportFileHandler(fileName, fileCharset);
        }

        throw new RuntimeException("Not supported file type.");
    }

    public static ImportFileHandler getHandler(
            String fileName, String fileCharset, String separator) {
        String lowerCase = fileName.toLowerCase(Locale.getDefault());
        if (lowerCase.endsWith(".xlsx")) {
            return new XLSXImportFileHandler(fileName);
        } else if (lowerCase.endsWith(".xls")) {
            return new XLSImportFileHandler(fileName, fileCharset);
        } else if (lowerCase.endsWith(".csv")) {
            return new CSVImportFileHandler(fileName, fileCharset);
        } else if (lowerCase.endsWith(".txt")) {
            return new TxtImportFileHandler(fileName, fileCharset, separator);
        }

        throw new RuntimeException("Not supported file type.");
    }
}
