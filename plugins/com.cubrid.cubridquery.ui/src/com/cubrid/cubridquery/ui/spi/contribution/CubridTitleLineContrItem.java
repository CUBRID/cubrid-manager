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
package com.cubrid.cubridquery.ui.spi.contribution;

import com.cubrid.common.ui.spi.contribution.TitleLineContrItem;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * CUBRID Query title line contribution item
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-9 created by pangqiren
 */
public class CubridTitleLineContrItem extends TitleLineContrItem {

    /**
     * Get title of CUBRID Query application for navigator
     *
     * @param cubridNode the ICubridNode object
     * @return String
     */
    protected String getTitleForNavigator(ICubridNode cubridNode) {
        if (cubridNode == null) {
            return "";
        }
        StringBuffer titleStrBuffer = new StringBuffer();
        if (cubridNode instanceof ISchemaNode) {
            ISchemaNode schemaNode = (ISchemaNode) cubridNode;
            CubridDatabase database = schemaNode.getDatabase();
            if (database != null) {
                titleStrBuffer.append(database.getLabel() + " / ");
            }
            DatabaseInfo dbInfo = database == null ? null : database.getDatabaseInfo();
            if (dbInfo == null) {
                return "";
            }
            StringBuffer dbInfoStrBuffer = new StringBuffer();
            DbUserInfo dbUserInfo = dbInfo.getAuthLoginedDbUserInfo();
            if (dbUserInfo == null
                    || dbUserInfo.getName() == null
                    || dbUserInfo.getName().trim().length() == 0) {
                dbInfoStrBuffer.append(dbInfo.getDbName());
            } else {
                dbInfoStrBuffer.append(dbUserInfo.getName() + "@" + dbInfo.getDbName());
            }

            String brokerPort = dbInfo.getBrokerPort();
            if (brokerPort != null && brokerPort.trim().length() > 0) {
                dbInfoStrBuffer.append(":").append(brokerPort);
            }

            String charset = dbInfo.getCharSet();
            if (charset != null && charset.trim().length() > 0) {
                dbInfoStrBuffer.append(":charset=").append(charset);
            }
            titleStrBuffer.append(dbInfoStrBuffer);
        } else {
            titleStrBuffer.append(cubridNode.getLabel());
        }
        return titleStrBuffer.toString();
    }

    /**
     * Get title of application for query editor
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getTitleForQueryEditor(ICubridNode cubridNode) {
        return getTitleForNavigator(cubridNode);
    }

    /**
     * Get the title of view or editor(not including query editor)
     *
     * @param cubridNode the ICubridNode object
     * @param workbenchPart the IWorkbenchPart object
     * @return String
     */
    protected String getTitleForViewOrEdit(ICubridNode cubridNode, IWorkbenchPart workbenchPart) {
        StringBuffer titleStrBuffer = new StringBuffer();
        if (cubridNode instanceof ISchemaNode) {
            ISchemaNode schemaNode = (ISchemaNode) cubridNode;
            CubridDatabase database = schemaNode.getDatabase();
            titleStrBuffer.append(database.getLabel());
        }
        String partTitle = "";
        if (workbenchPart == null) {
            if (null != cubridNode.getViewId()) {
                IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, cubridNode.getViewId());
                if (viewPart != null) {
                    partTitle = viewPart.getTitle();
                }
            }
            if (null != cubridNode.getEditorId()) {
                IEditorPart editorPart =
                        LayoutUtil.getEditorPart(cubridNode, cubridNode.getEditorId());
                if (editorPart != null) {
                    partTitle = editorPart.getTitle();
                }
            }
        } else {
            partTitle = workbenchPart.getTitle();
        }

        if (partTitle != null && partTitle.trim().length() > 0) {
            titleStrBuffer.append(" / " + partTitle);
        }
        return titleStrBuffer.toString();
    }
}
