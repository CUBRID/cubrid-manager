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
package com.cubrid.cubridquery.ui.common.navigator;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DatabaseEditorConfig;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridquery.ui.CubridQueryUIPlugin;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * CUBIRD Query navigator treeviewer label provider
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class NavigatorTreeLabelProvider extends LabelProvider
        implements IStyledLabelProvider, IColorProvider {

    public Image getImage(Object element) {
        String iconPath = "";
        if (element instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) element;
            if (database.getRunningType() == DbRunningType.CS && database.isLogined()) {
                iconPath = database.getStartAndLoginIconPath();
            } else {
                iconPath = database.getStartAndLogoutIconPath();
            }
        } else if (element instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) element;
            iconPath = node.getIconPath();
        }
        if (iconPath != null && iconPath.length() > 0) {
            return CubridQueryUIPlugin.getImage(iconPath.trim());
        }
        return super.getImage(element);
    }

    public String getText(Object element) {
        if (element instanceof ICubridNode) {
            if (element instanceof CubridDatabase) {
                CubridDatabase database = (CubridDatabase) element;
                StringBuffer sbLabel = new StringBuffer();
                sbLabel.append(((ICubridNode) element).getLabel());
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
                if (editorConfig != null
                        && editorConfig.getDatabaseComment() != null
                        && editorConfig.getDatabaseComment().length() > 0) {
                    sbLabel.append("(").append(editorConfig.getDatabaseComment()).append(")");

                    // [TOOLS-2425]Support shard broker
                    DatabaseInfo dbInfo = database.getDatabaseInfo();
                    if (dbInfo != null && dbInfo.isShard()) {
                        if (dbInfo.getShardQueryType() == DatabaseInfo.SHARD_QUERY_TYPE_ID) {
                            sbLabel.append("[SHARD-ID:")
                                    .append(dbInfo.getCurrentShardId())
                                    .append("]");
                        } else {
                            sbLabel.append("[SHARD-VAL:")
                                    .append(dbInfo.getCurrentShardVal())
                                    .append("]");
                        }
                    }

                    return sbLabel.toString();
                }
            }
            return ((ICubridNode) element).getLabel();
        } else if (element instanceof PendingUpdateAdapter) {
            return Messages.msgLoading;
        }
        return element == null ? "" : element.toString();
    }

    public Color getForeground(Object element) {
        return ResourceManager.getColor(SWT.COLOR_BLACK);
    }

    public Color getBackground(Object element) {
        if (element instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) element;
            if (database != null) {
                DatabaseEditorConfig editorConfig = QueryOptions.getEditorConfig(database, false);
                if (editorConfig != null && editorConfig.getBackGround() != null) {
                    RGB rgb = editorConfig.getBackGround();
                    return ResourceManager.getColor(EditorConstance.convertDeepBackground(rgb));
                }
            }
        }
        return null;
    }

    public StyledString getStyledText(Object element) {
        String text = getText(element);
        StyledString styledString = new StyledString(text);

        return styledString;
    }
}
