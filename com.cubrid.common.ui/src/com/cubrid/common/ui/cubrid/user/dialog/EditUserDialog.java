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
package com.cubrid.common.ui.cubrid.user.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.user.Messages;
import com.cubrid.common.ui.spi.TableContentProvider;
import com.cubrid.common.ui.spi.TableLabelProvider;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTrayDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassAuthorizations;
import com.cubrid.cubridmanager.core.cubrid.table.model.ClassInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllClassListTask;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.task.ChangeDbUserCommentTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.ChangeDbUserPwdTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.CreateUserTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.DropUserTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.UpdateAddUserJdbcTask;
import com.cubrid.cubridmanager.core.utils.ModelUtil.ClassType;


/**
 * Show the edit user dialog
 * 
 * @author robin 2009-3-18
 */
public class EditUserDialog extends CMTrayDialog {
	private static final Logger LOGGER = LogUtil.getLogger(EditUserDialog.class);
	private Table classTable;
	private final List<Map<String, String>> classListData = new ArrayList<Map<String, String>>();
	private TableViewer classTableViewer;
	private final List<Map<String, String>> memberListData = new ArrayList<Map<String, String>>();
	private TableViewer memberTableViewer;
	private Table allUserTable;
	private final List<Map<String, String>> allUserListData = new ArrayList<Map<String, String>>();
	private TableViewer allUserTableViewer;
	private final List<Map<String, String>> groupListData = new ArrayList<Map<String, String>>();
	private TableViewer groupTableViewer;
	private Table authTable;
	private final List<Map<String, Object>> authListData = new ArrayList<Map<String, Object>>();
	private final List<Map<String, Object>> authListDataOld = new ArrayList<Map<String, Object>>();
	private TableViewer authTableViewer;
	private Map<String, ClassAuthorizations> classGrantMap;
	private Table userGroupTable;
	private Button removeGroupBtn;
	private Button changePwdBtn;
	private Button revokeButton;
	private Text pwdCfmText;
	private Text pwdText;
	private Text oldPwdText;
	private Text userNameText;
	private Text userDescriptionText;
	private CubridDatabase database = null;
	private DbUserInfo currentUserInfo;
	private Map<String, ClassAuthorizations> currentUserAuthorizations;
	private CTabFolder tabFolder;
	private Button grantButton = null;
	private Composite parentComp;
	private Button buttonAddGroup;
	private List<ClassInfo> allClassInfoList;
	private DbUserInfoList userListInfo;
	private String userName;
	private DbUserInfo userInfo = null;
	private String oldLoginPassword = "";
	private boolean newFlag = false;
	public final static String DB_DEFAULT_USERNAME = "public";
	public final static String DB_DBA_USERNAME = "dba";
	private Map<String, String> partitionClassMap;
	private String inputtedPassword = null;
	private boolean isCommentSupport = false;
	private boolean isCommentModified = false;

	public EditUserDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create the dialog area content
	 * 
	 * @param parent the parent composite
	 * @return the composite
	 */
	protected Control createDialogArea(Composite parent) {
		parentComp = (Composite) super.createDialogArea(parent);

		tabFolder = new CTabFolder(parentComp, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		tabFolder.setLayout(layout);

		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setText(Messages.tabItemGeneral);
		item.setControl(createUserComposite());
		
		CTabItem authItem = null;
		if (!DB_DBA_USERNAME.equalsIgnoreCase(userName)) {
			authItem = new CTabItem(tabFolder, SWT.NONE);
			authItem.setText(Messages.tabItemAuthoration);
			Composite authComposite = createAuthComposite();
			authItem.setControl(authComposite);
		}
		/*init the data*/
		initial();

		/*The dba user group don't display the authItem*/
		if (isDBAGroup() && authItem != null) {
			authItem.dispose();
		}
		
		userNameText.setFocus();
		return parentComp;
	}

	/**
	 * Create user composite
	 * 
	 * @return the composite
	 */
	private Composite createUserComposite() {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		createUserPwdGroup(composite);

		final Group userMemberGroup = new Group(composite, SWT.NONE);
		userMemberGroup.setText(Messages.grpUserMemberInfo);
		layout = new GridLayout();
		layout.numColumns = 2;
		userMemberGroup.setLayout(layout);

		GridData gdUserMemberGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
		userMemberGroup.setLayoutData(gdUserMemberGroup);

		createAllUserComposit(userMemberGroup);
		createGroupListComposite(userMemberGroup);

		return composite;
	}

	/**
	 * Create the user password group
	 * 
	 * @param composite the parent composite
	 */
	private void createUserPwdGroup(Composite composite) {
		isCommentSupport = CompatibleUtil.isCommentSupports(database.getDatabaseInfo());
		final Group userNameGroup = new Group(composite, SWT.NONE);
		final GridData gdUserPasswordGroup = new GridData(SWT.FILL, SWT.FILL, true, false);
		userNameGroup.setLayoutData(gdUserPasswordGroup);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		userNameGroup.setLayout(layout);

		final Label userNameLabel = new Label(userNameGroup, SWT.NONE);
		final GridData gdUserNameLabel = new GridData(SWT.FILL, SWT.FILL, false, false);
		userNameLabel.setLayoutData(gdUserNameLabel);
		userNameLabel.setText(Messages.lblUserName);

		userNameText = new Text(userNameGroup, SWT.BORDER);
		userNameText.setEnabled(false);
		final GridData gdUserNameText = new GridData(SWT.FILL, SWT.FILL, true, false);
		userNameText.setLayoutData(gdUserNameText);
		userNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
			}

			public void keyReleased(KeyEvent event) {
				String userName = userNameText.getText();
				if (null == userName || "".equals(userName)
						|| userName.length() <= 0) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
		});

