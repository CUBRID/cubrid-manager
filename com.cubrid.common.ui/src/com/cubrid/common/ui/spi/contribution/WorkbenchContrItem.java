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
package com.cubrid.common.ui.spi.contribution;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.cubrid.table.control.SchemaInfoEditorPart;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.common.ui.spi.util.SQLGenerateUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllAttrTask;
import java.util.List;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * Workbench contribution item is responsible to process the double click navigator node event and
 * cubrid node change event and selection changed event
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-29 created by pangqiren
 */
public class WorkbenchContrItem {
    private static final Logger LOGGER = LogUtil.getLogger(WorkbenchContrItem.class);

    /**
     * Process double click event when double click navigator node
     *
     * @param event DoubleClickEvent
     */
    public void processDoubleClickNavigatorEvent(DoubleClickEvent event) {}

    /**
     * Process CUBRID node selection changed event when CUBRID node selection changd
     *
     * @param event SelectionChangedEvent
     */
    public void processSelectionChanged(SelectionChangedEvent event) {}

    /**
     * Process CUBRID node changed event when the cubrid node changed(refresh,remove) in navigator
     *
     * @param event CubridNodeChangedEvent
     */
    public void processCubridNodeChangeEvent(CubridNodeChangedEvent event) {
        ICubridNode eventNode = event.getCubridNode();
        if (eventNode == null) {
            return;
        }

        IWorkbenchPage page = LayoutUtil.getActivePage();
        if (page == null) {
            return;
        }

        if (event.getType() == CubridNodeChangedEventType.CONTAINER_NODE_REFRESH) {
            processNodeRemoveWhenRefreshContainer(eventNode, page);
        } else if (event.getType() == CubridNodeChangedEventType.NODE_REMOVE) {
            processNodeRemove(eventNode, page);
        } else if (event.getType() == CubridNodeChangedEventType.DATABASE_LOGOUT
                && eventNode instanceof CubridDatabase) {
            closeAllEditorAndViewInDatabase(
                    (CubridDatabase) eventNode, CubridNodeChangedEventType.DATABASE_LOGOUT);
        } else if (event.getType() == CubridNodeChangedEventType.DATABASE_STOP
                && eventNode instanceof CubridDatabase) {
            closeAllEditorAndViewInDatabase(
                    (CubridDatabase) eventNode, CubridNodeChangedEventType.DATABASE_STOP);
        }
    }

