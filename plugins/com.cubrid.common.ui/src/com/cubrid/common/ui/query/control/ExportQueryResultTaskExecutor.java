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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.table.CellValue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import jxl.write.WriteException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;

/**
 * A task executor for export query result
 *
 * @author wangsl 2009-6-22
 */
public class ExportQueryResultTaskExecutor extends TaskExecutor {

    private static final Logger LOGGER = LogUtil.getLogger(ExportQueryResultTaskExecutor.class);
    private final Export export;

    /**
     * The constructor
     *
     * @param file
     * @param fileCharset
     * @param allColumnList
     * @param queryResultVector
     */
    public ExportQueryResultTaskExecutor(
            File file, String fileCharset, Vector<QueryExecuter> queryResultVector) {
        this.export = new Export(file, fileCharset, queryResultVector);
    }

    /**
     * The constructor
     *
     * @param file
     * @param fileCharset
     * @param tblResult
     * @param doesGetOidInfo
     */
    public ExportQueryResultTaskExecutor(
            File file,
            String fileCharset,
            Table tblResult,
            TableItem[] items,
            boolean doesGetOidInfo) {
        this.export = new Export(file, fileCharset, tblResult, items, doesGetOidInfo);
    }

    public ExportQueryResultTaskExecutor(
            File file,
            String fileCharset,
            List<ColumnInfo> allColumnList,
            List<Map<String, CellValue>> allDataList,
            boolean doesGetOidInfo) {
        this.export = new Export(file, fileCharset, allColumnList, allDataList, doesGetOidInfo);
    }

    /**
     * @see
     *     com.cubrid.common.ui.spi.progress.TaskExecutor#exec(org.eclipse.core.runtime.IProgressMonitor)
     * @param monitor the monitor object
     * @return <code>true</code> if it is successfully;<code>false</code> otherwise
     */
    public boolean exec(final IProgressMonitor monitor) {
        monitor.beginTask(Messages.exportDataTaskName, IProgressMonitor.UNKNOWN);
        try {
            export.export(monitor);
        } catch (final Exception e) {
            LOGGER.error("", e);
            monitor.done();
            openErrorBox(null, export.getResultMsg(e), monitor);
            return false;
        }
        monitor.done();
        openInformationgBox(null, Messages.export, export.getResultMsg(null), monitor);
        return true;
    }

    /** @see com.cubrid.common.ui.spi.progress.TaskExecutor#cancel() */
    public void cancel() {
        if (this.export != null) {
            try {
                this.export.cancel();
            } catch (WriteException e) {
                LOGGER.error("", e);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }
}
