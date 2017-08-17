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
package com.cubrid.common.ui.query.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.dialog.ShardIdSelectionDialog;
import com.cubrid.common.ui.query.Messages;
import com.cubrid.common.ui.query.editor.QueryEditorPart;
import com.cubrid.common.ui.query.editor.QueryEditorUtil;
import com.cubrid.common.ui.query.editor.QueryUnit;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.LayoutUtil;
import com.cubrid.cubridmanager.core.common.model.DbRunningType;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * action for create new query editor of the databse node
 * 
 * @author wangsl 2009-3-9
 */
public class DatabaseQueryNewAction extends SelectionAction {
	private static final Logger LOGGER = LogUtil.getLogger(DatabaseQueryNewAction.class);
	public static final String ID = DatabaseQueryNewAction.class.getName();
	public static final String ID_BIG = DatabaseQueryNewAction.class.getName()+"Big";

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param text
	 * @param enabledIcon
	 * @param disabledIcon
	 */
	public DatabaseQueryNewAction(Shell shell, String text,
			ImageDescriptor enabledIcon, ImageDescriptor disabledIcon, boolean isBig) {
		this(shell, null, text, enabledIcon, disabledIcon, isBig);
	}

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param provider
	 * @param text
	 * @param enabledIcon
	 * @param disabledIcon
	 */
	public DatabaseQueryNewAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor enabledIcon,
			ImageDescriptor disabledIcon, boolean bigButton) {
		super(shell, provider, text, enabledIcon);
		this.setId(bigButton ? ID_BIG : ID);	
		this.setToolTipText(text);
		this.setDisabledImageDescriptor(disabledIcon);
	}

	/**
	 * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections()
	 * @return <code>true</code> if allow multi selection;<code>false</code>
	 *         otherwise
	 */
	public boolean allowMultiSelections() {
		return true;
	}

	/**
	 * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java.lang.Object)
	 * @param obj the Object
	 * @return <code>true</code> if support this obj;<code>false</code>
	 *         otherwise
	 */
	public boolean isSupported(Object obj) {
		if (obj instanceof ISchemaNode) {
			CubridDatabase database = ((ISchemaNode) obj).getDatabase();
			if (database.isLogined()
					&& database.getRunningType() == DbRunningType.CS) {
				return true;
			}
		} else if (obj instanceof Object[]) {
			Object[] objs = (Object[]) obj;
			for (Object object : objs) {
				if (!isSupported(object)) {
					return false;
				}
			}
			return objs.length >= 1;
		}
		return false;
	}

	/**
	 * handleSelectionObj
	 * @param objs
	 * @return
	 */
	private CubridDatabase[] handleSelectionObj(Object[] objs){
		List<CubridDatabase> returnArray = new ArrayList<CubridDatabase>();
		CubridDatabase database = null;
		for(Object obj : objs){
			if (obj instanceof ISchemaNode){
				database = ((ISchemaNode) obj).getDatabase();
				if (database != null) {
					//if multiple selection ,only open a query editor with one database
					if (!returnArray.contains(database)) {
						returnArray.add(database);
					}
				}
			} 
		}

		return returnArray.toArray(new CubridDatabase[0]);
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}

		Object[] obj = this.getSelectedObj();
		CubridDatabase[] cubridDatabases = handleSelectionObj(obj);
		if (cubridDatabases.length == 0) {
			return;
		}

		/*Limit max number one time*/
		if(cubridDatabases.length > LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM) {
			CommonUITool.openConfirmBox(Messages.bind(Messages.msgMaxOpenNum, LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM));
			List<CubridDatabase> list = new ArrayList<CubridDatabase>(LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM);
			for(int i = 0; i < LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM; i++) {
				list.add(cubridDatabases[i]);
			}
			cubridDatabases = new CubridDatabase[LayoutUtil.MAX_OPEN_QUERY_EDITOR_NUM];
			list.toArray(cubridDatabases);
		}

		// [TOOLS-2425]Support shard broker
		// it is used in order to check first shard database when you open several editor of databases.
		int count = cubridDatabases.length;

		for (CubridDatabase database : cubridDatabases) {
			QueryUnit queryUnit = new QueryUnit();
			queryUnit.setDatabase(database);

			// [TOOLS-2425]Support shard broker
			DatabaseInfo dbInfo = database.getDatabaseInfo();
			if (dbInfo == null) {
				continue;
			}

			int shardId = 0;
			int shardVal = 0;
			int shardQueryType = DatabaseInfo.SHARD_QUERY_TYPE_VAL;
			if (count == 1) {
				// [TOOLS-2425]Support shard broker
				if (dbInfo != null && dbInfo.isShard()) {
					ShardIdSelectionDialog dialog = new ShardIdSelectionDialog(getShell());
					dialog.setDatabaseInfo(dbInfo);
					dialog.setShardId(shardId);
					dialog.setShardVal(shardVal);
					dialog.setShardQueryType(shardQueryType);
					if (dialog.open() == IDialogConstants.OK_ID) {
						shardId = dialog.getShardId();
						shardVal = dialog.getShardVal();
						shardQueryType = dialog.getShardQueryType();
					}
				}
			}

			try {
				if (!QueryEditorUtil.isAvailableConnect(database)) {
					CommonUITool.openErrorBox(Messages.errQeditNotOpenForConnectionFull);
					return;
				}
				IEditorPart editor = window.getActivePage().openEditor(queryUnit,
						QueryEditorPart.ID);
				if (editor != null && database != null) {
					QueryEditorPart editorPart = (QueryEditorPart) editor;
					editorPart.connect(database);
					// [TOOLS-2425]Support shard broker
					if (dbInfo.isShard()) {
						editorPart.setShardId(shardId);
						editorPart.setShardVal(shardVal);
						editorPart.setShardQueryType(shardQueryType);
						editorPart.changeQueryEditorPartNameWithShard();
					}
				}
			} catch (PartInitException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}
}