		if (isCommentSupport) {
			Label userDescriptionLabel = new Label(userNameGroup, SWT.NONE);
			userDescriptionLabel.setLayoutData(gdUserNameLabel);
			userDescriptionLabel.setText(Messages.lblUserDescription);

			userDescriptionText = new Text(userNameGroup, SWT.BORDER);
			userDescriptionText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
			userDescriptionText.setLayoutData(gdUserNameText);
			userDescriptionText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					isCommentModified = true;
				}
			});
		}

		final Group userPasswordGroup = new Group(userNameGroup, SWT.NONE);
		final GridData gdPasswordGroup = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdPasswordGroup.horizontalSpan = 2;
		userPasswordGroup.setLayoutData(gdPasswordGroup);
		layout = new GridLayout();
		layout.numColumns = 2;
		userPasswordGroup.setLayout(layout);
		userPasswordGroup.setText(Messages.grpPasswordSetting);
		if (newFlag) {
			final Label passwordLabel = new Label(userPasswordGroup, SWT.NONE);
			passwordLabel.setText(Messages.lblPassword);

			pwdText = new Text(userPasswordGroup, SWT.BORDER | SWT.PASSWORD);
			final GridData gdPwdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
			pwdText.setLayoutData(gdPwdText);
			final Label newPasswordConfirmLabel = new Label(userPasswordGroup, SWT.NONE);
			newPasswordConfirmLabel.setText(Messages.lblPasswordConf);

			pwdCfmText = new Text(userPasswordGroup, SWT.BORDER | SWT.PASSWORD);
			final GridData gdPwdCfmText = new GridData(SWT.FILL, SWT.CENTER, true, false);
			pwdCfmText.setLayoutData(gdPwdCfmText);
		} else {
			changePwdBtn = new Button(userPasswordGroup, SWT.CHECK);
			changePwdBtn.setText(Messages.btnPasswordChange);
			changePwdBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			changePwdBtn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent event) {
					if (changePwdBtn.getSelection()) {
						if (!database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(DB_DBA_USERNAME)
								|| (database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(DB_DBA_USERNAME) && DB_DBA_USERNAME.equalsIgnoreCase(userName))) {
							oldPwdText.setEnabled(true);
						}
						pwdText.setEnabled(true);
						pwdCfmText.setEnabled(true);

					} else {
						oldPwdText.setEnabled(false);
						pwdText.setEnabled(false);
						pwdCfmText.setEnabled(false);
					}
				}

			});
			final Label oldPasswordLabel = new Label(userPasswordGroup, SWT.NONE);
			oldPasswordLabel.setText(Messages.lblOldPassword);

			oldPwdText = new Text(userPasswordGroup, SWT.BORDER | SWT.PASSWORD);
			final GridData gdOldPwdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
			oldPwdText.setLayoutData(gdOldPwdText);
			oldPwdText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);

			final Label passwordLabel = new Label(userPasswordGroup, SWT.NONE);
			passwordLabel.setText(Messages.lblNewPassword);

			pwdText = new Text(userPasswordGroup, SWT.BORDER | SWT.PASSWORD);
			pwdText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
			final GridData gdPwdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
			pwdText.setLayoutData(gdPwdText);
			final Label newPasswordConfirmLabel = new Label(userPasswordGroup, SWT.NONE);
			newPasswordConfirmLabel.setText(Messages.lblNewPasswordConf);

			pwdCfmText = new Text(userPasswordGroup, SWT.BORDER | SWT.PASSWORD);
			final GridData gdPwdCfmText = new GridData(SWT.FILL, SWT.CENTER, true, false);
			pwdCfmText.setLayoutData(gdPwdCfmText);
			pwdCfmText.setTextLimit(ValidateUtil.MAX_PASSWORD_LENGTH);
			oldPwdText.setEnabled(false);

			pwdText.setEnabled(false);
			pwdCfmText.setEnabled(false);
		}
	}

	/**
	 * Create all user composite
	 * 
	 * @param group the group composite
	 */
	private void createAllUserComposit(Group group) {
		Composite cmpAllUsers = new Composite(group, SWT.NONE);
		final GridData gdCmpAllUsers = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		cmpAllUsers.setLayoutData(gdCmpAllUsers);
		cmpAllUsers.setLayout(new GridLayout());

		final Label allUsersLabel = new Label(cmpAllUsers, SWT.NONE);
		allUsersLabel.setText(Messages.lblAllUser);

		final String[] userColumnNameArr = new String[]{"col1" };
		allUserTableViewer = CommonUITool.createCommonTableViewer(cmpAllUsers, null, userColumnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 4, -1, 200));
		allUserTable = allUserTableViewer.getTable();

		allUserTableViewer.setInput(allUserListData);
		allUserTable.setLinesVisible(false);
		allUserTable.setHeaderVisible(false);
		allUserTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setBtnEnableDisable();
			}
		});
	}

	/**
	 * Create the group list composite
	 * 
	 * @param group the group composite
	 */
	private void createGroupListComposite(Group group) {
		Composite cmpRightAreaGroup = new Composite(group, SWT.NONE);
		final GridData gdCmpAllUsers = new GridData(SWT.FILL, SWT.FILL, true, true);
		cmpRightAreaGroup.setLayoutData(gdCmpAllUsers);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 10;
		gridLayout.numColumns = 2;
		cmpRightAreaGroup.setLayout(gridLayout);

		new Label(cmpRightAreaGroup, SWT.NONE);

		final Label groupListLabel = new Label(cmpRightAreaGroup, SWT.NONE);
		groupListLabel.setText(Messages.grpUserGroupList);

		GridData gdButtonAddGroup = new GridData(SWT.FILL, SWT.BOTTOM, false, true);
		buttonAddGroup = new Button(cmpRightAreaGroup, SWT.NONE);
		buttonAddGroup.setEnabled(false);
		buttonAddGroup.setText(Messages.btnAddGroup);
		buttonAddGroup.setLayoutData(gdButtonAddGroup);
		buttonAddGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] idx = allUserTable.getSelectionIndices();
				if (idx.length < 0) {
					return;
				}
				for (int i : idx) {
					TableItem item = new TableItem(userGroupTable, SWT.NONE);
					item.setText(0, allUserTable.getItem(i).getText(0));
				}
				allUserTable.remove(idx);
				setBtnEnableDisable();
				packTable(allUserTable);
				packTable(userGroupTable);
				packTable(memberTableViewer.getTable());
			}
		});

		final String[] groupColumnNameArr = new String[]{"col" };
		groupTableViewer = CommonUITool.createCommonTableViewer(
				cmpRightAreaGroup, null, groupColumnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 2, -1, 70));
		groupTableViewer.setInput(groupListData);
		userGroupTable = groupTableViewer.getTable();
		userGroupTable.setLinesVisible(false);
		userGroupTable.setHeaderVisible(false);
		userGroupTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setBtnEnableDisable();
			}
		});
		removeGroupBtn = new Button(cmpRightAreaGroup, SWT.NONE);
		removeGroupBtn.setEnabled(false);
		final GridData gdButtonRemoveGroup = new GridData(SWT.LEFT, SWT.TOP, false, true);
		removeGroupBtn.setLayoutData(gdButtonRemoveGroup);
		removeGroupBtn.setText(Messages.btnRemoveGroup);
		removeGroupBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] idxs = userGroupTable.getSelectionIndices();
				if (idxs.length < 0) {
					return;
				}
				for (int idx : idxs) {
					if (!(userNameText.getText().equalsIgnoreCase("dba"))
							&& userGroupTable.getItem(idx).getText(0).equalsIgnoreCase("public")) {
						CommonUITool.openErrorBox(parentComp.getShell(), Messages.errRomoveUserGroup);
						return;
					}
				}
				for (int i : idxs) {
					TableItem item = new TableItem(allUserTable, SWT.NONE);
					item.setText(0, userGroupTable.getItem(i).getText(0));
				}

				userGroupTable.remove(idxs);
				setBtnEnableDisable();
				packTable(allUserTable);
				packTable(userGroupTable);
				packTable(memberTableViewer.getTable());
			}
		});
		new Label(cmpRightAreaGroup, SWT.NONE);
		Label tmpLabel = new Label(cmpRightAreaGroup, SWT.NONE);
		tmpLabel.setText(Messages.grpUserMemberList);
		new Label(cmpRightAreaGroup, SWT.NONE);
		memberTableViewer = CommonUITool.createCommonTableViewer(
				cmpRightAreaGroup, null, groupColumnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 1, 2, -1, 70));
		memberTableViewer.setInput(memberListData);
		memberTableViewer.getTable().setLinesVisible(false);
		memberTableViewer.getTable().setHeaderVisible(false);
	}

	/**
	 * Create auth composite
	 * 
	 * @return the composite
	 */
	private Composite createAuthComposite() {
		final Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		Label classTableDescLabel = new Label(composite, SWT.NONE);
		classTableDescLabel.setText(Messages.lblUnAuthorizedTable);
		final String[] columnNameArr = new String[]{Messages.tblColClassName,
				Messages.tblColClassSchematype, Messages.tblColClassOwner,
				Messages.tblColClassType };
		classTableViewer = CommonUITool.createCommonTableViewer(composite,
				new TableViewerSorter(), columnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
		classTableViewer.setInput(classListData);
		classTable = classTableViewer.getTable();

		classTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setAuthBtnEnableDisable();
			}
		});

		final Composite cmpControl = new Composite(composite, SWT.NONE);
		final GridData gdCmpControl = new GridData(SWT.CENTER, SWT.FILL, false, false);
		cmpControl.setLayoutData(gdCmpControl);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		cmpControl.setLayout(gridLayout);

		grantButton = new Button(cmpControl, SWT.LEFT);
		grantButton.setEnabled(false);
		grantButton.setText(Messages.addClassButtonName);
		grantButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] idx = classTable.getSelectionIndices();
				if (idx.length < 0) {
					return;
				}

				for (int i : idx) {
					String className = classTable.getItem(i).getText(0);
					for (Map<String, String> map : classListData) {
						if (map.get("0").equals(className)) {
							classListData.remove(map);
							break;
						}
					}

					ClassAuthorizations classAuthorizations = classGrantMap.get(className);
					
					if (classAuthorizations == null) {
						classAuthorizations = new ClassAuthorizations();
						classAuthorizations.setClassName(className);
						classAuthorizations.setSelectPriv(true);
					}
					authListData.add(getItemAuthMap(classAuthorizations));
				}
				classTableViewer.refresh();
				authTableViewer.refresh();
				if (authTableViewer.getTable().getColumn(0) != null) {
					authTableViewer.getTable().getColumn(0).pack();
				}
				setAuthBtnEnableDisable();
			}
		});

		revokeButton = new Button(cmpControl, SWT.NONE);
		revokeButton.setEnabled(false);
		revokeButton.setText(Messages.deleteClassButtonName);
		revokeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int[] idx = authTable.getSelectionIndices();
				if (idx.length < 0) {
					return;
				}
				for (int id : idx) {
					String tableName = authTable.getItem(id).getText(0);
					for (ClassInfo bean : allClassInfoList) {
						if (tableName.equals(bean.getFullName())) {
							if (bean.isSystemClass()) {
								CommonUITool.openErrorBox(parentComp.getShell(), Messages.errRemoveSysClass);
								return;
							} else {
								Map<String, String> map = new HashMap<String, String>();
								map.put("0", bean.getUniqueName());
								map.put("1", bean.isSystemClass() ? Messages.msgSystemSchema : Messages.msgUserSchema);
								map.put("2", bean.getOwnerName());
								map.put("3", bean.getClassType() == ClassType.VIEW ? Messages.msgVirtualClass : Messages.msgClass);
								classListData.add(map);
							}
						}
					}
					for (Map<String, Object> map : authListData) {
						String className = (String) map.get("0");
						if (tableName.equals(className)) {
							authListData.remove(map);
							break;
						}
					}

				}
				authTableViewer.refresh();
				classTableViewer.refresh();
				setAuthBtnEnableDisable();
			}
		});
		Label authTableDescLabel = new Label(composite, SWT.NONE);
		authTableDescLabel.setText(Messages.lblAuthorizedTable);
		final String[] authColumnNameArr = new String[]{
				Messages.tblColAuthTable, Messages.tblColAuthSelect,
				Messages.tblColAuthInsert, Messages.tblColAuthUpdate,
				Messages.tblColAuthDelete, Messages.tblColAuthAlter,
				Messages.tblColAuthIndex, Messages.tblColAuthExecute,
				Messages.tblColAuthGrantselect, Messages.tblColAuthGrantinsert,
				Messages.tblColAuthGrantupdate, Messages.tblColAuthGrantdelete,
				Messages.tblColAuthGrantalter, Messages.tblColAuthGrantindex,
				Messages.tblColAuthGrantexecute

		};
		authTableViewer = createCommonTableViewer(composite, authColumnNameArr,
				CommonUITool.createGridData(GridData.FILL_BOTH, 3, 1, -1, 200));
		authTableViewer.setLabelProvider(new AuthTableLabelProvider());

		authTableViewer.setInput(authListData);
		authTable = authTableViewer.getTable();
		CellEditor[] editors = new CellEditor[15];
		editors[0] = null;
		for (int i = 1; i < 15; i++) {
			editors[i] = new CheckboxCellEditor(authTable, SWT.READ_ONLY);
		}

		authTableViewer.setColumnProperties(authColumnNameArr);
		authTableViewer.setCellEditors(editors);
		authTableViewer.setCellModifier(new ICellModifier() {
			@SuppressWarnings("unchecked")
			public boolean canModify(Object element, String property) {
				Map<String, Object> map = (Map<String, Object>) element;
				boolean isDbaAuthority = database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority();
				if (isDbaAuthority) {
					return true;
				}
				/*Can't grant/revoke for  current login user*/
				if (StringUtil.isEqual(userName, currentUserInfo.getName())) {
					return false;
				}
				String name = (String) map.get("0");
				for (ClassInfo bean : allClassInfoList) {
					if (name.equals(bean.getUniqueName())) {
						if (bean.isSystemClass()) {
							return false;
						} else if (currentUserInfo.getName().equalsIgnoreCase(bean.getOwnerName())) {
							return true;
						}
					}
				}

				ClassAuthorizations authorizations = currentUserAuthorizations.get(name);
				if (authorizations == null || authorizations.isAllPriv() || authorizations.isPriv(property)) {
					return true;
				} else {
					return false;
				}
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				Map<String, Object> map = (Map<String, Object>) element;
				for (int i = 1; i < 15; i++) {
					if (property.equals(authColumnNameArr[i])) {
						return Boolean.valueOf((Boolean) map.get("" + i));
					}
				}
				return null;
			}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value) {
				Object elementData;
				elementData = element;
				if (element instanceof Item) {
					elementData = ((Item) element).getData();
				}
				String key = "";
				Map<String, Object> map = (Map<String, Object>) elementData;
				for (int i = 1; i < 15; i++) {
					if (property.equals(authColumnNameArr[i])) {
						key = "" + i;
						break;
					}
				}

				if (value instanceof Boolean) {
					map.put(key, ((Boolean) value).booleanValue());
				}

				authTableViewer.refresh();
			}
		});

		authTable.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}

			public void widgetSelected(SelectionEvent event) {
				setAuthBtnEnableDisable();
			}
		});

		authTable.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent event) {
				setAuthBtnEnableDisable();
			}
		});

		return composite;
	}

	/**
	 * Constrain the shell size
	 */
	protected void constrainShellSize() {
		super.constrainShellSize();
		getShell().setSize(680, 650);
		CommonUITool.centerShell(getShell());
		if (isNewFlag()) {
			getShell().setText(Messages.msgAddUserDialog);
		} else {
			getShell().setText(Messages.msgEditUserDialog);
		}
	}

	/**
	 * Create buttons for button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, com.cubrid.common.ui.common.Messages.btnOK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, com.cubrid.common.ui.common.Messages.btnCancel, false);
	}

	/**
	 * When press button,call it
	 * 
	 * @param buttonId the button id
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (!valid()) {
				return;
			}
			
			boolean isChangePassword = false;
			if (newFlag) {
				isChangePassword = true;
			} else if (changePwdBtn.getSelection()) {
				isChangePassword = true;
			}
			String password = null;
			if (isChangePassword) {
				password = pwdText.getText();
				if (password == null) {
					password = "";
				}
			}
			
			this.inputtedPassword = password;
			
			boolean isNoDbaAuthUser = database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(userName)
					&& !database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority();
			String taskName = Messages.bind(Messages.addOrUpdateUserTaskName, userNameText.getText());
			TaskExecutor taskExecutor = new CommonTaskExec(taskName);
			ITask task = null;
			boolean isDropAndCreateUser = false;
			if (isNoDbaAuthUser) {
				if (isChangePassword) {
					task = new ChangeDbUserPwdTask(
							database.getDatabaseInfo());
					ChangeDbUserPwdTask changeDbUserPwdTask = (ChangeDbUserPwdTask) task;
					changeDbUserPwdTask.setDbUserName(userName);
					changeDbUserPwdTask.setNewPwd(password);
					taskExecutor.addTask(changeDbUserPwdTask);
				} else {
					super.buttonPressed(IDialogConstants.CANCEL_ID);
					return;
				}
			} else {
				if (newFlag) {
					List<String> groupList = new ArrayList<String>();
					for (int i = 0; i < userGroupTable.getItemCount(); i++) {
						groupList.add(userGroupTable.getItem(i).getText(0));
					}
					List<String> memberList = new ArrayList<String>();
					for (Map<String, String> map : memberListData) {
						memberList.add(map.get("0").toLowerCase(Locale.getDefault()));
					}
					task = new CreateUserTask(database.getDatabaseInfo(),
							userNameText.getText(), pwdText.getText(),
							groupList, memberList, isCommentSupport ? userDescriptionText.getText() : null);
					taskExecutor.addTask(task);
				} else {
					if (!groupChange()) {
						task = new DropUserTask(database.getDatabaseInfo(), userName);
						taskExecutor.addTask(task);
						List<String> groupList = new ArrayList<String>();
						for (int i = 0; i < userGroupTable.getItemCount(); i++) {
							groupList.add(userGroupTable.getItem(i).getText(0));
						}
						List<String> memberList = new ArrayList<String>();
						for (Map<String, String> map : memberListData) {
							memberList.add(map.get("0").toLowerCase(Locale.getDefault()));
						}
						task = new CreateUserTask(database.getDatabaseInfo(),
								userNameText.getText(), pwdText.getText(),
								groupList, memberList, isCommentSupport ? userDescriptionText.getText() : null);
						taskExecutor.addTask(task);

						isDropAndCreateUser = true;
					} else if (isChangePassword) {
						task = new ChangeDbUserPwdTask(database.getDatabaseInfo());
						ChangeDbUserPwdTask changeDbUserPwdTask = (ChangeDbUserPwdTask) task;
						changeDbUserPwdTask.setDbUserName(userName);
						changeDbUserPwdTask.setNewPwd(password);
						taskExecutor.addTask(task);
					} else if (isCommentModified) {
						task = new ChangeDbUserCommentTask(database.getDatabaseInfo());
						ChangeDbUserCommentTask changeDbUserCommentTask = (ChangeDbUserCommentTask) task;
						changeDbUserCommentTask.setDbUserName(userName);
						changeDbUserCommentTask.setNewDescription(userDescriptionText.getText());
						taskExecutor.addTask(task);
					}
				}
			}
			
			task = new UpdateAddUserJdbcTask(database.getDatabaseInfo(), userNameText.getText(),
					classGrantMap, authListData, authListDataOld, isDBAGroup(), isDropAndCreateUser);
			taskExecutor.addTask(task);
			new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
			if (taskExecutor.isSuccess()
					&& isChangePassword
					&& database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(
							userName)) {
				database.getDatabaseInfo().getAuthLoginedDbUserInfo().setNoEncryptPassword(
						password);
			}
			if (taskExecutor.isSuccess()) {
				String message = null;
				if (newFlag) {
					message = Messages.msgAddUserSuccess;
				} else {
					message = Messages.msgModifyUserSuccess;
				}
				CommonUITool.openInformationBox(Messages.msgInformation, message);
			} else {
				return;
			}
		}

		super.buttonPressed(buttonId);
	}

	/**
	 * Initial data
	 */
	private void initial() {
		if (!CubridDatabase.hasValidDatabaseInfo(database)) {
			LOGGER.error("The database is invalid.");
			return;
		}

		this.currentUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
		currentUserAuthorizations = currentUserInfo.getUserAuthorizations();
		for (DbUserInfo userInfo : userListInfo.getUserList()) {
			if (userInfo.getName().equals(currentUserInfo.getName())) {
				currentUserInfo = userInfo;
				currentUserAuthorizations = currentUserInfo.getUserAuthorizations();
				break;
			}
		}
		
		List<String> groupList = null;
		if (newFlag) {
			userNameText.setEnabled(true);
			classGrantMap = new HashMap<String, ClassAuthorizations>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("0", DB_DEFAULT_USERNAME);
			groupList = new ArrayList<String>();
			groupList.add(DB_DEFAULT_USERNAME);
		} else {
			for (DbUserInfo bean : userListInfo.getUserList()) {
				if (bean.getName().equalsIgnoreCase(userName)) {
					userInfo = bean;
				}
				List<String> groups = bean.getGroups().getGroup();
				if (groups != null) {
					for (String g : groups) {
						if (userName != null && userName.equalsIgnoreCase(g)) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("0", bean.getName());
							memberListData.add(map);
						}
					}
				}
			}
			memberTableViewer.refresh();

			if (database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(userName)
					|| database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName().equalsIgnoreCase(DB_DBA_USERNAME)) {
				changePwdBtn.setEnabled(true);
			} else {
				changePwdBtn.setEnabled(false);
			}

			userNameText.setText(userInfo.getName());
			String description = userInfo.getDescription();
			if (isCommentSupport && StringUtil.isNotEmpty(description)) {
				userDescriptionText.setText(description);
			}
			groupList = userInfo.getGroups().getGroup();
			classGrantMap = userInfo.getUserAuthorizations();
			oldLoginPassword = database.getDatabaseInfo().getAuthLoginedDbUserInfo().getNoEncryptPassword();
			if (oldLoginPassword == null) {
				oldLoginPassword = "";
			}
		}

		Map<String, String> groupMap = new HashMap<String, String>();
		Map<String, String> memberMap = new HashMap<String, String>();

		// set group map
		if (groupList == null) {
			groupList = new ArrayList<String>();
		}

		for (String group : groupList) {
			groupMap.put(group.toLowerCase(Locale.getDefault()), "");
		}

		if (memberListData != null) {
			for (Map<String, String> map : memberListData) {
				memberMap.put(map.get("0").toLowerCase(Locale.getDefault()), "");
			}
		}

		for (DbUserInfo user : userListInfo.getUserList()) {
			if (!groupMap.containsKey(user.getName().toLowerCase())
					&& !memberMap.containsKey(user.getName().toLowerCase())
					&& !user.getName().equalsIgnoreCase(userName)) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("0", user.getName().toLowerCase());
				allUserListData.add(map);
			}
		}

		for (String userName : groupList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("0", userName.toLowerCase(Locale.getDefault()));
			groupListData.add(map);
		}
		allUserTableViewer.refresh();
		groupTableViewer.refresh();
		Iterator<String> authIter = classGrantMap.keySet().iterator();
		while (authIter.hasNext()) {
			String className = authIter.next();
			if (!partitionClassMap.containsKey(className)) {
				authListData.add(getItemAuthMap(classGrantMap.get(className)));
				authListDataOld.add(getItemAuthMap(classGrantMap.get(className)));
			}
		}
		if (!DB_DBA_USERNAME.equalsIgnoreCase(userName) && !isDBAGroup()) {
			authTableViewer.refresh();
			for (ClassInfo bean : allClassInfoList) {
				if (classGrantMap.containsKey(bean.getUniqueName())
						|| bean.isSystemClass()
						|| bean.getOwnerName().equalsIgnoreCase(DB_DEFAULT_USERNAME)) {
					continue;
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("0", bean.getUniqueName());
				map.put("1", Messages.msgUserSchema);
				map.put("2", bean.getOwnerName());
				map.put("3", bean.getClassType() == ClassType.VIEW ? Messages.msgVirtualClass : Messages.msgClass);

				classListData.add(map);
			}
			classTableViewer.refresh();
			packTable(classTable);
			packTable(authTable);
		}
		packTable(allUserTable);
		packTable(userGroupTable);
		packTable(memberTableViewer.getTable());
	}

	/**
	 * Pack the table
	 * 
	 * @param table Table
	 */
	private void packTable(Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
	}

	/**
	 * Get item map
	 * 
	 * @param auth the authorization object
	 * @return the map object
	 */
	private Map<String, Object> getItemAuthMap(ClassAuthorizations auth) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("0", auth.getClassName());
		map.put("1", auth.isSelectPriv());
		map.put("2", auth.isInsertPriv());
		map.put("3", auth.isUpdatePriv());
		map.put("4", auth.isDeletePriv());
		map.put("5", auth.isAlterPriv());
		map.put("6", auth.isIndexPriv());
		map.put("7", auth.isExecutePriv());
		map.put("8", auth.isGrantSelectPriv());
		map.put("9", auth.isGrantInsertPriv());
		map.put("10", auth.isGrantUpdatePriv());
		map.put("11", auth.isGrantDeletePriv());
		map.put("12", auth.isGrantAlterPriv());
		map.put("13", auth.isGrantIndexPriv());
		map.put("14", auth.isGrantExecutePriv());
		return map;
	}

	/**
	 * Set the button disable
	 */
	private void setBtnEnableDisable() {
		buttonAddGroup.setEnabled(false);
		removeGroupBtn.setEnabled(false);
		if (!database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
			return;
		}
		if (userNameText.getText().equalsIgnoreCase(DB_DEFAULT_USERNAME)) {
			return;
		}
		if (allUserTable.getSelectionCount() > 0
				&& allUserTable.isFocusControl()) {
			if (!userNameText.getText().equalsIgnoreCase(DB_DBA_USERNAME)) {
				buttonAddGroup.setEnabled(true);
			}
		} else if (userGroupTable.getSelectionCount() > 0
				&& userGroupTable.isFocusControl()) {
			int[] idx = userGroupTable.getSelectionIndices();
			for (int id : idx) {
				String name = userGroupTable.getItem(id).getText();
				if (name.equalsIgnoreCase(DB_DEFAULT_USERNAME)) {
					removeGroupBtn.setEnabled(false);
					return;
				}
			}

			removeGroupBtn.setEnabled(true);
		}
	}

	/**
	 * Set the auth button disable
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void setAuthBtnEnableDisable() {
		grantButton.setEnabled(false);
		revokeButton.setEnabled(false);
		/*Can't grant/revoke for  current login user*/
		if (StringUtil.isEqual(this.userName, currentUserInfo.getName())) {
			return;
		}
		DbUserInfo currentUserInfo = database.getDatabaseInfo().getAuthLoginedDbUserInfo();
		boolean isDbaAuthority = currentUserInfo.isDbaAuthority();

		if (classTable.getSelectionCount() > 0 && classTable.isFocusControl()) {
			boolean tag = true;
			if (!isDbaAuthority) {
				for (TableItem item : classTable.getSelection()) {
					ClassAuthorizations authorizations = currentUserAuthorizations.get(item.getText());
					boolean ownertag = false;
					for (ClassInfo bean : allClassInfoList) {
						if (item.getText().equals(bean.getUniqueName())) {
							if (currentUserInfo.getName().equalsIgnoreCase(bean.getOwnerName())) {
								ownertag = true;
							}
							break;
						}
					}
					tag = tag && (ownertag || authorizations == null || authorizations.isGrantPriv());
					if (!tag) {
						break;
					}
				}
			}
			grantButton.setEnabled(tag);
		} else if (authTable.getSelectionCount() > 0 && authTable.isFocusControl()) {
			boolean tag = true;
			if (!isDbaAuthority) {
				for (TableItem item : authTable.getSelection()) {
					ClassAuthorizations authorizations = currentUserAuthorizations.get(item.getText());
					boolean ownertag = false;
					for (ClassInfo bean : allClassInfoList) {
						if (item.getText().equals(bean.getUniqueName())) {
							if (currentUserInfo.getName().equalsIgnoreCase(bean.getOwnerName())) {
								ownertag = true;
							}
							break;
						}
					}
					tag = tag
							&& (ownertag || authorizations == null || (authorizations.isAllPriv() || authorizations
									.isRevokePriv((Map<String, Object>) item.getData())));
					if (!tag) {
						break;
					}
				}
			}
			revokeButton.setEnabled(tag);
		}
	}

	/**
	 * Check the data validation
	 * 
	 * @return <code>true</code> if valid;<code>false</code>otherwise
	 */
	public boolean valid() {
		String userName = userNameText.getText();
		if (userName == null) {
			userName = "";
		}

		if (newFlag) {
			String pwd = pwdText.getText();
			String pwdcfm = pwdCfmText.getText();
			if (pwd == null || pwd.equals("")) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInputPassword);
				return false;
			}
			if (!pwd.equals(pwdcfm)) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errPasswordDiff);
				return false;
			}

			if (userName == null || userName.equals("")) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInputName);
				return false;
			}
			if (!ValidateUtil.isValidDBName(userName)) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInputNameValidate);
				return false;
			}

			if (userName.length() > ValidateUtil.MAX_NAME_LENGTH) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputNameLength,
						ValidateUtil.MAX_NAME_LENGTH));
				return false;
			}
			for (DbUserInfo user : userListInfo.getUserList()) {
				if (user.getName().equalsIgnoreCase(userName)) {
					CommonUITool.openErrorBox(parentComp.getShell(),
							Messages.errInputNameExist);
					return false;
				}
			}

			if (!pwd.equals(pwdcfm)) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errPasswordDiff);
				return false;
			}
			if (pwd.length() > ValidateUtil.MAX_PASSWORD_LENGTH) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputPassLength,
						ValidateUtil.MAX_PASSWORD_LENGTH));
				return false;
			}
			if (pwd.length() < ValidateUtil.MIN_PASSWORD_LENGTH) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputPasswordMinLength,
						ValidateUtil.MIN_PASSWORD_LENGTH));
				return false;
			}
			if (pwd.indexOf(" ") >= 0) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInvalidPassword);
				return false;
			}
			if (("__NULL__").equals(pwd)) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputNameAccept, "__NULL__"));
				return false;
			}

		} else if (changePwdBtn.getSelection()) {
			String pwd = pwdText.getText();
			String pwdcfm = pwdCfmText.getText();
			String oldPwd = oldPwdText.getText();
			if (pwd == null || pwd.equals("")) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInputPassword);
				return false;
			}
			if (oldPwdText.getEnabled() && !oldLoginPassword.equals(oldPwd)) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errOldPassword);
				return false;
			}
			if (!pwd.equals(pwdcfm)) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errPasswordDiff);
				return false;
			}
			if (pwd.length() < ValidateUtil.MIN_PASSWORD_LENGTH) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputPasswordMinLength,
						ValidateUtil.MIN_PASSWORD_LENGTH));
				return false;
			}
			if (pwd.length() > ValidateUtil.MAX_PASSWORD_LENGTH) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputPassLength,
						ValidateUtil.MAX_PASSWORD_LENGTH));
				return false;
			}

			if (pwd.indexOf(" ") >= 0) {
				CommonUITool.openErrorBox(parentComp.getShell(),
						Messages.errInvalidPassword);
				return false;
			}

			if (("__NULL__").equals(pwd)) {
				CommonUITool.openErrorBox(parentComp.getShell(), Messages.bind(
						Messages.errInputNameAccept, "__NULL__"));
				return false;
			}
		}

		return true;
	}

	/**
	 * whether change the group
	 *
	 * @return
	 */
	public boolean groupChange() {
		boolean result = false;
		List<String> newGroupList = new ArrayList<String>();
		for (int i = 0; i < userGroupTable.getItemCount(); i++) {
			newGroupList.add(userGroupTable.getItem(i).getText(0));
		}

		for (DbUserInfo bean : userListInfo.getUserList()) {
			if (!(bean.getName().equalsIgnoreCase(userName))) {
				continue;
			}

			userInfo = bean;
			List<String> oldGoupsList = bean.getGroups().getGroup();
			// When only Change the user's password, oldGroup object is set to be null.
			if (oldGoupsList == null) {
				return true;
			}
			result = Arrays.equals(newGroupList.toArray(), oldGoupsList.toArray());
			break;
		}
		return result;
	}
	
	public boolean isDBAGroup () {
		for (int i = 0; i < userGroupTable.getItemCount(); i++) {
			if (userGroupTable.getItem(i).getText(0).equals(DB_DBA_USERNAME)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Get added CubridDatabase
	 * 
	 * @return the CubridDatabase object
	 */
	public CubridDatabase getDatabase() {
		return database;
	}

	/**
	 * Set edited CubridDatabase
	 * 
	 * @param database the CubridDatabase object
	 */
	public void setDatabase(CubridDatabase database) {
		this.database = database;
	}

	public boolean execTask(final int buttonId, final ITask[] tasks, Shell shell) {
		final Display display = Display.getCurrent();
		TaskExecutor taskExecutor = new TaskExecutor() {
			public boolean exec(IProgressMonitor monitor) {
				for (ITask task : tasks) {
					if (task instanceof GetAllClassListTask) {
						setAllClassInfoList(((GetAllClassListTask) task).getAllClassInfoList());
					} else {
						task.execute();
					}
					if (openErrorBox(null, task.getErrorMsg(), monitor)) {
						return false;
					}
					if (monitor.isCanceled()) {
						return false;
					}
				}
				if (!monitor.isCanceled()) {
					display.syncExec(new Runnable() {
						public void run() {
							if (buttonId > 0) {
								setReturnCode(buttonId);
								close();
							}
						}
					});
				}
				return true;
			}
		};
		for (ITask task : tasks) {
			taskExecutor.addTask(task);
		}
		new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
		return taskExecutor.isSuccess();
	}

	public DbUserInfoList getUserListInfo() {
		return userListInfo;
	}

	public void setUserListInfo(DbUserInfoList userListInfo) {
		this.userListInfo = userListInfo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isNewFlag() {
		return newFlag;
	}

	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}

	/**
	 * The provider is get table colume image
	 * 
	 * @author robin 2009-6-4
	 */
	static class AuthTableLabelProvider extends TableLabelProvider {
		@SuppressWarnings("unchecked")
		public Image getColumnImage(Object element, int columnIndex) {
			Map<String, Object> item = (Map<String, Object>) element;
			if (columnIndex > 0) {
				Boolean flag = (Boolean) item.get(columnIndex + "");
				return flag ? CommonUIPlugin.getImage("icons/checked.gif")
						: CommonUIPlugin.getImage("icons/unchecked.gif");

			}
			return null;
		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Map)) {
				return "";
			}
			if (columnIndex != 0) {
				return null;
			}
			Map<String, Object> map = (Map<String, Object>) element;
			return map.get("" + columnIndex).toString();
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}
	}

	/**
	 * Create common tableViewer
	 * 
	 * @param parent the parent composite
	 * @param columnNameArr the column name array
	 * @param gridData the GridData
	 * @return the tableviewer
	 */
	public TableViewer createCommonTableViewer(Composite parent, final String[] columnNameArr, GridData gridData) {
		final TableViewer tableViewer = new TableViewer(parent,
				SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new AuthTableLabelProvider());
		tableViewer.setSorter(new TableViewerSorter());

		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(gridData);

		for (int i = 0; i < columnNameArr.length; i++) {
			final TableColumn tblColumn = new TableColumn(
					tableViewer.getTable(), SWT.CHECK);
			if (i != 0) {
				tblColumn.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
			}
			tblColumn.setData(false);
			tblColumn.setText(columnNameArr[i]);
			final int num = i;
			tblColumn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					TableColumn column = (TableColumn) event.widget;
					if (num == 0) {
						int sortIndex = 0;
						for (int j = 0; j < columnNameArr.length; j++) {
							sortIndex = j;
							if (column.getText().equals(columnNameArr[j])) {
								break;
							}
						}

						TableViewerSorter sorter = ((TableViewerSorter) tableViewer.getSorter());
						if (sorter == null) {
							return;
						}
						sorter.doSort(sortIndex);
						tableViewer.getTable().setSortColumn(column);
						tableViewer.getTable().setSortDirection(sorter.isAsc() ? SWT.UP : SWT.DOWN);
						tableViewer.refresh();

						for (int k = 0; k < tableViewer.getTable().getColumnCount(); k++) {
							tableViewer.getTable().getColumn(k).pack();
						}

						return;
					}

					if ((Boolean) tblColumn.getData()) {
						column.setImage(CommonUIPlugin.getImage("icons/unchecked.gif"));
						column.setData(false);

						for (int v = 0; v < column.getParent().getItemCount(); v++) {
							Map<String, Object> map = authListData.get(v);
							if (isSystemClass((String) map.get("0"))) {
								map.put(num + "", false);
							}
						}
					} else {
						column.setImage(CommonUIPlugin.getImage("icons/checked.gif"));
						column.setData(true);

						for (int v = 0; v < column.getParent().getItemCount(); v++) {
							Map<String, Object> map = authListData.get(v);
							if (isSystemClass((String) map.get("0"))) {
								map.put(num + "", true);
							}
						}
					}
					tableViewer.refresh();
				}
			});

			tblColumn.pack();
		}

		return tableViewer;
	}

	/**
	 * Return whether it is system class
	 * 
	 * @param name the class name
	 * @return <code>true</code> if it is system class;<code>false</code>
	 *         otherwise
	 */
	public boolean isSystemClass(String name) {
		if (!database.getDatabaseInfo().getAuthLoginedDbUserInfo().isDbaAuthority()) {
			return false;
		}
		for (ClassInfo bean : allClassInfoList) {
			if (bean.getUniqueName().equals(name) && bean.isSystemClass()) {
				return false;
			}
		}
		return true;
	}

	public List<ClassInfo> getAllClassInfoList() {
		return allClassInfoList;
	}

	public void setAllClassInfoList(List<ClassInfo> allClassInfoList) {
		this.allClassInfoList = allClassInfoList;
	}

	public Map<String, String> getPartitionClassMap() {
		return partitionClassMap;
	}

	public void setPartitionClassMap(Map<String, String> partitionClassMap) {
		this.partitionClassMap = partitionClassMap;
	}

	public String getInputtedPassword() {
		return inputtedPassword;
	}
}
