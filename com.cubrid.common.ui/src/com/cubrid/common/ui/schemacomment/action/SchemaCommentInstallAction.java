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
package com.cubrid.common.ui.schemacomment.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * Schema Comment Install action
 *
 * @author Isaiah Choe
 * @version 1.0 - 2012-12-09 created by Isaiah Choe
 */
public class SchemaCommentInstallAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(SchemaCommentInstallAction.class);
	public static final String ID = SchemaCommentInstallAction.class.getName();

	public SchemaCommentInstallAction(Shell shell, String text,
			ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
		super(shell, null, text, enabledIcon);
		this.setDisabledImageDescriptor(disabledIcon);
		this.setId(ID);
	}

	private CubridDatabase getSelectedDatabase() {
		CubridDatabase cubridDatabase = null;
		Object[] objs = this.getSelectedObj();
		for (Object obj : objs){
			if (obj instanceof ISchemaNode){
				cubridDatabase = ((ISchemaNode) obj).getDatabase();
				if (cubridDatabase != null) {
					break;
				}
			} 
		}

		return cubridDatabase;
	}
	
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}

		CubridDatabase cubridDatabase = getSelectedDatabase();
		if (cubridDatabase == null) {
			CommonUITool.openErrorBox(Messages.msgTableCommentNotSelectedDb);
			return;
		}

		if (!cubridDatabase.isLogined()
				|| cubridDatabase.getRunningType() != DbRunningType.CS) {
			CommonUITool.openErrorBox(Messages.msgTableCommentNotLoginedDb);
			return;
		}

		boolean isDBA = cubridDatabase.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority();
		if (!isDBA) {
			CommonUITool.openErrorBox(Messages.msgTableCommentNotDBA);
			return;
		}

		DatabaseInfo dbInfo = cubridDatabase.getDatabaseInfo();

		if (CompatibleUtil.isCommentSupports(dbInfo)) {
			CommonUITool.openInformationBox(Messages.msgTableCommentAlertTitle,
					Messages.msgTableCommentInstallNotSupport);
			return;
		}

		String msg = Messages.bind(Messages.msgTableCommentConfirm, 
				ConstantsUtil.SCHEMA_DESCRIPTION_TABLE);
		boolean needToCreate = CommonUITool.openConfirmBox(msg);
		if (!needToCreate) {
			CommonUITool.openInformationBox(Messages.msgTableCommentAlertTitle,
					Messages.msgTableCommentCancel);
			return;
		}

		Connection conn = null;
		boolean success = false;
		String error = null;
		try {
			conn = JDBCConnectionManager.getConnection(dbInfo, false);

			if (SchemaCommentHandler.isInstalledMetaTable(dbInfo, conn)) {
				msg = Messages.bind(Messages.msgTableCommentAlreadyInstalled,
						ConstantsUtil.SCHEMA_DESCRIPTION_TABLE);
				CommonUITool.openErrorBox(msg);
				return;
			}

			if (dbInfo.isShard()) {
				msg = Messages.errTableCommentCannotInstallOnShard;
				CommonUITool.openErrorBox(msg);
				return;
			}

			success = SchemaCommentHandler.installMetaTable(dbInfo, conn);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			error = e.getMessage();
		}

		if (!success) {
			msg = Messages.bind(Messages.errTableCommentInstall, error);
			CommonUITool.openErrorBox(msg);
			return;
		}

		msg = Messages.bind(Messages.msgTableCommentSuccess, 
				ConstantsUtil.SCHEMA_DESCRIPTION_TABLE);
		CommonUITool.openInformationBox(Messages.msgTableCommentAlertTitle, msg);
	}

	public boolean allowMultiSelections() {
		return false;
	}

	public boolean isSupported(Object obj) {
		return true;
	}
}
