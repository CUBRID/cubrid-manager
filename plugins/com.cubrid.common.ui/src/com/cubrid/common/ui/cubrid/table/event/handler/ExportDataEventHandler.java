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
package com.cubrid.common.ui.cubrid.table.event.handler;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.event.ExportDataBeginOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataFinishOneTableEvent;
import com.cubrid.common.ui.cubrid.table.event.ExportDataSuccessEvent;
import com.cubrid.common.ui.cubrid.table.progress.IExportDataMonitor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;

/**
 * The Export Data Event Handler.
 *
 * @author Kevin.Wang
 */
public class ExportDataEventHandler implements IExportDataEventHandler {
    private static final Logger LOGGER = LogUtil.getLogger(ExportDataEventHandler.class);
    private final ExecutorService handlerExecutor = Executors.newFixedThreadPool(1);
    private final IExportDataMonitor exportDataMonitor;
    private ExportDataFinishAllTableEvent allFinish;

    public ExportDataEventHandler(IExportDataMonitor exportDataMonitor) {
        this.exportDataMonitor = exportDataMonitor;
    }

    public void handleEvent(ExportDataEvent event) {
        handlerExecutor.execute(new EventHandlerRunnable(event));
    }

    public void dispose() {
        exportDataMonitor.finished();
        handlerExecutor.shutdown();
    }

    protected class EventHandlerRunnable implements Runnable {
        private final ExportDataEvent event;

        public EventHandlerRunnable(ExportDataEvent event) {
            this.event = event;
        }

        public void run() {
            try {
                // After finished event, new event will not be accepted.
                if (allFinish != null) {
                    LOGGER.info("", event);
                    return;
                }

                if (event instanceof ExportDataFinishAllTableEvent) {
                    // Only receives the first MigrationFinishedEvent.
                    allFinish = (ExportDataFinishAllTableEvent) event;
                    exportDataMonitor.addEvent(event);
                    dispose();
                } else if (event instanceof ExportDataSuccessEvent
                        || event instanceof ExportDataBeginOneTableEvent
                        || event instanceof ExportDataFinishOneTableEvent) {
                    exportDataMonitor.addEvent(event);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
