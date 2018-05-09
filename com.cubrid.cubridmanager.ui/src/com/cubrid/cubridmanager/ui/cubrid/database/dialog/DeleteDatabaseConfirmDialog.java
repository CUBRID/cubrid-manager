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
package com.cubrid.cubridmanager.ui.cubrid.database.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoginDatabaseTask;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;

/**
 * Delete database confirm
 * 
 * @author robin 2009-3-16
 */
public class DeleteDatabaseConfirmDialog extends
		CMTitleAreaDialog {

	private Composite parentComp;
	private CubridDatabase database = null;
	private DbSpaceInfoList dbSpaceInfo = null;
	public final static int DELETE_ID = 103;
	public final static int CONNECT_ID = 0;
	private final String USERNAME_DBA = "dba";
	private Text text;

	public DeleteDatabaseConfirmDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the control
	 */
	protected Control createDialogArea(Composite parent) {
		parentComp = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parentComp, SWT.NONE);

		final GridLayout glSourceDBComposite = new GridLayout();
		glSourceDBComposite.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		glSourceDBComposite.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		glSourceDBComposite.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		glSourceDBComposite.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		glSourceDBComposite.numColumns = 2;
		composite.setLayout(glSourceDBComposite);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createDirectoryList(composite);

		setTitle(Messages.titleDeleteDbConfirmDialog);
		setMessage(Messages.msgDeleteDbConfirmDialog);

		return parentComp;
	}

	/**
	 * create the directory list
	 * 
	 * @param composite the parent composite
	 */
	private void createDirectoryList(Composite composite) {

		Label clabel1 = new Label(composite, SWT.SHADOW_IN);
		final GridData gdClabel1 = CommonUITool.createGridData(1, 1, -1, -1);
		clabel1.setLayoutData(gdClabel1);
		clabel1.setText(Messages.msgInputDbaPassword);
		text = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(500, 220);
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.titleDeleteDbConfirmDialog);

	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.cubridmanager.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When button pressed,handle with the event
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			String pass = text.getText();
			if (pass == null) {
				pass = "";
			}
			/*TOOLS-3633*/
			ServerInfo serverInfo = database.getServer().getServerInfo();
			LoginDatabaseTask loginDatabaseTask = new LoginDatabaseTask(
					serverInfo);
			loginDatabaseTask.setCMUser(serverInfo.getUserName());
			loginDatabaseTask.setDbName(database.getDatabaseInfo().getDbName());
			loginDatabaseTask.setDbUser(USERNAME_DBA);
			loginDatabaseTask.setDbPassword(pass);

			TaskExecutor taskExcutor = new CommonTaskExec(
					Messages.msgCheckPassword);
			taskExcutor.addTask(loginDatabaseTask);
			new ExecTaskWithProgress(taskExcutor).exec();
			if (!taskExcutor.isSuccess()) {
				return;
			}
		}
		super.buttonPressed(buttonId);
	}

	public CubridDatabase getDatabase() {
		return database;
	}

	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	public DbSpaceInfoList getDbSpaceInfo() {
		return dbSpaceInfo;
	}

	public void setDbSpaceInfo(DbSpaceInfoList dbSpaceInfo) {
		this.dbSpaceInfo = dbSpaceInfo;
	}

}
