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
package com.cubrid.common.ui.cubrid.table.dialog;

import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;

/**
 * The dialog of Rename Table
 * 
 * @author pangqiren 2009-6-4
 */
public class RenameTableDialog extends
		CMTitleAreaDialog {

	private String oldName;
	private List<String> existNameList;
	private boolean isTable;
	private boolean isPhysical;

	private Text newTableText = null;
	private String newName;
	private String tableOrView;

	private boolean isSupportUserSchema;
	private String ownerName;

	public RenameTableDialog(Shell parentShell, String name, boolean isTable,
			List<String> nameList, boolean isPhysical, boolean isSupportUserSchema) {
		super(parentShell);
		this.oldName = name;
		this.existNameList = nameList;
		this.isTable = isTable;
		if (isTable) {
			tableOrView = Messages.renameTable;
		} else {
			tableOrView = Messages.renameView;
		}
		this.isPhysical = isPhysical;
		this.isSupportUserSchema = isSupportUserSchema;
	}

	/**
	 * @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize()
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		CommonUITool.centerShell(getShell());
		getShell().setText(Messages.bind(Messages.renameShellTitle, tableOrView, oldName));
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 * @param parent the button bar composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.renameOKBTN, false);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.renameCancelBTN, false);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 * @param buttonId the id of the button that was pressed (see
	 *        <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (!CommonUITool.openConfirmBox(Messages.renameTableDialogConfirmMsg)) {
				newTableText.setFocus();
				return;
			}
			newName = newTableText.getText().trim();
			//[TOOLS-4464] Table Name are always stored on the server in lower case.
			newName = newName.toLowerCase();
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @param parent The parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComp = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		Label label1 = new Label(composite, SWT.LEFT);
		label1.setText(Messages.bind(Messages.renameNewTableName, tableOrView));
		GridData data = new GridData();
		data.widthHint = 120;
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		label1.setLayoutData(data);

		newTableText = new Text(composite, SWT.BORDER);
		data = new GridData();
		data.horizontalSpan = 2;
		data.verticalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;

		if (isSupportUserSchema) {
			ownerName = oldName.substring(0, oldName.indexOf("."));
			oldName = oldName.substring(oldName.indexOf(".") + 1);
		}

		newTableText.setLayoutData(data);
		newTableText.setText(oldName);
		newTableText.selectAll();
		newTableText.setFocus();
		newTableText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				setErrorMessage(null);
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				String newTable = newTableText.getText();
				if (isPhysical && !ValidateUtil.isValidIdentifier(newTable)) {
					setErrorMessage(Messages.bind(Messages.renameInvalidTableNameMSG, tableOrView,
							newTable));
					return;
				}
				if (-1 != existNameList.indexOf(newTable.toLowerCase(Locale.getDefault()))) {
					if (isTable) {
						setErrorMessage(Messages.bind(Messages.errExistTable, newTable));
					} else {
						setErrorMessage(Messages.bind(Messages.errExistView, newTable));
					}
					return;
				}
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});

		newTableText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				if (e.type == SWT.KeyDown && e.character == SWT.CR) {
					/*For bug TOOLS-2698*/
					String newSchemaName = newTableText.getText().trim();
					if (StringUtil.isEqualNotIgnoreNull(oldName, newSchemaName)) {
						buttonPressed(IDialogConstants.CANCEL_ID);
					} else {
						buttonPressed(IDialogConstants.OK_ID);
					}
				}
			}
		});

		setTitle(Messages.bind(Messages.renameMSGTitle, tableOrView));
		setMessage(Messages.bind(Messages.renameDialogMSG, tableOrView));
		return parent;
	}

	public String getNewName() {
		return newName;

	}

	public String getOwnerName() {
		return ownerName;

	}
}
