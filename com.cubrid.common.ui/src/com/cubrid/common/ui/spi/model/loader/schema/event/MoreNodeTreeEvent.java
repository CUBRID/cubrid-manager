/*
 * Copyright (C) 2018 CUBRID Co., Ltd. All rights reserved by CUBRID Co., Ltd.
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
package com.cubrid.common.ui.spi.model.loader.schema.event;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;

public class MoreNodeTreeEvent implements ITreeViewerListener {
	private final int MAX_TABLES_COUNT = 100;
	private final AbstractTreeViewer treeViewer;
	
	public MoreNodeTreeEvent(AbstractTreeViewer viewer) {
		this.treeViewer = viewer;
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		final Object element = event.getElement();
		if (element instanceof DefaultSchemaNode
				&& ((DefaultSchemaNode) element).getType().equals(NodeType.MORE)) {
			Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {
					DefaultSchemaNode parent = (DefaultSchemaNode) element;
					CubridDatabase database = parent.getDatabase();
					List<ClassInfo> allClassInfoList = database.getDatabaseInfo().getClassInfoList();
					int index = CubridTablesFolderLoader.moreNodeIndex(parent.getId());
					boolean hasMoreNode = allClassInfoList.size() > index + MAX_TABLES_COUNT;
					int length = hasMoreNode ? index + MAX_TABLES_COUNT : allClassInfoList.size();
					ICubridNode[] children = new ICubridNode[length - index];
					ICubridNode tablesTree = parent.getParent();

					for (int i = index; i < length; i++) {
						ClassInfo classInfo = allClassInfoList.get(i);
						String id = parent.getId() + ICubridNodeLoader.NODE_SEPARATOR
								+ classInfo.getClassName();
						ICubridNode child = CubridTablesFolderLoader.createClassNode(id, classInfo, 1);
						children[i % MAX_TABLES_COUNT] = child;
						tablesTree.addChild(child);
					}

					treeViewer.add(tablesTree, children);
					Object[] expandedElements = treeViewer.getExpandedElements();
					for (Object o : expandedElements) {
						if (NodeType.MORE.equals(((ICubridNode) o).getType())) {
							treeViewer.remove(o);
						}
					}

					if (hasMoreNode) {
						treeViewer.add(tablesTree,
								CubridTablesFolderLoader.createMoreNode(tablesTree, length));
					}
				}
			});

		}
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
	}
}
