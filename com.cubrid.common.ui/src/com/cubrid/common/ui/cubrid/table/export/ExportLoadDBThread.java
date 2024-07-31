/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.export;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFailedOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.handler.IExportDataEventHandler;
import com.cubrid.common.ui.cubrid.table.export.handler.AbsExportDataHandler;
import com.cubrid.common.ui.cubrid.table.export.handler.ExportHandlerFactory;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import org.slf4j.Logger;

/**
 * ExportLoadDBThread Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-5-27 created by Kevin.Wang
 */
public class ExportLoadDBThread extends AbsExportThread {

    private static final Logger LOGGER = LogUtil.getLogger(ExportLoadDBThread.class);

    protected final DatabaseInfo dbInfo;
    protected final ExportConfig exportConfig;
    protected final IExportDataEventHandler exportDataEventHandler;
    private AbsExportDataHandler handler;

    public ExportLoadDBThread(
            DatabaseInfo dbInfo,
            ExportConfig exportConfig,
            IExportDataEventHandler exportDataEventHandler,
            IJobListener jobListener) {
        super(jobListener);
        this.dbInfo = dbInfo;
        this.exportConfig = exportConfig;
        this.exportDataEventHandler = exportDataEventHandler;
    }

    protected void doRun() {
        exportDataEventHandler.handleEvent(
                new ExportDataBeginOneTableEvent(
                        exportConfig.getDataFilePath(ExportConfig.LOADDB_DATAFILEKEY)));
        try {
            AbsExportDataHandler handler =
                    ExportHandlerFactory.getExportHandler(
                            dbInfo, exportConfig, exportDataEventHandler);
            handler.handle(null);
        } catch (Exception e) {
            isSuccess = false;
            LOGGER.error("", e);
        } catch (OutOfMemoryError error) {
            isSuccess = false;
            error.printStackTrace();
        } finally {
            if (isSuccess) {
                exportDataEventHandler.handleEvent(
                        new ExportDataFinishOneTableEvent(
                                exportConfig.getDataFilePath(ExportConfig.LOADDB_DATAFILEKEY)));
            } else {
                exportDataEventHandler.handleEvent(
                        new ExportDataFailedOneTableEvent(
                                exportConfig.getDataFilePath(ExportConfig.LOADDB_DATAFILEKEY)));
            }
        }
    }

    public void performStop() {
        if (!isFinished) {
            if (handler != null) {
                handler.setStop(true);
            }
            isStoped = true;
        }
    }
}
