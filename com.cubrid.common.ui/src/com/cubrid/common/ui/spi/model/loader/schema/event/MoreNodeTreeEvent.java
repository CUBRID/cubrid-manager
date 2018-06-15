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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.MoreTablesNode;
import com.cubrid.common.ui.spi.model.NodeType;

/**
 * An event class that adds new tables to the 'TreeViewer' 
 * when the tree of the 'More Tables ...' node is expanded.
 *
 * @author hun-a
 *
 */
public class MoreNodeTreeEvent implements ITreeViewerListener {
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
					MoreTablesNode model = new MoreTablesNode(
							treeViewer, (DefaultSchemaNode) element);
					model.expandMoreTables();
				}
			});
		}
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
	}
}