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
package com.cubrid.cubridmanager.ui.cubrid.database.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.QueryOptions;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import com.cubrid.cubridmanager.ui.cubrid.database.dialog.RenameDatabaseDialog;
import com.cubrid.cubridmanager.ui.spi.persist.CMDBNodePersistManager;

/**
 * This action is responsible to rename database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class RenameDatabaseAction extends SelectionAction {
	public static final String ID = RenameDatabaseAction.class.getName();
	private DbSpaceInfoList dbSpaceInfo = null;

	public RenameDatabaseAction(Shell shell, String text, ImageDescriptor icon) {
		this(shell, null, text, icon);
	}

	public RenameDatabaseAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor icon) {
		super(shell, provider, text, icon);
		this.setId(ID);
		this.setToolTipText(text);
	}

	public boolean allowMultiSelections() {
		return false;
	}

	public boolean isSupported(Object obj) {
		return ActionSupportUtil.hasAdminPermissionOnStopState(obj);
	}

	public void run() {
		Object[] obj = this.getSelectedObj();
		if (!isSupported(obj[0])) {
			setEnabled(false);
			return;
		}

		final CubridDatabase database = (CubridDatabase) obj[0];
		ISelectionProvider provider = this.getSelectionProvider();
		final TreeViewer viewer = (TreeViewer) provider;
		String serverName = database.getServer().getName();
		String dbName = database.getName();
		final String jobName = serverName + "-" + dbName + "-"
				+ Messages.msgRenameDBRearJobName;
		final RenameDatabaseDialog dialog = new RenameDatabaseDialog(
				getShell(), new ITaskExecutorInterceptor() {

					public void completeAll() {
						CMDBNodePersistManager.getInstance().deleteDbParameter(database);
						QueryOptions.removePref(database, true);
						CommonUITool.openInformationBox(Messages.titleSuccess,
								Messages.bind(Messages.msgRenameDBComplete,
										jobName));
						CommonUITool.refreshNavigatorTree(viewer,
								database.getParent());
					}

					public IStatus postTaskFinished(ITask task) {
						return Status.OK_STATUS;
					}
				});
		dialog.setDatabase(database);
		final Shell shell = getShell();
		TaskExecutor taskExcutor = new TaskExecutor() {
			@SuppressWarnings("unchecked")
			public boolean exec(final IProgressMonitor monitor) {
				if (monitor.isCanceled()) {
					return false;
				}
				monitor.beginTask(Messages.getDbSpaceInfoTaskName,
						IProgressMonitor.UNKNOWN);
				for (ITask task : taskList) {
					task.execute();
					final String msg = task.getErrorMsg();
					if (openErrorBox(shell, msg, monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
					if (task instanceof CommonQueryTask) {
						dbSpaceInfo = ((CommonQueryTask<DbSpaceInfoList>) task).getResultModel();
					}
				}
				return true;
			}
		};
		dbSpaceInfo = new DbSpaceInfoList();
		CommonQueryTask<DbSpaceInfoList> task = new CommonQueryTask<DbSpaceInfoList>(
				database.getServer().getServerInfo(),
				CommonSendMsg.getCommonDatabaseSendMsg(), dbSpaceInfo);
		task.setDbName(database.getLabel());
		taskExcutor.addTask(task);
		new ExecTaskWithProgress(taskExcutor).busyCursorWhile();
		if (taskExcutor.isSuccess()) {
			dialog.setDbSpaceInfoList(dbSpaceInfo);
			dialog.open();
		}
	}
}
