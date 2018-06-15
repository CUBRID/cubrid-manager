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
package com.cubrid.common.ui.common.navigator;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.WorkbenchJob;

import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.model.loader.schema.event.MoreNodeTreeEvent;

/**
 * 
 * This class extend DeferredTreeContentManager for restoring the previous tree
 * expanded status
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDeferredTreeContentManager extends DeferredTreeContentManager {
	private Object[] expandedElements = null;
	private final AbstractTreeViewer treeViewer;

	public CubridDeferredTreeContentManager(AbstractTreeViewer viewer) {
		super(viewer);
		treeViewer = viewer;
		addHostListeners();
	}

	/**
	 * Set expanded elements
	 * 
	 * @param expandedElements the expanded elements array
	 */
	public void setExpandedElements(Object[] expandedElements) {
		this.expandedElements = expandedElements == null ? null : (Object[]) expandedElements.clone();
	}

	/**
	 * Create a UIJob to add the children to the parent in the tree viewer.
	 * 
	 * @param parent the parent tree container node
	 * @param children the added children array
	 * @param monitor the monitor object
	 */
	protected void addChildren(final Object parent, final Object[] children, IProgressMonitor monitor) {
		WorkbenchJob updateJob = new WorkbenchJob(Messages.msgAddingChildren) {
			public IStatus runInUIThread(IProgressMonitor updateMonitor) {
				// Cancel the job if the tree viewer got closed
				if (treeViewer.getControl().isDisposed() || updateMonitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				treeViewer.add(parent, children);
				if (parent instanceof ICubridNode && ((ICubridNode) parent).isContainer()) {
					treeViewer.update(parent, null);
					ActionManager.getInstance().fireSelectionChanged(treeViewer.getSelection());
				}
				ICubridNode parentNode = null;
				if (parent instanceof ICubridNode) {
					parentNode = (ICubridNode) parent;
				}
				boolean isExpandNextLevel = parentNode != null
						&& (parentNode.getType().equals(NodeType.USER_TABLE) || parentNode.getType().equals(NodeType.SERVER)) && expandedElements == null;
				if (isExpandNextLevel) {
					expandToNextLevel(children);
				} else {
					for (int i = 0; children != null && i < children.length; i++) {
						if ((children[i] instanceof ICubridNode)) {
							ICubridNode node = (ICubridNode) children[i];
							for (int j = 0; expandedElements != null && j < expandedElements.length; j++) {
								if (expandedElements[j] instanceof ICubridNode
										&& node.getId().equals(((ICubridNode) expandedElements[j]).getId())) {
									treeViewer.expandToLevel(children[i], 1);
								}
							}
						}
					}
				}

				// set user/table folder node label, add children count
				if (hasChildCount(parentNode)) {
					int count = 0;
					if (parentNode.getType().equals(NodeType.TABLE_FOLDER)) {
						count = 0;
						for (ICubridNode tableNode : parentNode.getChildren()) {
							if (tableNode.getType().equals(NodeType.USER_TABLE)
									|| tableNode.getType().equals(NodeType.USER_PARTITIONED_TABLE_FOLDER)) {
								count ++;
							}
						}
					} else if (parentNode.getType().equals(NodeType.VIEW_FOLDER)) {
						for (ICubridNode viewNode : parentNode.getChildren()) {
							if (viewNode.getType().equals(NodeType.USER_VIEW)) {
								count ++;
							}
						}
					} else {
						count = parentNode.getChildren().size();
					}
					String suffix = "(" + Integer.valueOf(count) + ")";
					String beforeLable = parentNode.getLabel();
					
					if (beforeLable.endsWith(")") && beforeLable.indexOf("(") > -1){
						String beforeCount = beforeLable.substring(beforeLable.indexOf("(") + 1, beforeLable.length() - 1);
						// if children count not change, do not update label
						if (String.valueOf(count).equals(beforeCount)) {
							return Status.OK_STATUS;
						}
						beforeLable = beforeLable.substring(0, beforeLable.indexOf("("));
					} 
					parentNode.setLabel(beforeLable + suffix);
					treeViewer.refresh(parentNode, true);
				}

				return Status.OK_STATUS;
			}
		};
		updateJob.setSystem(true);
		updateJob.schedule();
	}

	/**
	 * Return true if there have counts of children node.
	 *
	 * @param parentNode
	 * @return boolean
	 */
	private boolean hasChildCount(ICubridNode parentNode) {
		if (parentNode == null) {
			return false;
		}

		return (parentNode.getType().equals(NodeType.USER_FOLDER)
				|| parentNode.getType().equals(NodeType.TABLE_FOLDER)
				|| parentNode.getType().equals(NodeType.VIEW_FOLDER)
				|| parentNode.getType().equals(NodeType.SERIAL_FOLDER)
				|| parentNode.getType().equals(NodeType.TRIGGER_FOLDER));
	}

	/**
	 * Expand to next level
	 * 
	 * @param children Object[]
	 */
	private void expandToNextLevel(final Object[] children) {
		for (int i = 0; children != null && i < children.length; i++) {
			ICubridNode node = null;
			if (children[i] instanceof ICubridNode) {
				node = (ICubridNode) children[i];
			}
			if (node != null && node.getType().equals(NodeType.TABLE_INDEX_FOLDER)) {
				continue;
			}
			treeViewer.expandToLevel(children[i], 1);
		}
	}

	private void addHostListeners() {
		treeViewer.addTreeListener(new MoreNodeTreeEvent(treeViewer));
	}
}