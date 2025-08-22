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
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

/**
 * This type is responsible for response to the user click and shift the state with different icon.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-8-18 created by lizhiqiang
 */
public final class RecordAction extends Action {
    private boolean recordFlag;
    private String recordTooltip;
    private String prepareTooltip;
    private Recordable recorder;

    /**
     * Shifting the recordFlag and return the the interface of Recordable.
     *
     * @param event the SWT event which triggered this action being run
     */
    public void runWithEvent(final Event event) {
        recordFlag = recorder.getRecordFlag();
        recordFlag = !recordFlag;
        recorder.setRecordFlag(recordFlag);
        final Display display = Display.getDefault();
        Runnable runnable =
                new Runnable() {
                    boolean state = false;

                    public void run() {
                        if (state) {
                            ((ToolItem) (event.widget))
                                    .setImage(
                                            CubridManagerUIPlugin.getImage(
                                                    "icons/monitor/record_bright.png"));
                        } else {
                            ((ToolItem) (event.widget))
                                    .setImage(
                                            CubridManagerUIPlugin.getImage(
                                                    "icons/monitor/record_dim.png"));
                        }
                        state = !state;
                        if (recordFlag) {
                            display.timerExec(800, this);
                        } else {
                            display.timerExec(-1, this);
                            ((ToolItem) (event.widget))
                                    .setImage(
                                            CubridManagerUIPlugin.getImage(
                                                    "icons/monitor/prepare_record.png"));
                        }
                    }
                };
        if (recordFlag) {
            this.setImageDescriptor(
                    CubridManagerUIPlugin.getImageDescriptor("icons/monitor/record_bright.png"));
            this.setToolTipText(recordTooltip);
            display.timerExec(20, runnable);
        } else {
            this.setImageDescriptor(
                    CubridManagerUIPlugin.getImageDescriptor("icons/monitor/prepare_record.png"));
            this.setToolTipText(prepareTooltip);
        }
    }

    /**
     * Get the recordTooltip
     *
     * @return the recordTooltip
     */
    public String getRecordTooltip() {
        return recordTooltip;
    }

    /** @param recordTooltip the recordTooltip to set */
    public void setRecordTooltip(String recordTooltip) {
        this.recordTooltip = recordTooltip;
    }

    /**
     * Get the prepareTooltip
     *
     * @return the prepareTooltip
     */
    public String getPrepareTooltip() {
        return prepareTooltip;
    }

    /** @param prepareTooltip the prepareTooltip to set */
    public void setPrepareTooltip(String prepareTooltip) {
        this.prepareTooltip = prepareTooltip;
    }

    /** @param recorder the recorder to set */
    public void setRecorder(Recordable recorder) {
        this.recorder = recorder;
    }
}
