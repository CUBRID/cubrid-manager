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
package com.cubrid.common.ui.cubrid.procedure.dialog;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.procedure.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;

/**
 * The Dialog of add function parameter
 * 
 * @author robin 2009-3-11
 */
public class AddFuncParamsDialog extends
		CMTitleAreaDialog {
	private org.eclipse.swt.widgets.List javaTypeList;
	private Combo paramTypeCombo;
	private Combo paramModelCombo;

	private CubridDatabase database = null;
	private final Map<String, String> model;
	private final Map<String, String> sqlTypeMap;
	private final Map<String, List<String>> javaTypeMap;
	private Text parameterNameText = null;
	private Text parameterDescriptionText = null;
	private Label javaTypeLabel = null;
	private Label javaTypeLabel2 = null;
	private Text javaTypeText;
	private final boolean newFlag;
	private final static String[] PARAM_MODEL_STRS = new String[]{"IN", "OUT",
			"INOUT" };
	private final List<Map<String, String>> paramList;
	private boolean isCommentSupport = false;

	public AddFuncParamsDialog(Shell parentShell, Map<String, String> model,
			Map<String, String> sqlTypeMap,
			Map<String, List<String>> javaTypeMap, boolean newFlag,
			List<Map<String, String>> paramList, CubridDatabase database) {
		super(parentShell);
		this.sqlTypeMap = sqlTypeMap;
		this.javaTypeMap = javaTypeMap;
		this.model = model;
		this.newFlag = newFlag;
		this.paramList = paramList;
		this.database = database;
	}

	/**
	 * Create dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the composite
	 */
	protected Control createDialogArea(Composite parent) {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
		Composite parentComp = (Composite) super.createDialogArea(parent);

		final Composite composite = new Composite(parentComp, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createdbNameGroup(composite);
		if (newFlag) {
			setTitle(Messages.titleAddFuncParamDialog);
			setMessage(Messages.msgAddFuncParamDialog);
		} else {
			setTitle(Messages.titleEditFuncParamDialog);
			setMessage(Messages.msgEditFuncParamDialog);
		}
		initial();
		return parentComp;
	}

	/**
	 * Create Database Name Group
	 * 
	 * @param composite the parent composite
	 */
	private void createdbNameGroup(Composite composite) {
		final Group dbnameGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.numColumns = 2;
		final GridData gdDbnameGroup = new GridData(GridData.FILL_BOTH);
		dbnameGroup.setLayoutData(gdDbnameGroup);
		dbnameGroup.setLayout(layout);

		final Label parameterNameLabel = new Label(dbnameGroup, SWT.LEFT
				| SWT.WRAP);

		parameterNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		parameterNameLabel.setText(Messages.lblParameterName);

		parameterNameText = new Text(dbnameGroup, SWT.BORDER);
		parameterNameText.setTextLimit(ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		parameterNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		parameterNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				setValidMessage();
			}
		});

		if (isCommentSupport) {
			final Label parameterDescriptionLabel = new Label(dbnameGroup, SWT.LEFT
					| SWT.WRAP);
			parameterDescriptionLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
			parameterDescriptionLabel.setText(Messages.lblParameterDescription);

			parameterDescriptionText = new Text(dbnameGroup, SWT.BORDER);
			parameterDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			parameterDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}

		final Label databaseName = new Label(dbnameGroup, SWT.LEFT | SWT.WRAP);

		databaseName.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		databaseName.setText(Messages.lblSqlType);

		paramTypeCombo = new Combo(dbnameGroup, SWT.SINGLE);
		paramTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		paramTypeCombo.setVisibleItemCount(10);
		paramTypeCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				String name = paramTypeCombo.getText();
				if (sqlTypeMap.containsKey(name.toUpperCase(Locale.getDefault()))) {
					setJavaTypeEnable(true);
				} else {
					setJavaTypeEnable(false);
				}
				String level = sqlTypeMap.get(name.toUpperCase(Locale.getDefault()));

				paramTypeCombo.setData(level);
				if (level == null) {
					return;
				}
				if (("4").equals(level)) {
					setJavaTypeEnable(false);
				} else {
					setJavaTypeEnable(true);
					List<String> list = javaTypeMap.get(level);
					javaTypeList.removeAll();
					for (String tmp : list) {
						javaTypeList.add(tmp);
					}
					javaTypeList.select(0);
				}

				setValidMessage();

			}
		});

		javaTypeLabel2 = new Label(dbnameGroup, SWT.LEFT | SWT.WRAP);
		javaTypeLabel2.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		javaTypeLabel2.setText(Messages.lblSpecialJavaType);
		javaTypeLabel2.setEnabled(false);
		javaTypeText = new Text(dbnameGroup, SWT.BORDER);
		javaTypeText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		javaTypeText.setEnabled(false);
		javaTypeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				setValidMessage();
			}
		});
		javaTypeLabel = new Label(dbnameGroup, SWT.LEFT | SWT.WRAP);

		javaTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		javaTypeLabel.setText(Messages.lblJavaType);
		javaTypeList = new org.eclipse.swt.widgets.List(dbnameGroup, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		javaTypeList.setLayoutData(gd);
		javaTypeList.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				//
			}

			public void widgetSelected(SelectionEvent event) {
				setValidMessage();

			}
		});

		final Label paramModelLabel = new Label(dbnameGroup, SWT.LEFT
				| SWT.WRAP);

		paramModelLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
		paramModelLabel.setText(Messages.lblParamModel);

		paramModelCombo = new Combo(dbnameGroup, SWT.SINGLE | SWT.READ_ONLY);
		paramModelCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
	}

	/**
	 * 
	 * Initial the data
	 * 
	 */
	private void initial() {
		/** init the combo * */
		for (String name : sqlTypeMap.keySet()) {
			if (!"0".equals(sqlTypeMap.get(name))) {
				paramTypeCombo.add(name);
			}
		}
		paramTypeCombo.select(0);
		for (String t : PARAM_MODEL_STRS) {
			paramModelCombo.add(t);
		}
		paramModelCombo.select(0);

		/** update the value* */
		if (newFlag) {
			List<String> list = javaTypeMap.get("1");
			javaTypeList.removeAll();
			for (int i = 0; i < list.size(); i++) {
				String tmp = list.get(i);
				javaTypeList.add(tmp);
			}
			javaTypeList.setSelection(0);
			paramModelCombo.select(0);
		} else {
			String name = model.get("0");
			String sqlType = model.get("1");
			String javaType = model.get("2");
			String paramModel = model.get("3");

			if (name != null && !"".equals(name)) {
				parameterNameText.setText(name);
			}
			if (sqlType != null && !"".equals(sqlType)) {
				paramTypeCombo.setText(sqlType);
				String level = sqlTypeMap.get(sqlType);
				if (level == null || "".equals(level)) {
					level = "-1";
				}
				paramTypeCombo.setData(level);
				if ("4".equals(level) || "-1".equals(level)) {
					setJavaTypeEnable(false);
					javaTypeText.setText(javaType);
				} else {
					setJavaTypeEnable(true);
					List<String> list = javaTypeMap.get(sqlTypeMap.get(sqlType));
					javaTypeList.removeAll();
					for (String tmp : list) {
						javaTypeList.add(tmp);
					}

					for (int i = 0; i < javaTypeList.getItemCount(); i++) {
						String type = javaTypeList.getItem(i);
						if (javaType.equals(type)) {
							javaTypeList.select(i);
						}
					}

				}
				for (int i = 0; i < paramModelCombo.getItemCount(); i++) {
					String type = paramModelCombo.getItem(i);
					if (paramModel.equals(type)) {
						paramModelCombo.select(i);
					}
				}
			}
		}
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		// getShell().setSize(400, 420);
		CommonUITool.centerShell(getShell());
		if (newFlag) {
			getShell().setText(Messages.titleAddFuncParamDialog);
		} else {
			getShell().setText(Messages.titleEditFuncParamDialog);
		}
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				com.cubrid.common.ui.common.Messages.btnOK, false);
		String msg = getValidInput();
		setMessage(msg);
		if (msg == null || "".equals(msg)) {
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		} else {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}

		createButton(parent, IDialogConstants.CANCEL_ID,
				com.cubrid.common.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When button press,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (!verify()) {
				return;
			}
			if (model == null) {
				return;
			}
			String name = parameterNameText.getText();
			String sqlType = paramTypeCombo.getText();
			String paramModel = paramModelCombo.getText();
			model.put("0", name);
			model.put("1", sqlType);
			if ("4".equals(paramTypeCombo.getData())
					|| "-1".equals(paramTypeCombo.getData())) {
				String javaType = javaTypeText.getText();
				model.put("2", javaType);
			} else {
				String[] javaType = javaTypeList.getSelection();
				model.put("2", javaType[0]);
			}
			model.put("3", paramModel);
		}

		super.buttonPressed(buttonId);
	}

	/**
	 * 
	 * Verify the input
	 * 
	 * @return <code>true</code> it is valid;<code>false</code> otherwise
	 */
	private boolean verify() {
		String msg = getValidInput();
		if (msg != null && "".equals(msg)) {
			CommonUITool.openErrorBox(getShell(), msg);
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	@Override
	public boolean isHelpAvailable() {
		return true;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}

	public CubridDatabase getDatabase() {
		return database;
	}

	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	/**
	 * 
	 * Set message for message title
	 * 
	 */
	private void setValidMessage() {
		String msg = getValidInput();
		setErrorMessage(msg);
		if (getButton(IDialogConstants.OK_ID) != null) {
			if (msg == null || "".equals(msg)) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			} else {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}
	}

	/**
	 * 
	 * Get input
	 * 
	 * @return the string
	 */
	private String getValidInput() {
		String paramsName = parameterNameText.getText();
		if (paramsName == null || paramsName.length() == 0) {
			return Messages.errInputParameterName;
		}
		if (paramsName.length() > ValidateUtil.MAX_SCHEMA_NAME_LENGTH) {
			return Messages.bind(Messages.errInputParameterNameLength,
					ValidateUtil.MAX_SCHEMA_NAME_LENGTH);
		}
		if (newFlag) {
			for (Map<String, String> map : paramList) {
				String name = map.get("0");
				if (name != null && name.equalsIgnoreCase(paramsName)) {
					return Messages.errInputParameterNameDuplicate;
				}
			}
		}
		if (!ValidateUtil.isValidIdentifier(paramsName)) {
			return Messages.bind(Messages.errInputParameterNameValid, paramsName);
		}
		String paramType = paramTypeCombo.getText();

		if (paramType == null || "".equals(paramType)) {
			return Messages.errInputSqlType;
		}
		// if (!sqlTypeMap.containsKey(paramType.toUpperCase()))
		// return "Please input the valid sql type";
		if ("4".equals(paramTypeCombo.getData())
				|| "-1".equals(paramTypeCombo.getData())) {
			String javaType = javaTypeText.getText();
			if (javaType == null || "".equals(javaType)) {
				return Messages.msgSelectSpecialJavaConfirm;
			}
		} else {
			String[] javaType = javaTypeList.getSelection();
			if (javaType.length != 1) {
				return Messages.msgSelectJavaConfirm;
			}
		}

		return null;

	}

	/**
	 * 
	 * Enable or disable the java type related composite
	 * 
	 * @param flag whether enabled
	 */
	private void setJavaTypeEnable(boolean flag) {
		javaTypeList.setEnabled(flag);
		javaTypeList.setSelection(flag ? 0 : -1);
		javaTypeLabel.setEnabled(flag);
		javaTypeLabel2.setEnabled(!flag);
		javaTypeText.setEnabled(!flag);
		setJavaTypeList();
	}

	/**
	 * 
	 * Set java type list
	 * 
	 */
	private void setJavaTypeList() {
		String sqlType = paramTypeCombo.getText();
		if (StringUtil.isEmpty(sqlType)) {
			return;
		}
		String level = sqlTypeMap.get(sqlType.toUpperCase(Locale.getDefault()));
		if (StringUtil.isEmpty(level)) {
			paramTypeCombo.setData("-1");
			return;
		}
		paramTypeCombo.setData(level);
		List<String> list = javaTypeMap.get(level);
		javaTypeList.removeAll();
		for (String tmp : list) {
			javaTypeList.add(tmp);
		}
		javaTypeList.select(0);
	}
}
