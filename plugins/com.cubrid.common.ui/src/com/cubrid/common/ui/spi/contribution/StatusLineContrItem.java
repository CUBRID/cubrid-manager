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
package com.cubrid.common.ui.spi.contribution;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import java.util.List;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * Status line contribution item,it show the status information
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-21 created by pangqiren
 */
public class StatusLineContrItem {

    /**
     * Update the status line information
     *
     * @param statusLineManager StatusLineManager
     * @param cubridNode The selected ICubridNode object
     */
    protected void updateStatusLine(StatusLineManager statusLineManager, ICubridNode cubridNode) {
        noOp();
    }

    /**
     * When selection changed,change status line message for view part or editor part
     *
     * @param node the ICubridNode object
     * @param workbenchPart the IWorkbenchPart object
     */
    public void changeStuatusLineForViewOrEditPart(ICubridNode node, IWorkbenchPart workbenchPart) {

        WorkbenchWindow window =
                (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        String nodePath = "";
        if (workbenchPart == null) {
            if (null != node.getViewId()) {
                IViewPart viewPart = LayoutUtil.getViewPart(node, node.getViewId());
                if (viewPart != null) {
                    nodePath = viewPart.getTitle();
                }
            }
            if (null != node.getEditorId()) {
                IEditorPart editorPart = LayoutUtil.getEditorPart(node, node.getEditorId());
                if (editorPart != null) {
                    nodePath = editorPart.getTitle();
                }
            }
        } else {
            nodePath = workbenchPart.getTitle();
        }
        // window.setStatus(nodePath == null ? "" : nodePath);
    }

    /**
     * Change status line for navigator selection
     *
     * @param selection the ISelection object
     */
    public void changeStuatusLineForNavigator(ISelection selection) {
        // empty
    }

    /** Clear the status line information of CUBRID Manager */
    public void clearStatusLine() {
        // empty
    }

    /**
     * Get children number of the selected CUBRID node
     *
     * @param cubridNode ICubridNode
     * @return String
     */
    protected String getChilderenNumStr(ICubridNode cubridNode) {
        ICubridNode containerNode = cubridNode;
        if (!cubridNode.isContainer()) {
            containerNode = cubridNode.getParent();
        }
        boolean isLoaded =
                containerNode != null
                        && containerNode.getLoader() != null
                        && containerNode.getLoader().isLoaded();
        if (!isLoaded) {
            return "";
        }
        String nodeType = containerNode.getType();
        int size = containerNode.getChildren() == null ? 0 : containerNode.getChildren().size();
        if (NodeType.TABLE_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgUserTableNum, size - 1);
        } else if (NodeType.SYSTEM_TABLE_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgSysTableNum, size);
        } else if (NodeType.VIEW_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgUserViewNum, size - 1);
        } else if (NodeType.SYSTEM_VIEW_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgSysViewNum, size);
        } else if (NodeType.TRIGGER_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgTriggerNum, size);
        } else if (NodeType.SYNONYM_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgSynonymNum, size);
        } else if (NodeType.SERIAL_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgSerialNum, size);
        } else if (NodeType.STORED_PROCEDURE_FUNCTION_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgFunctionNum, size);
        } else if (NodeType.STORED_PROCEDURE_PROCEDURE_FOLDER.equals(nodeType)) {
            return Messages.bind(Messages.msgProcedureNum, size);
        } else if (NodeType.STORED_PROCEDURE_FOLDER.equals(nodeType)) {
            size = 0;
            boolean isShow = false;
            List<ICubridNode> nodeList = containerNode.getChildren();
            for (int i = 0; nodeList != null && i < nodeList.size(); i++) {
                ICubridNode node = nodeList.get(i);
                if (node.isContainer() && node.getLoader().isLoaded()) {
                    size += node.getChildren().size();
                    isShow = true;
                } else {
                    isShow = false;
                    break;
                }
            }
            if (isShow) {
                return Messages.bind(Messages.msgSPNum, size);
            }
        } else if (NodeType.DATABASE.equals(nodeType)) {
            size = 0;
            boolean isShow = false;
            List<ICubridNode> nodeList = containerNode.getChildren();
            for (int i = 0; nodeList != null && i < nodeList.size(); i++) {
                ICubridNode node = nodeList.get(i);
                if (node.isContainer()
                        && NodeType.TABLE_FOLDER.equals(node.getType())
                        && node.getLoader().isLoaded()) {
                    size += node.getChildren().size();
                    isShow = true;
                }
            }
            if (isShow) {
                return Messages.bind(Messages.msgUserTableNum, size - 1);
            }
        }
        return "";
    }
}
