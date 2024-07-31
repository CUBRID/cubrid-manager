/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.sqlrunner.part;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * run SQL part input
 *
 * @author Kevin.Wang
 */
public class RunSQLFileEditorInput implements IEditorInput {
    private final List<String> fileList;
    private final CubridDatabase database;

    private int maxThreadSize = 1;
    private int commitCount = 1000;
    private String charset = "UTF-8";
    private String logFolderPath = Platform.getInstanceLocation().getURL().getPath();

    /**
     * The constructor
     *
     * @param database
     * @param fileList
     * @param charset
     * @param maxThreadSize
     * @param commitCount
     * @param logFolderPath
     */
    public RunSQLFileEditorInput(
            CubridDatabase database,
            List<String> fileList,
            String charset,
            int maxThreadSize,
            int commitCount,
            String logFolderPath) {
        this.database = database;
        this.fileList = fileList;
        this.charset = charset;
        this.maxThreadSize = maxThreadSize;
        this.commitCount = commitCount;
        if (!StringUtil.isEmpty(logFolderPath)) {
            this.logFolderPath = logFolderPath;
        }
    }

    /**
     * The constructor
     *
     * @param database
     * @param fileList
     */
    public RunSQLFileEditorInput(CubridDatabase database, List<String> fileList) {
        this.database = database;
        this.fileList = fileList;
    }

    public boolean exists() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        if (dbInfo == null) {
            return "";
        }
        return database.getName() + "@" + dbInfo.getBrokerIP();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return this.getName();
    }

    public CubridDatabase getDatabase() {
        return database;
    }

    /**
     * Get Max Thread Size
     *
     * @return the maxThreadSize
     */
    public int getMaxThreadSize() {
        return maxThreadSize;
    }

    /**
     * Get Commit Count
     *
     * @return the commitCount
     */
    public int getCommitCount() {
        return commitCount;
    }

    /**
     * Get Charset
     *
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Get Log Folder Path
     *
     * @return the logFolderPath
     */
    public String getLogFolderPath() {
        return logFolderPath;
    }

    /**
     * Get file List
     *
     * @return the fileList
     */
    public List<String> getFileList() {
        return fileList;
    }
}
