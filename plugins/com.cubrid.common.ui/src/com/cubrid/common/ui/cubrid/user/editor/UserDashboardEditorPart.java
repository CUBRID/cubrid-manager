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
package com.cubrid.common.ui.cubrid.user.editor;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.cubrid.user.Messages;
import com.cubrid.common.ui.cubrid.user.action.AddUserAction;
import com.cubrid.common.ui.cubrid.user.action.DeleteUserAction;
import com.cubrid.common.ui.cubrid.user.action.EditUserAction;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.progress.CommonTaskJobExec;
import com.cubrid.common.ui.spi.progress.ITaskExecutorInterceptor;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.cubrid.user.model.AuthType;
import com.cubrid.cubridmanager.core.cubrid.user.model.DBAuth;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfoList;
import com.cubrid.cubridmanager.core.cubrid.user.model.UserDetailInfo;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetAllUserAuthorizationsTask;
import com.cubrid.cubridmanager.core.cubrid.user.task.GetUserListTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.GridData;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * User dashboard Editor Part
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-7-5 created by Kevin.Wang
 */
public class UserDashboardEditorPart extends CubridEditorPart {
    public static final String ID = UserDashboardEditorPart.class.getName();
    public static final String DB_DBA_USERNAME = "dba";
    private boolean userChangeFlag;
    private CubridDatabase database;
    private TableViewer userTableViewer;
    private TableViewer authTableViewer;

    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(parent, SWT.LEFT_TO_RIGHT | SWT.FLAT);
        toolBar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
        refreshItem.setText(Messages.btnRefresh);
        refreshItem.setToolTipText(Messages.btnRefresh);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        refresh();
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);
        ToolItem addUserItem = new ToolItem(toolBar, SWT.NONE);
        addUserItem.setText(Messages.btnAddUser);
        addUserItem.setImage(CommonUIPlugin.getImage("icons/action/user_add.png"));
        addUserItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        addUser();
                    }
                });

        ToolItem editUserItem = new ToolItem(toolBar, SWT.NONE);
        editUserItem.setText(Messages.btnEditUser);
        editUserItem.setImage(CommonUIPlugin.getImage("icons/action/user_edit.png"));
        editUserItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        editUser();
                    }
                });

        ToolItem dropUserItem = new ToolItem(toolBar, SWT.NONE);
        dropUserItem.setText(Messages.btnDropUser);
        dropUserItem.setImage(CommonUIPlugin.getImage("icons/action/user_delete.png"));
        dropUserItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        dropUser();
                    }
                });

        SashForm form = new SashForm(parent, SWT.HORIZONTAL);
        form.setLayout(new FillLayout());
        form.setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        createUsersTable(form);
        createAuthTable(form);

        form.setWeights(new int[] {150, 850});
    }

    /**
     * create user table
     *
     * @param parent
     */
    public void createUsersTable(Composite parent) {
        userTableViewer =
                new TableViewer(
                        parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        userTableViewer.getTable().setHeaderVisible(false);
        userTableViewer.getTable().setLinesVisible(false);
        userTableViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));

        final TableViewerColumn nameColumn = new TableViewerColumn(userTableViewer, SWT.LEFT);
        nameColumn.getColumn().setWidth(200);

        userTableViewer.setContentProvider(new UserContentProvider());
        userTableViewer.setLabelProvider(new UserLabelProvider());

        registerContextMenu();

        userTableViewer
                .getTable()
                .addSelectionListener(
                        new SelectionListener() {
                            public void widgetSelected(SelectionEvent e) {
                                widgetDefaultSelected(e);
                            }

                            public void widgetDefaultSelected(SelectionEvent e) {
                                TableItem[] items = userTableViewer.getTable().getSelection();
                                if (items.length == 1) {
                                    UserDetailInfo userInfo = (UserDetailInfo) items[0].getData();
                                    setAuthDetailInfoTableViewer(userInfo);
                                }
                            }
                        });
    }

    private void createAuthTable(Composite parent) {
        authTableViewer =
                new TableViewer(
                        parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        authTableViewer.getTable().setHeaderVisible(true);
        authTableViewer.getTable().setLinesVisible(true);

        final TableViewerColumn tableColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        tableColumn.getColumn().setWidth(120);
        tableColumn.getColumn().setText(Messages.tblColAuthTable);

        final TableViewerColumn ownerColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        ownerColumn.getColumn().setWidth(70);
        ownerColumn.getColumn().setText(Messages.lblOWner);

        final TableViewerColumn authSelectColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authSelectColumn.getColumn().setWidth(70);
        authSelectColumn.getColumn().setText(Messages.tblColAuthSelect);

        final TableViewerColumn authInsertColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authInsertColumn.getColumn().setWidth(70);
        authInsertColumn.getColumn().setText(Messages.tblColAuthInsert);

        final TableViewerColumn authUpdateColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authUpdateColumn.getColumn().setWidth(70);
        authUpdateColumn.getColumn().setText(Messages.tblColAuthUpdate);

        final TableViewerColumn authDeleteColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authDeleteColumn.getColumn().setWidth(70);
        authDeleteColumn.getColumn().setText(Messages.tblColAuthDelete);

        final TableViewerColumn authAlterColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authAlterColumn.getColumn().setWidth(70);
        authAlterColumn.getColumn().setText(Messages.tblColAuthAlter);

        final TableViewerColumn authIndexColumn = new TableViewerColumn(authTableViewer, SWT.LEFT);
        authIndexColumn.getColumn().setWidth(70);
        authIndexColumn.getColumn().setText(Messages.tblColAuthIndex);

        final TableViewerColumn authExecuteColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authExecuteColumn.getColumn().setWidth(70);
        authExecuteColumn.getColumn().setText(Messages.tblColAuthExecute);

        final TableViewerColumn authGrantSelectColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantSelectColumn.getColumn().setWidth(90);
        authGrantSelectColumn.getColumn().setText(Messages.tblColAuthGrantselect);

        final TableViewerColumn authGrantInsertColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantInsertColumn.getColumn().setWidth(90);
        authGrantInsertColumn.getColumn().setText(Messages.tblColAuthGrantinsert);

        final TableViewerColumn authGrantUpdateColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantUpdateColumn.getColumn().setWidth(90);
        authGrantUpdateColumn.getColumn().setText(Messages.tblColAuthGrantupdate);

        final TableViewerColumn authGrantDeleteColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantDeleteColumn.getColumn().setWidth(90);
        authGrantDeleteColumn.getColumn().setText(Messages.tblColAuthGrantdelete);

        final TableViewerColumn authGrantAlterColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantAlterColumn.getColumn().setWidth(90);
        authGrantAlterColumn.getColumn().setText(Messages.tblColAuthGrantalter);

        final TableViewerColumn authGrantIndexColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantIndexColumn.getColumn().setWidth(90);
        authGrantIndexColumn.getColumn().setText(Messages.tblColAuthGrantindex);

        final TableViewerColumn authGrantExecuteColumn =
                new TableViewerColumn(authTableViewer, SWT.LEFT);
        authGrantExecuteColumn.getColumn().setWidth(90);
        authGrantExecuteColumn.getColumn().setText(Messages.tblColAuthGrantexecute);

        authTableViewer.setLabelProvider(new AuthLabelProvider());
        authTableViewer.setContentProvider(new AuthContentProvider());
    }

    /** register context menu */
    private void registerContextMenu() {
        userTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(userTableViewer.getTable());
                            }
                        });

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(userTableViewer.getTable());
        userTableViewer.getTable().setMenu(contextMenu);

        Menu menu =
                new Menu(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        SWT.POP_UP);

        final MenuItem editSerialItem = new MenuItem(menu, SWT.PUSH);
        editSerialItem.setText(Messages.itemEditUser);
        editSerialItem.setImage(CommonUIPlugin.getImage("icons/action/user_edit.png"));
        editSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        editUser();
                    }
                });

        final MenuItem dropSerialItem = new MenuItem(menu, SWT.PUSH);
        dropSerialItem.setText(Messages.itemDropUser);
        dropSerialItem.setImage(CommonUIPlugin.getImage("icons/action/user_delete.png"));
        dropSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        dropUser();
                    }
                });

        new MenuItem(menu, SWT.SEPARATOR);

        final MenuItem addSerialItem = new MenuItem(menu, SWT.PUSH);
        addSerialItem.setText(Messages.itemAddUser);
        addSerialItem.setImage(CommonUIPlugin.getImage("icons/action/user_add.png"));
        addSerialItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        addUser();
                    }
                });

        userTableViewer.getTable().setMenu(menu);
    }

    /** add User */
    public void addUser() {
        AddUserAction action =
                (AddUserAction) ActionManager.getInstance().getAction(AddUserAction.ID);
        action.doRun(database);
        refresh();
    }

    /** edit User */
    public void editUser() {
        TableItem[] items = userTableViewer.getTable().getSelection();
        if (items.length == 1) {
            TableItem item = items[0];
            UserDetailInfo userInfo = (UserDetailInfo) item.getData();
            Set<String> typeSet = new HashSet<String>();
            typeSet.add(NodeType.USER);

            ICubridNode userNode = CommonUITool.findNode(database, typeSet, userInfo.getUserName());
            if (userNode != null) {
                EditUserAction action =
                        (EditUserAction) ActionManager.getInstance().getAction(EditUserAction.ID);
                action.doRun((ISchemaNode) userNode);
                refresh();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errNoUserSelected);
        }
    }

    /** drop User */
    public void dropUser() {
        TableItem[] items = userTableViewer.getTable().getSelection();
        if (items.length == 1) {
            TableItem item = items[0];
            UserDetailInfo userInfo = (UserDetailInfo) item.getData();
            String msg = Messages.bind(Messages.msgDoYouWantToDeleteUser, userInfo.getUserName());
            if (!CommonUITool.openConfirmBox(msg)) {
                return;
            }

            Set<String> typeSet = new HashSet<String>();
            typeSet.add(NodeType.USER);

            ICubridNode userNode = CommonUITool.findNode(database, typeSet, userInfo.getUserName());
            if (userNode != null) {
                DeleteUserAction action =
                        (DeleteUserAction)
                                ActionManager.getInstance().getAction(DeleteUserAction.ID);
                action.doRun((ISchemaNode) userNode);
                refresh();
            }
        } else {
            CommonUITool.openWarningBox(Messages.errNoUserSelected);
        }
    }

    /** refresh data */
    public void refresh() {
        loadData();
        userChangeFlag = false;
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        this.database = (CubridDatabase) input.getAdapter(CubridDatabase.class);

        StringBuilder partName = new StringBuilder(Messages.tabUserList);
        partName.append(" [")
                .append(database.getUserName())
                .append("@")
                .append(database.getName())
                .append(":")
                .append(database.getDatabaseInfo().getBrokerIP())
                .append("]");
        setPartName(partName.toString());

        loadData();
    }

    /** Load the data */
    private void loadData() {
        final GetUserListTask getUserTask = new GetUserListTask(database.getDatabaseInfo());
        final GetAllUserAuthorizationsTask getUserAuthTask =
                new GetAllUserAuthorizationsTask(database.getDatabaseInfo());

        TaskJobExecutor taskExec =
                new CommonTaskJobExec(
                        new ITaskExecutorInterceptor() {
                            public void completeAll() {
                                final List<UserDetailInfo> userDetailList =
                                        new ArrayList<UserDetailInfo>();
                                DbUserInfoList userListInfo = getUserTask.getResultModel();
                                Map<String, UserDetailInfo> allUserAuthMap =
                                        getUserAuthTask.getAllAuthMap();
                                if (userListInfo != null) {
                                    for (DbUserInfo userInfo : userListInfo.getUserList()) {
                                        UserDetailInfo userDetailInfo =
                                                allUserAuthMap.get(userInfo.getName());
                                        if (userDetailInfo == null) {
                                            userDetailInfo = new UserDetailInfo();
                                            userDetailInfo.setUserName(userInfo.getName());
                                        }
                                        userDetailList.add(userDetailInfo);
                                    }
                                }

                                setTableData(userDetailList);
                            }

                            public IStatus postTaskFinished(ITask task) {
                                return Status.OK_STATUS;
                            }
                        });
        taskExec.addTask(getUserTask);
        taskExec.addTask(getUserAuthTask);

        JobFamily jobFamily = new JobFamily();

        String serverName = database.getServer().getName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        String jobName = Messages.jobLoadUserData + " - " + "@" + dbName + "@" + serverName;
        taskExec.schedule(jobName, jobFamily, false, Job.SHORT);
    }

    /**
     * Set table in UI thread
     *
     * @param userDetailList
     */
    private void setTableData(final List<UserDetailInfo> userDetailList) {
        Display.getCurrent()
                .asyncExec(
                        new Runnable() {
                            public void run() {
                                setUserDetailInfoTableData(userDetailList);
                                setAuthDetailInfoTableViewer(null);
                            }
                        });
    }

    private void setUserDetailInfoTableData(final List<UserDetailInfo> userDetailList) {
        Collections.sort(userDetailList, new UserInfoComparator());
        userTableViewer.setInput(userDetailList);
    }

    private void setAuthDetailInfoTableViewer(UserDetailInfo userDetailInfo) {
        if (userDetailInfo == null || userDetailInfo.getAllDBAuth() == null) {
            authTableViewer.setInput(null);
        } else {
            List<DBAuth> list = userDetailInfo.getAllDBAuth();
            Collections.sort(list, new DBAuthComparator());
            authTableViewer.setInput(list);
        }
    }

    public void nodeChanged(CubridNodeChangedEvent event) {
        if (event.getSource() instanceof DefaultSchemaNode) {
            DefaultSchemaNode node = (DefaultSchemaNode) event.getSource();
            if ((node.getType().equals(NodeType.USER)
                            || node.getType().equals(NodeType.USER_FOLDER))
                    && node.getDatabase().equals(database)) {
                if (CubridNodeChangedEventType.NODE_ADD.equals(event.getType())
                        || CubridNodeChangedEventType.NODE_REFRESH.equals(event.getType())
                        || CubridNodeChangedEventType.NODE_REMOVE.equals(event.getType())) {
                    userChangeFlag = true;
                }
            }
        }
        if (CubridNodeChangedEventType.SERVER_DISCONNECTED.equals(event.getType())) {
            close(event, database.getServer());
        }

        if (CubridNodeChangedEventType.DATABASE_LOGOUT.equals(event.getType())
                || CubridNodeChangedEventType.DATABASE_STOP.equals(event.getType())) {
            close(event, database);
        }
    }

    public void setFocus() {
        // if view info chaned, ask whether refresh
        if (userChangeFlag) {
            if (CommonUITool.openConfirmBox(Messages.msgConfirmRefresh)) {
                refresh();
            }
            userChangeFlag = false;
        } else {
            refresh();
        }
    }

    public void doSave(IProgressMonitor monitor) {}

    public void doSaveAs() {}

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public CubridDatabase getDatabase() {
        return database;
    }
}

class UserLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof UserDetailInfo) {
            UserDetailInfo userDetail = (UserDetailInfo) element;
            if (userDetail != null && columnIndex == 0) {
                return userDetail.getUserName();
            }
        }
        return null;
    }
}

class UserContentProvider implements IStructuredContentProvider {
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Collection) {
            Collection<UserDetailInfo> list = (Collection<UserDetailInfo>) inputElement;
            UserDetailInfo[] nodeArr = new UserDetailInfo[list.size()];
            return list.toArray(nodeArr);
        }
        return new Object[] {};
    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}

class AuthLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage(Object element, int columnIndex) {
        if (element != null && element instanceof DBAuth) {
            DBAuth dbAuth = (DBAuth) element;
            Boolean flag = false;
            switch (columnIndex) {
                case 2:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.SELECT);
                    return getCheckImage(flag);
                case 3:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.INSERT);
                    return getCheckImage(flag);
                case 4:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.UPDATE);
                    return getCheckImage(flag);
                case 5:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.DELETE);
                    return getCheckImage(flag);
                case 6:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.ALTER);
                    return getCheckImage(flag);
                case 7:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.INDEX);
                    return getCheckImage(flag);
                case 8:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.EXECUTE);
                    return getCheckImage(flag);
                case 9:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.SELECTGRANT);
                    return getCheckImage(flag);
                case 10:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.INDEXGRANT);
                    return getCheckImage(flag);
                case 11:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.UPDATEGRANT);
                    return getCheckImage(flag);
                case 12:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.DELETEGRANT);
                    return getCheckImage(flag);
                case 13:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.ALTERGTANT);
                    return getCheckImage(flag);
                case 14:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.INDEXGRANT);
                    return getCheckImage(flag);
                case 15:
                    flag = AuthType.isHasAuth(dbAuth.getAuthType(), AuthType.EXECUTEGRANT);
                    return getCheckImage(flag);
            }
        }
        return null;
    }

    private Image getCheckImage(Boolean flag) {
        return flag
                ? CommonUIPlugin.getImage("icons/disabled_checked.gif")
                : CommonUIPlugin.getImage("icons/disabled_unchecked.gif");
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element != null && element instanceof DBAuth) {
            DBAuth dbAuth = (DBAuth) element;
            switch (columnIndex) {
                case 0:
                    return dbAuth.getClassName();
                case 1:
                    return dbAuth.getOwner();
            }
        }
        return "";
    }
}

