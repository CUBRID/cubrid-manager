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

import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.cubrid.common.core.common.model.Trigger;
import com.cubrid.common.core.util.ApplicationType;
import com.cubrid.common.core.util.ApplicationUtil;
import com.cubrid.common.ui.cubrid.trigger.dialog.CreateTriggerDialog;
import com.cubrid.common.ui.perspective.PerspectiveManager;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.loader.CubridTriggerFolderLoader;
import com.cubrid.common.ui.spi.util.ActionSupportUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;

/**
 * 
 * Create trigger action
 * 
 * @author wangmoulin
 * @version 1.0 - 2009-12-28 created by wangmoulin
 */
public class NewTriggerAction extends
		SelectionAction {

	public static final String ID = NewTriggerAction.class.getName();

	/**
	 * The constructor
	 * 
	 * @param shell
	 * @param text
	 * @param enabledIcon
	 * @param disabledIcon
	 */
	public NewTriggerAction(Shell shell, String text,
			ImageDescriptor enabledIcon, ImageDescriptor disabledIcon) {
		this(shell, null, text, enabledIcon, disabledIcon);
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
	public NewTriggerAction(Shell shell, ISelectionProvider provider,
			String text, ImageDescriptor enabledIcon,
			ImageDescriptor disabledIcon) {
		super(shell, provider, text, enabledIcon);
		this.setId(ID);
		this.setToolTipText(text);
		this.setDisabledImageDescriptor(disabledIcon);
	}

	/**
	 * 
	 * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections
	 *      ()
	 * @return false
	 */
	public boolean allowMultiSelections() {
		return true;
	}

	/**
	 * Sets this action support this object
	 * 
	 * @see org.eclipse.jface.action.IAction.ISelectionAction
	 * @param obj Object
	 * @return boolean
	 */
	public boolean isSupported(Object obj) {
		if (!ActionSupportUtil.isSupportMultiSelection(obj, null, false)) {
			return false;
		}

		if (ApplicationType.CUBRID_MANAGER.equals(PerspectiveManager.getInstance().getCurrentMode())) {
			return true;
		}

		CubridDatabase database = null;
		if (obj instanceof ISchemaNode) {
			ISchemaNode node = (ISchemaNode) obj;
			database = node.getDatabase();
		} else if (obj instanceof Object[]) {
			Object[] objArr = (Object[]) obj;
			ISchemaNode node = (ISchemaNode) objArr[0];
			database = node.getDatabase();
		}
		if (database != null
				&& database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
			return true;
		}
		return false;
	}

	/**
	 * Create trigger
	 */
	public void run() {
		Object[] obj = this.getSelectedObj();
		if (!isSupported(obj)) {
			setEnabled(false);
			return;
		}
		ISchemaNode triggerNode = (ISchemaNode) obj[0];
		CubridDatabase database = triggerNode.getDatabase();
		run(database);
	}
	
	/**
	 * Create trigger
	 * @param database CubridDatabase
	 */
	public void run (CubridDatabase database) {
		CreateTriggerDialog dlg = new CreateTriggerDialog(getShell(), database);
		ISelectionProvider provider = getSelectionProvider();
		if (dlg.open() == IDialogConstants.OK_ID
				&& (provider instanceof TreeViewer)) {
			TreeViewer treeViewer = (TreeViewer) provider;
			ICubridNode folderNode = database.getChild(database.getId()
					+ ICubridNodeLoader.NODE_SEPARATOR
					+ CubridTriggerFolderLoader.TRIGGER_FOLDER_ID);
			if (folderNode == null || !folderNode.getLoader().isLoaded()) {
				return;
			}
			String triggerName = dlg.getTriggerName().toLowerCase(
					Locale.getDefault());
			String id = folderNode.getId() + ICubridNodeLoader.NODE_SEPARATOR
					+ triggerName;
			Trigger trigger = new Trigger
				.Builder()
				.name(triggerName)
				.build();
			ICubridNode newNode = CubridTriggerFolderLoader.createTriggerNode(
					id, trigger);
			CommonUITool.addNodeToTree(treeViewer, folderNode, newNode);
			CommonUITool.updateFolderNodeLabelIncludingChildrenCount(treeViewer, folderNode);
			CubridNodeManager.getInstance().fireCubridNodeChanged(
					new CubridNodeChangedEvent(newNode,
							CubridNodeChangedEventType.NODE_ADD));
		}
	}
}