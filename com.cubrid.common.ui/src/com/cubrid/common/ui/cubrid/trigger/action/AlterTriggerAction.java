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
package com.cubrid.common.ui.cubrid.trigger.action;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.CommentType;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.cubrid.trigger.Messages;
import com.cubrid.common.ui.cubrid.trigger.dialog.CreateTriggerDialog;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.GetTriggerListTask;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCGetTriggerInfoTask;

/**
 *
 * Alter trigger action
 *
 * @author wangmoulin
 * @version 1.0 - 2009-12-28 created by wangmoulin
 */
public class AlterTriggerAction extends
		SelectionAction {

	public static final String ID = AlterTriggerAction.class.getName();

	/**
	 * The constructor
	 *
	 * @param shell
	 * @param text
	 * @param icon
	 */
	public AlterTriggerAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	/**
	 * The constructor
	 *
	 * @param shell
	 * @param provider
	 * @param text
	 * @param icon
	 */
	public AlterTriggerAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
	}

	/**
	 *
	 * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections
	 *      ()
	 * @return false
	 */
	public boolean allowMultiSelections() {
		return false;
	}

	/**
	 *
	 * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java
	 *      .lang.Object)
	 * @param obj the Object
	 * @return <code>true</code> if support this obj;<code>false</code>
	 *         otherwise
	 */
	public boolean isSupported(Object obj) {
		return ActionSupportUtil.isSupportSinSelCheckDbUser(obj,
				NodeType.TRIGGER);
	}

	/**
	 * Open alter trigger dialog
	 */
	public void run() {
		Object[] objArr = this.getSelectedObj();
		if (!isSupported(objArr)) {
			this.setEnabled(false);
			return;
		}
		final ISchemaNode triggerNode = (ISchemaNode) objArr[0];
		CubridDatabase database = triggerNode.getDatabase();
		run (database, triggerNode);
	}

	/**
	 * edit trigger
	 * @param database
	 * @param node
	 * @return
	 */
	public int run (final CubridDatabase database, final ISchemaNode node) { // FIXME move this logic to core module
		TaskExecutor taskExcutor = new TaskExecutor() {
			public boolean exec(final IProgressMonitor monitor) {
				if (monitor.isCanceled()) {
					return false;
				}
				for (ITask task : taskList) {
					task.execute();
					final String msg = task.getErrorMsg();
					if (openErrorBox(shell, msg, monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
					Trigger trigger = null;
					if (task instanceof GetTriggerListTask) {
						GetTriggerListTask getTriggerListTask = (GetTriggerListTask) task;
						List<Trigger> triggerList = getTriggerListTask.getTriggerInfoList();
						for (int i = 0; triggerList != null
								&& i < triggerList.size(); i++) {
							Trigger trig = triggerList.get(i);
							if (node.getName().equals(trig.getName())) {
								trigger = trig;
								break;
							}
						}
					} else if (task instanceof JDBCGetTriggerInfoTask) {
						JDBCGetTriggerInfoTask getTriggerInfoTask = (JDBCGetTriggerInfoTask) task;
						trigger = getTriggerInfoTask.getTriggerInfo(node.getLabel());
					}
					if (trigger == null) {
						openErrorBox(shell, Messages.errNameNoExist, monitor);
						return false;
					}
					// getting comment for version after 10.0
					if (CompatibleUtil.isCommentSupports(database.getDatabaseInfo())) {
						try {
							SchemaComment schemaComment = SchemaCommentHandler.loadObjectDescription(
									database.getDatabaseInfo(), JDBCConnectionManager.getConnection(
											database.getDatabaseInfo(), true), trigger.getName(),
											CommentType.TRIGGER);
							trigger.setDescription(schemaComment.getDescription());
						} catch (SQLException e) {
							CommonUITool.openErrorBox(e.getMessage());
						}
					}
					node.setModelObj(trigger);
				}
				return true;
			}
		};
		ITask task = null;

		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			task = new GetTriggerListTask(database.getServer().getServerInfo());
			((GetTriggerListTask) task).setDbName(database.getName());
		} else {
			task = new JDBCGetTriggerInfoTask(database.getDatabaseInfo());
		}
		taskExcutor.addTask(task);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
		if (!taskExcutor.isSuccess()) {
			return IDialogConstants.CANCEL_ID;
		}

		CreateTriggerDialog dialog = new CreateTriggerDialog(getShell(),
				node.getDatabase(),
				(Trigger) node.getAdapter(Trigger.class));

		if (dialog.open() != IDialogConstants.CANCEL_ID) {
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(node,
							CubridNodeChangedEventType.NODE_REFRESH));
			ActionManager.getInstance().fireSelectionChanged(getSelection());
			return IDialogConstants.OK_ID;
		}
		return IDialogConstants.CANCEL_ID;
	}
}
