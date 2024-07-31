/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.common.query.autosave;

import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.control.CombinedQueryEditorComposite;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.RestorableQueryEditorInfo;
import com.cubrid.common.ui.spi.persist.ApplicationPersistUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

/**
 * The Check Query Editor Task
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 5, 2012 created by Kevin.Wang
 */
public class CheckQueryEditorTask
        implements IHeartBeatTask { // FIXME logic code move to core module
    private static final Logger LOGGER = LogUtil.getLogger(CheckQueryEditorTask.class);

    private int EXECUTE_TIME = 10; /*The unit is second*/
    private int count = 0;
    private static CheckQueryEditorTask instance;

    public static CheckQueryEditorTask getInstance() {
        synchronized (HeartBeatTaskManager.class) {
            if (instance == null) {
                instance = new CheckQueryEditorTask();
            }
        }
        return instance;
    }

    private CheckQueryEditorTask() {}

    public static String getQuery(RestorableQueryEditorInfo editorStatus) {
        final DateFormat formater = DateUtil.getDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        if (editorStatus.getQueryContents() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-- [Query Editor Autosave] Host: ").append(editorStatus.getServerName());
        sb.append(", Database: ").append(editorStatus.getDatabaseName());
        sb.append(", Date: ").append(formater.format(editorStatus.getCreatedTime()));
        sb.append(" --").append(StringUtil.NEWLINE).append(StringUtil.NEWLINE);

        String sql = editorStatus.getQueryContents().trim();
        if (sql.startsWith("-- [Query Editor Autosave]")) {
            int endPos = sql.indexOf("\n");
            if (endPos != -1) {
                sql = sql.substring(endPos + 1).trim();
            }
        }
        sb.append(sql);
        return sb.toString();
    }

    public void beat() {
        if (count < EXECUTE_TIME) {
            count++;
        } else {
            doSave();
            count = 0;
        }
    }

    public void stop() {
        ApplicationPersistUtil.getInstance().clearAllEditorStatus();
        ApplicationPersistUtil.getInstance().save();
    }

    public void doSave() {
        ApplicationPersistUtil.getInstance().clearAllEditorStatus();

        Display.getDefault()
                .syncExec(
                        new Runnable() {
                            public void run() {
                                Date createTime = new Date();
                                List<QueryEditorPart> editorPartList =
                                        QueryEditorUtil.getAllQueryEditorPart();
                                for (QueryEditorPart editor : editorPartList) {
                                    ArrayList<RestorableQueryEditorInfo> sqlTabItemList =
                                            new ArrayList<RestorableQueryEditorInfo>();
                                    for (CombinedQueryEditorComposite combinedQueryEditorComposite :
                                            editor.getAllCombinedQueryEditorComposite()) {
                                        StyledText text =
                                                combinedQueryEditorComposite
                                                        .getSqlEditorComp()
                                                        .getText();
                                        if (text == null) {
                                            LOGGER.warn("The editor.getSqlTextEditor() is a null.");
                                            continue;
                                        }
                                        if (StringUtil.isEmpty(text.getText())) {
                                            LOGGER.warn("The text.getText() is a null.");
                                            continue;
                                        }

                                        CubridDatabase cubridDatabase =
                                                editor.getSelectedDatabase();
                                        RestorableQueryEditorInfo editorStatus =
                                                new RestorableQueryEditorInfo();
                                        if (cubridDatabase != null) {
                                            DatabaseInfo dbInfo = cubridDatabase.getDatabaseInfo();
                                            if (dbInfo != null) {
                                                editorStatus.setDatabaseName(dbInfo.getDbName());
                                            }

                                            CubridServer cubridServer = cubridDatabase.getServer();
                                            if (cubridServer != null) {
                                                editorStatus.setServerName(cubridServer.getId());
                                            }
                                        }

                                        editorStatus.setQueryContents(text.getText());
                                        editorStatus.setCreatedTime(createTime);
                                        sqlTabItemList.add(editorStatus);
                                    }
                                    ApplicationPersistUtil.getInstance()
                                            .addEditorStatus(sqlTabItemList);
                                }
                            }
                        });

        ApplicationPersistUtil.getInstance().save();
    }
}
