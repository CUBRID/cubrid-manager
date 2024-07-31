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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.common.dialog.FilterSettingDialog;
import com.cubrid.common.ui.common.navigator.NodeFilterManager;
import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Filter setting action for tree
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-1 created by pangqiren
 */
public class FilterSettingAction extends Action {

    public static final String ID = FilterSettingAction.class.getName();
    private TreeViewer tv;

    public FilterSettingAction(String text, ImageDescriptor image, TreeViewer treeViewer) {
        super(text);
        setId(ID);
        this.setToolTipText(text);
        this.setImageDescriptor(image);
        tv = treeViewer;
    }

    public void setTv(TreeViewer tv) {
        this.tv = tv;
    }

    /** Filter */
    public void run() {
        final List<ICubridNode> defaultCheckedList = new ArrayList<ICubridNode>();
        final List<ICubridNode> defaultGrayCheckedList = new ArrayList<ICubridNode>();
        BusyIndicator.showWhile(
                Display.getDefault(),
                new Runnable() {
                    public void run() {
                        // initial the tree id filter
                        Object inputObj = tv.getInput();
                        if (!(inputObj instanceof List<?>)) {
                            return;
                        }
                        List<?> list = (List<?>) inputObj;
                        Map<String, ICubridNode> nodeMap = new HashMap<String, ICubridNode>();
                        for (Object obj : list) {
                            if (!(obj instanceof ICubridNode)) {
                                continue;
                            }
                            ICubridNode node = (ICubridNode) obj;
                            nodeMap.put(node.getId(), node);
                            makeNodeMap(node, nodeMap);
                        }
                        // make the default checked gray elements list
                        List<String> idGrayList =
                                NodeFilterManager.getInstance().getIdGrayFilterList();
                        for (int i = 0; idGrayList != null && i < idGrayList.size(); i++) {
                            String id = idGrayList.get(i);
                            ICubridNode node = nodeMap.get(id);
                            if (node != null) {
                                defaultGrayCheckedList.add(node);
                            }
                        }
                        // make the default checked elements list
                        List<String> idList = NodeFilterManager.getInstance().getIdFilterList();
                        if (idList == null || idList.isEmpty()) {
                            return;
                        }
                        for (String id : idList) {
                            ICubridNode node = nodeMap.get(id);
                            if (node != null) {
                                defaultCheckedList.add(node);
                                defaultGrayCheckedList.remove(node);
                            }
                        }
                    }
                });
        FilterSettingDialog dialog =
                new FilterSettingDialog(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        tv,
                        defaultCheckedList,
                        defaultGrayCheckedList);
        if (IDialogConstants.OK_ID == dialog.open()) {
            tv.setFilters(NodeFilterManager.getInstance().getViewerFilter());
        }
    }

    /**
     * Make node map
     *
     * @param parent ICubridNode
     * @param nodeMap Map<String, ICubridNode>
     */
    private void makeNodeMap(ICubridNode parent, Map<String, ICubridNode> nodeMap) {
        List<ICubridNode> nodeList = parent.getChildren();
        if (nodeList == null || nodeList.isEmpty()) {
            return;
        }
        for (ICubridNode node : nodeList) {
            nodeMap.put(node.getId(), node);
            if (node.isContainer()) {
                makeNodeMap(node, nodeMap);
            }
        }
    }
}