class AuthContentProvider implements IStructuredContentProvider {
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Collection) {
            List<DBAuth> list = (List<DBAuth>) inputElement;
            DBAuth[] nodeArr = new DBAuth[list.size()];
            return list.toArray(nodeArr);
        }
        return new Object[] {};
    }

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}

class UserInfoComparator implements Comparator<UserDetailInfo> {
    public int compare(UserDetailInfo o1, UserDetailInfo o2) {
        if (o1 == null || o2 == null) {
            return 0;
        }
        if (StringUtil.isEqualIgnoreCase(UserDashboardEditorPart.DB_DBA_USERNAME, o1.getUserName())
                && !StringUtil.isEqualIgnoreCase(
                        UserDashboardEditorPart.DB_DBA_USERNAME, o2.getUserName())) {
            return -1;
        } else if (!StringUtil.isEqualIgnoreCase(
                        UserDashboardEditorPart.DB_DBA_USERNAME, o1.getUserName())
                && StringUtil.isEqualIgnoreCase(
                        UserDashboardEditorPart.DB_DBA_USERNAME, o2.getUserName())) {
            return 1;
        }
        return o1.getUserName().compareTo(o2.getUserName());
    }
}

class DBAuthComparator implements Comparator<DBAuth> {
    public int compare(DBAuth o1, DBAuth o2) {
        if (o1.isTable() && o2.isTable()) {
            if (StringUtil.isEqualIgnoreCase(UserDashboardEditorPart.DB_DBA_USERNAME, o1.getOwner())
                    && !StringUtil.isEqualIgnoreCase(
                            UserDashboardEditorPart.DB_DBA_USERNAME, o2.getOwner())) {
                return -1;
            }

        } else if (!o1.isTable() && !o2.isTable()) {
            if (StringUtil.isEqualIgnoreCase(UserDashboardEditorPart.DB_DBA_USERNAME, o1.getOwner())
                    && !StringUtil.isEqualIgnoreCase(
                            UserDashboardEditorPart.DB_DBA_USERNAME, o2.getOwner())) {
                return -1;
            }
        } else if (o1.isTable() && !o2.isTable()) {
            return -1;
        } else if (!o1.isTable() && o2.isTable()) {
            return 1;
        }
        if (o1.getClassName() != null && o2.getClassName() != null) {
            return o1.getClassName().compareTo(o2.getClassName());
        }
        return 0;
    }
}
