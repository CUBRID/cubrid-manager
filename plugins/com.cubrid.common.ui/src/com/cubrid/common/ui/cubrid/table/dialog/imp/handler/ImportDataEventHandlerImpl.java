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
package com.cubrid.common.ui.cubrid.table.dialog.imp.handler;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.event.ImportDataFinishAllTableEvent;
import com.cubrid.common.ui.cubrid.table.dialog.imp.progress.IImportDataMonitor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;

/**
 * The Import Data Event Handler.
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-9 created by Kevin.Wang
 */
public class ImportDataEventHandlerImpl implements ImportDataEventHandler {
    private static final Logger LOGGER = LogUtil.getLogger(ImportDataEventHandlerImpl.class);
    private final IImportDataMonitor importDataMonitor;
    private final ExecutorService handlerExecutor = Executors.newFixedThreadPool(1);
    private ImportDataFinishAllTableEvent allFinish;

    public ImportDataEventHandlerImpl(IImportDataMonitor exportDataMonitor) {
        this.importDataMonitor = exportDataMonitor;
    }

    public void handleEvent(ImportDataEvent event) {
        handlerExecutor.execute(new EventHandlerRunnable(event));
    }

    public void dispose() {
        importDataMonitor.finished();
        handlerExecutor.shutdown();
    }

    protected class EventHandlerRunnable implements Runnable {
        private final ImportDataEvent event;

        public EventHandlerRunnable(ImportDataEvent event) {
            this.event = event;
        }

        public void run() {
            try {
                // After finished event, new event will not be accepted.
                if (allFinish != null) {
                    return;
                }

                if (event instanceof ImportDataFinishAllTableEvent) {
                    // Only receives the first FinishAllEvent.
                    allFinish = (ImportDataFinishAllTableEvent) event;
                    importDataMonitor.addEvent(event);
                } else if (event instanceof ImportDataEvent) {
                    importDataMonitor.addEvent(event);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