    /**
     * Close all editor and view part related with this CUBRID Manager database node,not include
     * query editor
     *
     * @param databaseNode the CubridDatabase object
     * @param eventType CubridNodeChangedEventType
     */
    public void closeAllEditorAndViewInDatabase(
            CubridDatabase databaseNode, CubridNodeChangedEventType eventType) {

        IWorkbenchPage page = LayoutUtil.getActivePage();
        if (page == null) {
            return;
        }

        IEditorReference[] editorRefArr = page.getEditorReferences();
        for (int i = 0; editorRefArr != null && i < editorRefArr.length; i++) {
            IEditorReference editorRef = editorRefArr[i];
            try {
                IEditorInput editorInput = editorRef.getEditorInput();
                if (!(editorInput instanceof ISchemaNode)) {
                    continue;
                }
                ISchemaNode schemaNode = ((ISchemaNode) editorInput);
                ISchemaNode dbNode = schemaNode.getDatabase();
                if (dbNode.getId().equals(databaseNode.getId())) {
                    page.closeEditor(editorRef.getEditor(false), true);
                }
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }

        IViewReference[] viewRefArr = page.getViewReferences();
        if (viewRefArr == null || viewRefArr.length == 0) {
            return;
        }
        for (IViewReference viewRef : viewRefArr) {
            IViewPart viewPart = viewRef.getView(false);
            if (!(viewPart instanceof CubridViewPart)) {
                continue;
            }

            CubridViewPart cubridViewPart = (CubridViewPart) viewPart;
            ICubridNode cubridNode = cubridViewPart.getCubridNode();
            if (!(cubridNode instanceof ISchemaNode)) {
                continue;
            }
            ICubridNode cubridDatabaseNode = ((ISchemaNode) cubridNode).getDatabase();
            if (cubridDatabaseNode.getId().equals(databaseNode.getId())) {
                page.hideView(viewPart);
            }
        }
    }

    /**
     * When node remove, close this node's editor or view part
     *
     * @param eventNode ICubridNode
     * @param page IWorkbenchPage
     */
    protected void processNodeRemove(ICubridNode eventNode, IWorkbenchPage page) {
        List<IEditorPart> editorParts = LayoutUtil.getEditorParts(eventNode);
        for (IEditorPart part : editorParts) {
            if (part != null) {
                page.closeEditor(part, false);
            }
        }

        List<IViewPart> viewParts = LayoutUtil.getViewParts(eventNode);
        for (IViewPart part : viewParts) {
            if (part != null) {
                page.hideView(part);
            }
        }
    }

    /**
     * When refresh the container node and if this child node is deleted, then close it's editor or
     * view part
     *
     * @param eventNode ICubridNode
     * @param page IWorkbenchPage
     */
    protected void processNodeRemoveWhenRefreshContainer(
            ICubridNode eventNode, IWorkbenchPage page) {
        synchronized (this) {
            IEditorReference[] editorRefArr = page.getEditorReferences();
            if (editorRefArr != null && editorRefArr.length > 0) {
                for (IEditorReference editorRef : editorRefArr) {
                    try {
                        IEditorInput editorInput = editorRef.getEditorInput();
                        if (editorInput instanceof ICubridNode) {
                            ICubridNode editorNode = (ICubridNode) editorInput;
                            ICubridNode parentNode = editorNode.getParent();
                            if (editorNode != null
                                    && parentNode != null
                                    && parentNode.getId().equals(eventNode.getId())
                                    && eventNode.getChild(editorNode.getId()) == null) {
                                processNodeRemove(editorNode, page);
                            }
                        }
                    } catch (PartInitException e1) {
                        LOGGER.error(e1.getMessage());
                    }
                }
            }
            IViewReference[] viewRefArr = page.getViewReferences();
            if (viewRefArr != null && viewRefArr.length > 0) {
                for (IViewReference viewRef : viewRefArr) {
                    IViewPart viewPart = viewRef.getView(false);
                    if (viewPart instanceof CubridViewPart) {
                        ICubridNode viewPartNode = ((CubridViewPart) viewPart).getCubridNode();
                        if (viewPartNode == null) {
                            continue;
                        }
                        ICubridNode parentNode = viewPartNode.getParent();
                        if (viewPartNode != null
                                && parentNode != null
                                && parentNode.getId().equals(eventNode.getId())
                                && eventNode.getChild(viewPartNode.getId()) == null) {
                            processNodeRemove(viewPartNode, page);
                        }
                    }
                }
            }
        }
    }

    /**
     * Open and reopen the editor or view part of this cubrid node
     *
     * @param cubridNode the ICubridNode object
     */
    public void openEditorOrView(ICubridNode cubridNode) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        if (cubridNode instanceof ISchemaNode) {
            ISchemaNode schemaNode = (ISchemaNode) cubridNode;
            if (schemaNode.getDatabase() != null && !schemaNode.getDatabase().isLogined()) {
                return;
            }
        }
        // close the editor part that has been open
        String editorId = cubridNode.getEditorId();
        String viewId = cubridNode.getViewId();
        if (editorId != null && editorId.trim().length() > 0) {
            IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
            if (editorPart != null) {
                window.getActivePage().closeEditor(editorPart, false);
            }
        } else if (viewId != null && viewId.trim().length() > 0) {
            IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, viewId);
            if (viewPart != null) {
                window.getActivePage().hideView(viewPart);
            }
        }
        if (editorId != null && editorId.trim().length() > 0) {
            try {
                // if open the table schema editor,firstly load the schema
                if (editorId.equals(SchemaInfoEditorPart.ID) && cubridNode instanceof ISchemaNode) {
                    CubridDatabase database = ((ISchemaNode) cubridNode).getDatabase();
                    SchemaInfo newSchema =
                            database.getDatabaseInfo().getSchemaInfo(cubridNode.getName());
                    if (null == newSchema) {
                        CommonUITool.openErrorBox(database.getDatabaseInfo().getErrorMessage());
                        return;
                    }
                }
                window.getActivePage()
                        .openEditor(
                                cubridNode,
                                editorId,
                                true,
                                IWorkbenchPage.MATCH_ID & IWorkbenchPage.MATCH_INPUT);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        } else if (viewId != null && viewId.trim().length() > 0) {
            try {
                window.getActivePage().showView(viewId);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * ReOpen the editor or view part of this CUBRID node
     *
     * @param cubridNode the ICubridNode object
     */
    public void reopenEditorOrView(ICubridNode cubridNode) {
        if (cubridNode == null) {
            return;
        }
        String editorId = cubridNode.getEditorId();
        String viewId = cubridNode.getViewId();
        if (editorId != null && editorId.trim().length() > 0) {
            IEditorPart editorPart = LayoutUtil.getEditorPart(cubridNode, editorId);
            if (editorPart != null) {
                openEditorOrView(cubridNode);
            }
        } else if (viewId != null && viewId.trim().length() > 0) {
            IViewPart viewPart = LayoutUtil.getViewPart(cubridNode, viewId);
            if (viewPart != null) {
                openEditorOrView(cubridNode);
            }
        }
    }

    /**
     * Create select statement SQL
     *
     * @param schemaNode DefaultSchemaNode
     * @return String
     */
    protected String getStmtSQL(
            DefaultSchemaNode schemaNode) { // FIXME move this logic to core module
        if (schemaNode == null) {
            return "";
        }

        CubridDatabase db = schemaNode.getDatabase();
        DatabaseInfo dbInfo = db.getDatabaseInfo();
        GetAllAttrTask task = new GetAllAttrTask(dbInfo);
        task.setClassName(schemaNode.getName());
        task.getAttrList();
        if (task.getErrorMsg() != null) {
            return "";
        }

        List<DBAttribute> allAttrList = task.getAllAttrList();
        return SQLGenerateUtils.getSelectSQLWithLimit(schemaNode.getName(), allAttrList);
    }
}
