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
package com.cubrid.common.ui.spi.model;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;

import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.model.loader.schema.CubridTablesFolderLoader;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;

/**
 * This Class will be used to create the 'More Tables...' node in host navigator
 * 
 * @author hun-a
 *
 */
public class MoreTablesNode {
	private final static int MAX_TABLES_COUNT = 100;
	private final AbstractTreeViewer treeViewer;
	private DefaultSchemaNode moreNode;
	private ICubridNode tablesNode;
	private List<ClassInfo> userTableInfoList;
	private int currentIndex;
	private int nextIndex;
	private boolean hasMoreNode;
	private ICubridNode[] children;

	public MoreTablesNode(AbstractTreeViewer viewer, DefaultSchemaNode node) {
		this.treeViewer = viewer;
		this.moreNode = node;
		this.tablesNode = node.getParent();
		this.userTableInfoList = node.getDatabase().getDatabaseInfo().getUserTableInfoList();
		this.currentIndex = CubridTablesFolderLoader.moreNodeIndex(node.getId());
		this.hasMoreNode = userTableInfoList.size() > currentIndex + MAX_TABLES_COUNT;
		this.nextIndex = hasMoreNode ? currentIndex + MAX_TABLES_COUNT : userTableInfoList.size();
		this.children = new ICubridNode[nextIndex - currentIndex];
	}

	/**
	 * Make the children and add the children to the TreeViewer
	 *  and refresh the 'More tables...' node
	 */
	public void expandMoreTables() {
		makeChildren();
		removeMoreNode();
		addChildrenToTreeViewer();
		updateTablesCount();
		makeNewMoreNode();
	}

	/**
	 * Create and add child nodes to the 'Tables' model 
	 */
	private void makeChildren() {
		for (int i = currentIndex; i < nextIndex; i++) {
			ClassInfo classInfo = userTableInfoList.get(i);
			String id = moreNode.getId() + ICubridNodeLoader
					.NODE_SEPARATOR + classInfo.getClassName();
			ICubridNode child = CubridTablesFolderLoader
					.createClassNode(id, classInfo, 1);
			children[i % MAX_TABLES_COUNT] = child;
			tablesNode.addChild(child);
		}
	}

	/**
	 * Add child nodes to the 'Tables' node of 'TreeViewer'
	 */
	private void addChildrenToTreeViewer() {
		treeViewer.add(tablesNode, children);
	}

	/**
	 * Find and remove the 'More tables ...' node
	 * among the expanded nodes in the TreeViewer
	 */
	private void removeMoreNode() {
		treeViewer.remove(moreNode);
	}

	/**
	 * Create a new 'More Tables ...' node in the 'TreeViewer'
	 */
	private void makeNewMoreNode() {
		if (hasMoreNode) {
			treeViewer.add(tablesNode,
					CubridTablesFolderLoader
					.createMoreNode(tablesNode, nextIndex));
		}
	}

	/**
	 * Updated the number of tables currently displayed in the 'Tables' node
	 */
	private void updateTablesCount() {
		String label = Messages.msgTablesFolderName;
		tablesNode.setLabel(String.format("%s(%d)", label, nextIndex));
		treeViewer.update(tablesNode, null);
	}
}