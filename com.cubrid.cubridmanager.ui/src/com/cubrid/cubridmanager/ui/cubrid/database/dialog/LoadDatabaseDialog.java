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

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.JobFamily;
import com.cubrid.common.ui.spi.progress.TaskJobExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbUnloadInfo;
import com.cubrid.cubridmanager.core.cubrid.database.task.LoadDbTask;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * Load database will use this dialog to fill in the information
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class LoadDatabaseDialog extends CMTitleAreaDialog implements ModifyListener {
    private Text databaseNameText = null;
    private CubridDatabase database = null;
    private Table unloadInfoTable;
    private Button selectLoadFileFromListButton;
    private Combo databaseCombo;
    private Text userNameText;
    private Text loadSchemaText;
    private Button loadSchemaFileSearchButton;
    private Button loadSchemaButton;
    private Button loadObjButton;
    private Text loadObjText;
    private Button loadObjFileSearchButton;
    private Button loadIndexButton;
    private Text loadIndexText;
    private Button loadIndexFileSearchButton;
    private Button loadTriggerButton;
    private Text loadTriggerText;
    private Button loadTriggerFileSearchButton;
    private Button checkSyntaxButton;
    private Button commitPeriodButton;
    private Text commitPeriodText;
    private Button estimatedSizeButton;
    private Text estimatedSizeText;
    private Button oidButton;
    private Button selectUnloadFileFromSysButton;
    private List<DbUnloadInfo> dbUnloadInfoList;
    private CheckboxTableViewer tableViewer;
    private String loadDbRusultStr = "";
    private Button useErrorControlFileButton;
    private Text errorControlFileText;
    private Button selectErrorControlFileButton;
    private String dbDir;
    private Button ignoreClassFileButton;
    private Text ignoredClassFileText;
    private Button selectIgnoreClassFileButton;
    private Button noStatisButton;

    private boolean isLocalServer = false;

    private String filteredFilePath;
    private static final String KEY_ERROR_CONTROL_FILE = "LoadDatabaseDialog.ERROR_CONTROL_FILE_";
    private static final String KEY_IGNORE_CLASS_FILE = "LoadDatabaseDialog.IGNORE_CLASS_FILE_";
    private static final String KEY_LOAD_SCHEMA_FILE = "LoadDatabaseDialog.LOAD_SCHEMA_FILE_";

    /**
     * The constructor
     *
     * @param parentShell
     */
    public LoadDatabaseDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        // the content may be too high so use ScrolledComposite
        ScrolledComposite scrolledComposite =
                new ScrolledComposite(parentComp, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = new Composite(scrolledComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);
        scrolledComposite.setContent(composite);
        isLocalServer = database.getServer().getServerInfo().isLocalServer();

        createDatabaseInfoGruop(composite);
        createLoadTargetInfoGroup(composite);
        createLoadOptionGruop(composite);
        // set ScrolledComposite scroll options
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        setTitle(Messages.titleLoadDbDialog);
        setMessage(Messages.msgLoadDbDialog);
        initial();

        return parentComp;
    }

    /**
     * Create target database information group
     *
     * @param parent the parent composite
     */
    private void createDatabaseInfoGruop(Composite parent) {
        Group databaseInfoGroup = new Group(parent, SWT.NONE);
        databaseInfoGroup.setText(Messages.grpDbInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        databaseInfoGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        databaseInfoGroup.setLayout(layout);

        Label databaseNameLabel = new Label(databaseInfoGroup, SWT.LEFT | SWT.WRAP);
        databaseNameLabel.setText(Messages.lblTargetDbName);
        databaseNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        databaseNameText = new Text(databaseInfoGroup, SWT.BORDER);
        databaseNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        databaseNameText.setEditable(false);

        Label userNameLabel = new Label(databaseInfoGroup, SWT.LEFT | SWT.CHECK);
        userNameLabel.setText(Messages.lblUserName);
        userNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        userNameText = new Text(databaseInfoGroup, SWT.BORDER);
        userNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
    }

    /**
     * Create unload file information group
     *
     * @param parent the parent composite
     */
    private void createLoadTargetInfoGroup(Composite parent) {
        Group unloadFileGroup = new Group(parent, SWT.NONE);
        unloadFileGroup.setText(Messages.grpLoadFile);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        unloadFileGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        unloadFileGroup.setLayout(layout);

        selectLoadFileFromListButton = new Button(unloadFileGroup, SWT.RADIO | SWT.LEFT);
        selectLoadFileFromListButton.setText(Messages.btnSelectFileFromList);
        selectLoadFileFromListButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        selectLoadFileFromListButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectLoadFileFromListButton.getSelection()) {
                            selectUnloadFileFromSysButton.setSelection(false);
                            setBtnStatusInList(true);
                            setBtnStatusInSys(false);
                        } else {
                            selectUnloadFileFromSysButton.setSelection(true);
                            databaseCombo.setEnabled(false);
                            unloadInfoTable.setEnabled(false);
                            for (int i = 0, n = unloadInfoTable.getItemCount(); i < n; i++) {
                                unloadInfoTable.getItem(i).setChecked(false);
                            }

                            loadSchemaButton.setEnabled(true);
                            loadObjButton.setEnabled(true);
                            loadIndexButton.setEnabled(true);
                            loadTriggerButton.setEnabled(true);
                        }
                        valid();
                    }
                });

        databaseCombo = new Combo(unloadFileGroup, SWT.NONE | SWT.READ_ONLY);
        databaseCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        databaseCombo.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        setTableModel(databaseCombo.getText());
                        valid();
                    }
                });
        createDbUnloadInfoTable(unloadFileGroup);
        createUnLoadInfoComp(unloadFileGroup);
    }

    /**
     * Set all buttons status in list
     *
     * @param isEnabled boolean
     */
    public void setBtnStatusInList(boolean isEnabled) {
        databaseCombo.setEnabled(isEnabled);
        unloadInfoTable.setEnabled(isEnabled);
    }

    /**
     * Set button status in system
     *
     * @param isEnabled boolean
     */
    public void setBtnStatusInSys(boolean isEnabled) {
        loadSchemaButton.setSelection(isEnabled);
        loadSchemaButton.setEnabled(isEnabled);
        loadSchemaText.setEnabled(isEnabled);
        loadSchemaText.setText("");
        if (loadSchemaFileSearchButton != null) {
            loadSchemaFileSearchButton.setEnabled(isEnabled);
        }

        loadObjButton.setSelection(isEnabled);
        loadObjButton.setEnabled(isEnabled);
        loadObjText.setEnabled(isEnabled);
        loadObjText.setText("");
        if (loadObjFileSearchButton != null) {
            loadObjFileSearchButton.setEnabled(isEnabled);
        }

        loadIndexButton.setSelection(isEnabled);
        loadIndexButton.setEnabled(isEnabled);
        loadIndexText.setEnabled(isEnabled);
        loadIndexText.setText("");
        if (loadIndexFileSearchButton != null) {
            loadIndexFileSearchButton.setEnabled(isEnabled);
        }

        loadTriggerButton.setSelection(isEnabled);
        loadTriggerButton.setEnabled(isEnabled);
        loadTriggerText.setEnabled(isEnabled);
        loadTriggerText.setText("");
        if (loadTriggerFileSearchButton != null) {
            loadTriggerFileSearchButton.setEnabled(isEnabled);
        }
    }

    /**
     * Create database unload information table
     *
     * @param parent the parent composite
     */
    private void createDbUnloadInfoTable(Composite parent) {
        final String[] columnNameArr =
                new String[] {
                    Messages.tblColumnLoadType, Messages.tblColumnPath, Messages.tblColumnDate
                };
        tableViewer =
                (CheckboxTableViewer)
                        CommonUITool.createCheckBoxTableViewer(
                                parent,
                                new TableViewerSorter(),
                                columnNameArr,
                                CommonUITool.createGridData(
                                        GridData.FILL_HORIZONTAL, 3, 1, -1, 100));
        tableViewer.addCheckStateListener(
                new ICheckStateListener() {
                    @SuppressWarnings("unchecked")
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        Map<String, String> map = (Map<String, String>) event.getElement();
                        String checkedType = map.get("0");
                        String checkedPath = map.get("1");
                        String checkedDate = map.get("2");
                        if (event.getChecked()) {
                            for (int i = 0, n = unloadInfoTable.getItemCount(); i < n; i++) {
                                if (unloadInfoTable.getItem(i).getChecked()) {
                                    String type = unloadInfoTable.getItem(i).getText(0);
                                    String path = unloadInfoTable.getItem(i).getText(1);
                                    String date = unloadInfoTable.getItem(i).getText(2);
                                    if (checkedType.equals(type)
                                            && checkedPath.equals(path)
                                            && checkedDate.equals(date)) {
                                        continue;
                                    }
                                    if (checkedType.equals(type)) {
                                        unloadInfoTable.getItem(i).setChecked(false);
                                    }
                                }
                            }
                        }
                        valid();
                    }
                });
        unloadInfoTable = tableViewer.getTable();
    }

    /**
     * Create unload information composite
     *
     * @param parent the parent composite
     */
    private void createUnLoadInfoComp(Composite parent) {

        Composite selectLoadFileFromSysComposite = new Composite(parent, SWT.NONE);
        selectLoadFileFromSysComposite.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        GridLayout layout = new GridLayout();
        int columNum = 2;
        if (isLocalServer) {
            columNum = 3;
        }
        layout.numColumns = columNum;
        selectLoadFileFromSysComposite.setLayout(layout);

        selectUnloadFileFromSysButton =
                new Button(selectLoadFileFromSysComposite, SWT.RADIO | SWT.LEFT);
        if (isLocalServer) {
            selectUnloadFileFromSysButton.setText(Messages.btnSelectFileFromLocalSys);
        } else {
            selectUnloadFileFromSysButton.setText(Messages.btnSelectFileFromRemoteSys);
        }
        selectUnloadFileFromSysButton.setLayoutData(
                CommonUITool.createGridData(columNum, 1, -1, -1));
        selectUnloadFileFromSysButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (selectUnloadFileFromSysButton.getSelection()) {
                            selectLoadFileFromListButton.setSelection(false);
                            databaseCombo.setEnabled(false);
                            unloadInfoTable.setEnabled(false);

                            loadSchemaButton.setEnabled(true);
                            loadObjButton.setEnabled(true);
                            loadIndexButton.setEnabled(true);
                            loadTriggerButton.setEnabled(true);
                            for (int i = 0, n = unloadInfoTable.getItemCount(); i < n; i++) {
                                unloadInfoTable.getItem(i).setChecked(false);
                            }
                        } else {
                            selectLoadFileFromListButton.setSelection(true);
                            databaseCombo.setEnabled(true);
                            unloadInfoTable.setEnabled(true);
                            loadSchemaButton.setEnabled(false);
                            loadObjButton.setEnabled(false);
                            loadIndexButton.setEnabled(false);
                            loadTriggerButton.setEnabled(false);
                        }
                        valid();
                    }
                });

        // add load schema comp
        loadSchemaButton = new Button(selectLoadFileFromSysComposite, SWT.CHECK | SWT.LEFT);
        loadSchemaButton.setText(Messages.btnLoadSchema);
        loadSchemaButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        loadSchemaButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (loadSchemaButton.getSelection()) {
                            loadSchemaText.setEnabled(true);
                            if (database.getServer().getServerInfo().isLocalServer()) {
                                loadSchemaFileSearchButton.setEnabled(true);
                            }
                        } else {
                            loadSchemaText.setText("");
                            loadSchemaText.setEnabled(false);
                            if (loadSchemaFileSearchButton != null) {
                                loadSchemaFileSearchButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });

        loadSchemaText = new Text(selectLoadFileFromSysComposite, SWT.BORDER);
        loadSchemaText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        loadSchemaText.setEnabled(false);
        loadSchemaText.addModifyListener(this);

        if (isLocalServer) {
            loadSchemaFileSearchButton = new Button(selectLoadFileFromSysComposite, SWT.NONE);
            loadSchemaFileSearchButton.setText(Messages.btnBrowse);
            loadSchemaFileSearchButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            loadSchemaFileSearchButton.setEnabled(false);
            loadSchemaFileSearchButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = loadSchemaText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text = filteredFilePath;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String file = dlg.open();
                            if (file != null) {
                                loadSchemaText.setText(file);
                                filteredFilePath = new File(file).getParent();
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(
                                                KEY_LOAD_SCHEMA_FILE + database.getId(),
                                                filteredFilePath);
                            }
                        }
                    });
        }
        // add load object comp
        loadObjButton = new Button(selectLoadFileFromSysComposite, SWT.CHECK | SWT.LEFT);
        loadObjButton.setText(Messages.btnLoadObj);
        loadObjButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        loadObjButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (loadObjButton.getSelection()) {
                            loadObjText.setEnabled(true);
                            if (database.getServer().getServerInfo().isLocalServer()) {
                                loadObjFileSearchButton.setEnabled(true);
                            }
                        } else {
                            loadObjText.setText("");
                            loadObjText.setEnabled(false);
                            if (loadObjFileSearchButton != null) {
                                loadObjFileSearchButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });

        loadObjText = new Text(selectLoadFileFromSysComposite, SWT.BORDER);
        loadObjText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        loadObjText.setEnabled(false);
        loadObjText.addModifyListener(this);

        if (isLocalServer) {
            loadObjFileSearchButton = new Button(selectLoadFileFromSysComposite, SWT.NONE);
            loadObjFileSearchButton.setText(Messages.btnBrowse);
            loadObjFileSearchButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            loadObjFileSearchButton.setEnabled(false);
            loadObjFileSearchButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = loadObjText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text = filteredFilePath;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String file = dlg.open();
                            if (file != null) {
                                loadObjText.setText(file);
                                filteredFilePath = new File(file).getParent();
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(
                                                KEY_LOAD_SCHEMA_FILE + database.getId(),
                                                filteredFilePath);
                            }
                        }
                    });
        }

        // add load index comp
        loadIndexButton = new Button(selectLoadFileFromSysComposite, SWT.CHECK | SWT.LEFT);
        loadIndexButton.setText(Messages.btnLoadIndex);
        loadIndexButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        loadIndexButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (loadIndexButton.getSelection()) {
                            loadIndexText.setEnabled(true);
                            if (database.getServer().getServerInfo().isLocalServer()) {
                                loadIndexFileSearchButton.setEnabled(true);
                            }
                        } else {
                            loadIndexText.setText("");
                            loadIndexText.setEnabled(false);
                            if (loadIndexFileSearchButton != null) {
                                loadIndexFileSearchButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });

        loadIndexText = new Text(selectLoadFileFromSysComposite, SWT.BORDER);
        loadIndexText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        loadIndexText.setEnabled(false);
        loadIndexText.addModifyListener(this);

        if (isLocalServer) {
            loadIndexFileSearchButton = new Button(selectLoadFileFromSysComposite, SWT.NONE);
            loadIndexFileSearchButton.setText(Messages.btnBrowse);
            loadIndexFileSearchButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            loadIndexFileSearchButton.setEnabled(false);
            loadIndexFileSearchButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = loadIndexText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text = filteredFilePath;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String file = dlg.open();
                            if (file != null) {
                                loadIndexText.setText(file);
                                filteredFilePath = new File(file).getParent();
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(
                                                KEY_LOAD_SCHEMA_FILE + database.getId(),
                                                filteredFilePath);
                            }
                        }
                    });
        }

        // add load trigger comp
        loadTriggerButton = new Button(selectLoadFileFromSysComposite, SWT.CHECK | SWT.LEFT);
        loadTriggerButton.setText(Messages.btnLoadTrigger);
        loadTriggerButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        loadTriggerButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (loadTriggerButton.getSelection()) {
                            loadTriggerText.setEnabled(true);
                            if (database.getServer().getServerInfo().isLocalServer()) {
                                loadTriggerFileSearchButton.setEnabled(true);
                            }
                        } else {
                            loadTriggerText.setText("");
                            loadTriggerText.setEnabled(false);
                            if (loadTriggerFileSearchButton != null) {
                                loadTriggerFileSearchButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });

        loadTriggerText = new Text(selectLoadFileFromSysComposite, SWT.BORDER);
        loadTriggerText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, -1, -1));
        loadTriggerText.setEnabled(false);
        loadTriggerText.addModifyListener(this);

        if (isLocalServer) {
            loadTriggerFileSearchButton = new Button(selectLoadFileFromSysComposite, SWT.NONE);
            loadTriggerFileSearchButton.setText(Messages.btnBrowse);
            loadTriggerFileSearchButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            loadTriggerFileSearchButton.setEnabled(false);
            loadTriggerFileSearchButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = loadTriggerText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text = filteredFilePath;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String file = dlg.open();
                            if (file != null) {
                                loadTriggerText.setText(file);
                                filteredFilePath = new File(file).getParent();
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(
                                                KEY_LOAD_SCHEMA_FILE + database.getId(),
                                                filteredFilePath);
                            }
                        }
                    });
        }
    }

    /**
     * Create load option information group
     *
     * @param parent the parent composite
     */
    private void createLoadOptionGruop(Composite parent) {
        Group unloadOptionGroup = new Group(parent, SWT.NONE);
        unloadOptionGroup.setText(Messages.grpLoadOption);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        unloadOptionGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        unloadOptionGroup.setLayout(layout);

        checkSyntaxButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        checkSyntaxButton.setText(Messages.btnCheckSyntax);
        checkSyntaxButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        checkSyntaxButton.setSelection(true);

        estimatedSizeButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        estimatedSizeButton.setText(Messages.btnNumOfInstances);
        estimatedSizeButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        estimatedSizeButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (estimatedSizeButton.getSelection()) {
                            estimatedSizeText.setText("5000");
                            estimatedSizeText.setEditable(true);
                        } else {
                            estimatedSizeText.setText("");
                            estimatedSizeText.setEditable(false);
                        }
                        valid();
                    }
                });
        estimatedSizeText = new Text(unloadOptionGroup, SWT.BORDER);
        estimatedSizeText.setTextLimit(8);
        estimatedSizeText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        estimatedSizeText.setEditable(false);
        estimatedSizeText.addModifyListener(this);

        commitPeriodButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        commitPeriodButton.setText(Messages.btnInsCount);
        commitPeriodButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        commitPeriodButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (commitPeriodButton.getSelection()) {
                            commitPeriodText.setText("10000");
                            commitPeriodText.setEditable(true);
                        } else {
                            commitPeriodText.setText("");
                            commitPeriodText.setEditable(false);
                        }
                        valid();
                    }
                });
        commitPeriodText = new Text(unloadOptionGroup, SWT.BORDER);
        commitPeriodText.setTextLimit(8);
        commitPeriodText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 2, 1, -1, -1));
        commitPeriodText.setEditable(false);
        commitPeriodText.addModifyListener(this);

        oidButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        oidButton.setText(Messages.btnNoUseOid);
        oidButton.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));

        if (CompatibleUtil.isSupportNoUseStatistics(database.getServer().getServerInfo())) {
            noStatisButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
            noStatisButton.setText(Messages.btnNoUseStatistics);
            noStatisButton.setLayoutData(
                    CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, -1, -1));
        }
        // error control file
        useErrorControlFileButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        useErrorControlFileButton.setText(Messages.btnUseErrorFile);
        useErrorControlFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        useErrorControlFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (useErrorControlFileButton.getSelection()) {
                            errorControlFileText.setEnabled(true);
                            if (selectErrorControlFileButton != null) {
                                ServerInfo serverInfo = database.getServer().getServerInfo();
                                selectErrorControlFileButton.setEnabled(
                                        serverInfo != null && serverInfo.isLocalServer());
                            }

                        } else {
                            errorControlFileText.setText("");
                            errorControlFileText.setEnabled(false);
                            if (selectErrorControlFileButton != null) {
                                selectErrorControlFileButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });
        errorControlFileText = new Text(unloadOptionGroup, SWT.BORDER);
        errorControlFileText.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
        errorControlFileText.setEnabled(false);
        errorControlFileText.addModifyListener(this);

        if (isLocalServer) {
            selectErrorControlFileButton = new Button(unloadOptionGroup, SWT.NONE);
            selectErrorControlFileButton.setText(Messages.btnBrowse);
            selectErrorControlFileButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            selectErrorControlFileButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = errorControlFileText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text =
                                        CubridManagerUIPlugin.getPluginDialogSettings()
                                                .get(KEY_ERROR_CONTROL_FILE + database.getId());
                            }
                            if (text == null || text.trim().length() == 0) {
                                text = dbDir;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String dir = dlg.open();
                            if (dir != null) {
                                errorControlFileText.setText(dir);
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(KEY_ERROR_CONTROL_FILE + database.getId(), dir);
                            }
                        }
                    });
            selectErrorControlFileButton.setEnabled(false);
        }
        // ignore class file
        ignoreClassFileButton = new Button(unloadOptionGroup, SWT.LEFT | SWT.CHECK);
        ignoreClassFileButton.setText(Messages.btnIgnoreClassFile);
        ignoreClassFileButton.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        ignoreClassFileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (ignoreClassFileButton.getSelection()) {
                            ignoredClassFileText.setEnabled(true);
                            if (selectIgnoreClassFileButton != null) {
                                ServerInfo serverInfo = database.getServer().getServerInfo();
                                selectIgnoreClassFileButton.setEnabled(
                                        serverInfo != null && serverInfo.isLocalServer());
                            }

                        } else {
                            ignoredClassFileText.setText("");
                            ignoredClassFileText.setEnabled(false);
                            if (selectIgnoreClassFileButton != null) {
                                selectIgnoreClassFileButton.setEnabled(false);
                            }
                        }
                        valid();
                    }
                });
        ignoredClassFileText = new Text(unloadOptionGroup, SWT.BORDER);
        ignoredClassFileText.setLayoutData(
                CommonUITool.createGridData(
                        GridData.FILL_HORIZONTAL, isLocalServer ? 1 : 2, 1, -1, -1));
        ignoredClassFileText.setEnabled(false);
        ignoredClassFileText.addModifyListener(this);

        if (isLocalServer) {
            selectIgnoreClassFileButton = new Button(unloadOptionGroup, SWT.NONE);
            selectIgnoreClassFileButton.setText(Messages.btnBrowse);
            selectIgnoreClassFileButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
            selectIgnoreClassFileButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
                            String text = ignoredClassFileText.getText();
                            if (text == null || text.trim().length() == 0) {
                                text =
                                        CubridManagerUIPlugin.getPluginDialogSettings()
                                                .get(KEY_IGNORE_CLASS_FILE + database.getId());
                            }
                            if (text == null || text.trim().length() == 0) {
                                text = dbDir;
                            }
                            if (text != null) {
                                dlg.setFilterPath(text);
                            }
                            dlg.setText(Messages.msgSelectFile);
                            String dir = dlg.open();
                            if (dir != null) {
                                ignoredClassFileText.setText(dir);
                                CubridManagerUIPlugin.getPluginDialogSettings()
                                        .put(KEY_IGNORE_CLASS_FILE + database.getId(), dir);
                            }
                        }
                    });
            selectIgnoreClassFileButton.setEnabled(false);
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleLoadDbDialog);
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
                parent,
                IDialogConstants.OK_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnOK,
                true);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        createButton(
                parent,
                IDialogConstants.CANCEL_ID,
                com.cubrid.cubridmanager.ui.common.Messages.btnCancel,
                false);
    }

    /**
     * Call this method when press button
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String msg = Messages.confirmLoaddbWillBeChangedPassword;
            if (!valid() || !CommonUITool.openConfirmBox(msg)) {
                return;
            }
            loadDb(buttonId);
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /**
     * Execute task and load database
     *
     * @param buttonId the button id
     */
    private void loadDb(final int buttonId) {
        TaskJobExecutor taskExcutor = new LoadDbTaskExecutor(buttonId);

        LoadDbTask loadDbTask1 = new LoadDbTask(database.getServer().getServerInfo());
        LoadDbTask loadDbTask2 = new LoadDbTask(database.getServer().getServerInfo());
        loadDbTask1.setDbName(databaseNameText.getText());
        loadDbTask2.setDbName(databaseNameText.getText());
        loadDbTask1.setDbUser(userNameText.getText());
        loadDbTask2.setDbUser(userNameText.getText());
        if (checkSyntaxButton.getSelection()) {
            loadDbTask1.setCheckOption("both");
            loadDbTask2.setCheckOption("both");
        } else {
            loadDbTask1.setCheckOption("load");
            loadDbTask2.setCheckOption("load");
        }
        if (commitPeriodButton.getSelection()) {
            loadDbTask1.setUsedPeriod(true, commitPeriodText.getText());
            loadDbTask2.setUsedPeriod(true, commitPeriodText.getText());
        } else {
            loadDbTask1.setUsedPeriod(false, "");
            loadDbTask2.setUsedPeriod(false, "");
        }
        if (estimatedSizeButton.getSelection()) {
            loadDbTask1.setUsedEstimatedSize(true, estimatedSizeText.getText());
            loadDbTask2.setUsedEstimatedSize(true, estimatedSizeText.getText());
        } else {
            loadDbTask1.setUsedEstimatedSize(false, "");
            loadDbTask2.setUsedEstimatedSize(false, "");
        }
        if (useErrorControlFileButton.getSelection()) {
            loadDbTask1.setUsedErrorContorlFile(true, errorControlFileText.getText());
            loadDbTask2.setUsedErrorContorlFile(true, errorControlFileText.getText());
        } else {
            loadDbTask1.setUsedErrorContorlFile(false, "");
            loadDbTask2.setUsedErrorContorlFile(false, "");
        }
        if (ignoreClassFileButton.getSelection()) {
            loadDbTask1.setUsedIgnoredClassFile(true, ignoredClassFileText.getText());
            loadDbTask2.setUsedIgnoredClassFile(true, ignoredClassFileText.getText());
        } else {
            loadDbTask1.setUsedIgnoredClassFile(false, "");
            loadDbTask2.setUsedIgnoredClassFile(false, "");
        }
        loadDbTask1.setNoUsedOid(oidButton.getSelection());
        loadDbTask2.setNoUsedOid(oidButton.getSelection());
        if (noStatisButton != null) {
            loadDbTask1.setNoUsedStatistics(noStatisButton.getSelection());
            loadDbTask2.setNoUsedStatistics(noStatisButton.getSelection());
        }
        loadDbTask1.setNoUsedLog(false);
        loadDbTask2.setNoUsedLog(false);
        if (selectLoadFileFromListButton.getSelection()) {
            String schemaPath = "";
            String objectPath = "";
            String indexPath = "";
            String triggerPath = "";
            for (int i = 0, n = unloadInfoTable.getItemCount(); i < n; i++) {
                if (unloadInfoTable.getItem(i).getChecked()) {
                    String type = unloadInfoTable.getItem(i).getText(0);
                    String path = unloadInfoTable.getItem(i).getText(1);
                    if (type != null && type.trim().equals("schema")) {
                        schemaPath = path;
                    }
                    if (type != null && type.trim().equals("object")) {
                        objectPath = path;
                    }
                    if (type != null && type.trim().equals("index")) {
                        indexPath = path;
                    }
                    if (type != null && type.trim().equals("trigger")) {
                        triggerPath = path;
                    }
                }
            }
            boolean isAddTask1 = false;
            if (schemaPath != null && schemaPath.trim().length() > 0) {
                loadDbTask1.setSchemaPath(schemaPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setSchemaPath("none");
            }
            if (objectPath != null && objectPath.trim().length() > 0) {
                loadDbTask1.setObjectPath(objectPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setObjectPath("none");
            }
            if (indexPath != null && indexPath.trim().length() > 0) {
                loadDbTask1.setIndexPath(indexPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setIndexPath("none");
            }
            boolean isAddTask2 = false;
            if (triggerPath != null && triggerPath.trim().length() > 0) {
                loadDbTask2.setSchemaPath(triggerPath);
                isAddTask2 = true;
            }
            if (isAddTask1) {
                taskExcutor.addTask(loadDbTask1);
            }
            if (isAddTask2) {
                taskExcutor.addTask(loadDbTask2);
            }

        } else if (selectUnloadFileFromSysButton.getSelection()) {
            boolean isAddTask1 = false;
            if (loadSchemaButton.getSelection()) {
                String schemaPath = loadSchemaText.getText();
                loadDbTask1.setSchemaPath(schemaPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setSchemaPath("none");
            }
            if (loadObjButton.getSelection()) {
                String objPath = loadObjText.getText();
                loadDbTask1.setObjectPath(objPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setObjectPath("none");
            }
            if (loadIndexButton.getSelection()) {
                String indexPath = loadIndexText.getText();
                loadDbTask1.setIndexPath(indexPath);
                isAddTask1 = true;
            } else {
                loadDbTask1.setIndexPath("none");
            }
            boolean isAddTask2 = false;
            if (loadTriggerButton.getSelection()) {
                String triggerPath = loadTriggerText.getText();
                loadDbTask2.setSchemaPath(triggerPath);
                isAddTask2 = true;
            }
            if (isAddTask1) {
                taskExcutor.addTask(loadDbTask1);
            }
            if (isAddTask2) {
                taskExcutor.addTask(loadDbTask2);
            }
        }

        JobFamily jobFamily = new JobFamily();
        String serverName = database.getServer().getName();
        String dbName = database.getName();
        jobFamily.setServerName(serverName);
        jobFamily.setDbName(dbName);

        String jobName = Messages.msgLoadDBRearJobName + " - " + dbName + "@" + serverName;
        taskExcutor.schedule(jobName, jobFamily, true, Job.SHORT);
    }

    /** Initial data */
    private void initial() {

        IDialogSettings dialogSettings = CubridManagerUIPlugin.getPluginDialogSettings();
        filteredFilePath = dialogSettings.get(KEY_LOAD_SCHEMA_FILE + database.getId());
        if (filteredFilePath == null || filteredFilePath.trim().length() == 0) {
            filteredFilePath = database.getDatabaseInfo().getDbDir();
        }

        databaseNameText.setText(database.getLabel());
        userNameText.setText(database.getUserName());
        dbDir = database.getDatabaseInfo().getDbDir();
        if (dbUnloadInfoList == null || dbUnloadInfoList.isEmpty()) {
            selectLoadFileFromListButton.setSelection(false);
            databaseCombo.setEnabled(false);
            selectUnloadFileFromSysButton.setSelection(true);
        } else {
            int index = 0;
            for (int i = 0; i < dbUnloadInfoList.size(); i++) {
                DbUnloadInfo dbUnloadInfo = dbUnloadInfoList.get(i);
                databaseCombo.add(dbUnloadInfo.getDbName());
                if (dbUnloadInfo.getDbName().equals(database.getLabel())) {
                    index = i;
                }
            }
            databaseCombo.select(index);
            setTableModel(databaseCombo.getText());
            selectUnloadFileFromSysButton.setSelection(false);
            selectLoadFileFromListButton.setSelection(true);
            setBtnStatusInList(true);
            setBtnStatusInSys(false);
        }
    }

    /**
     * Set tableViewer input model of some database
     *
     * @param dbName the database name
     */
    private void setTableModel(String dbName) {
        List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        if (dbUnloadInfoList != null && !dbUnloadInfoList.isEmpty()) {
            DbUnloadInfo dbUnloadInfo = null;
            for (int i = 0; i < dbUnloadInfoList.size(); i++) {
                dbUnloadInfo = dbUnloadInfoList.get(i);
                if (dbUnloadInfo.getDbName().equals(dbName)) {
                    break;
                }
            }
            if (dbUnloadInfo != null) {
                List<String> pathList = dbUnloadInfo.getSchemaPathList();
                List<String> dateList = dbUnloadInfo.getSchemaDateList();
                for (int i = 0; i < pathList.size() && i < dateList.size(); i++) {
                    String path = pathList.get(i);
                    String date = dateList.get(i);
                    if (path != null && path.trim().length() > 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        path =
                                FileUtil.changeSeparatorByOS(
                                        path,
                                        database.getServer().getServerInfo().getServerOsInfo());
                        map.put("0", "schema");
                        map.put("1", path);
                        map.put("2", date);
                        dataList.add(map);
                    }
                }
                pathList = dbUnloadInfo.getObjectPathList();
                dateList = dbUnloadInfo.getObjectDateList();
                for (int i = 0; i < pathList.size() && i < dateList.size(); i++) {
                    String path = pathList.get(i);
                    String date = dateList.get(i);
                    if (path != null && path.trim().length() > 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        path =
                                FileUtil.changeSeparatorByOS(
                                        path,
                                        database.getServer().getServerInfo().getServerOsInfo());
                        map.put("0", "object");
                        map.put("1", path);
                        map.put("2", date);
                        dataList.add(map);
                    }
                }
                pathList = dbUnloadInfo.getIndexPathList();
                dateList = dbUnloadInfo.getIndexDateList();
                for (int i = 0; i < pathList.size() && i < dateList.size(); i++) {
                    String path = pathList.get(i);
                    String date = dateList.get(i);
                    if (path != null && path.trim().length() > 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        path =
                                FileUtil.changeSeparatorByOS(
                                        path,
                                        database.getServer().getServerInfo().getServerOsInfo());
                        map.put("0", "index");
                        map.put("1", path);
                        map.put("2", date);
                        dataList.add(map);
                    }
                }
                pathList = dbUnloadInfo.getTriggerPathList();
                dateList = dbUnloadInfo.getTriggerDateList();
                for (int i = 0; i < pathList.size() && i < dateList.size(); i++) {
                    String path = pathList.get(i);
                    String date = dateList.get(i);
                    if (path != null && path.trim().length() > 0) {
                        Map<String, String> map = new HashMap<String, String>();
                        path =
                                FileUtil.changeSeparatorByOS(
                                        path,
                                        database.getServer().getServerInfo().getServerOsInfo());
                        map.put("0", "trigger");
                        map.put("1", path);
                        map.put("2", date);
                        dataList.add(map);
                    }
                }
            }
        }
        if (!dataList.isEmpty()) {
            tableViewer.setInput(dataList);
            tableViewer.refresh();
            for (int i = 0; i < unloadInfoTable.getColumnCount(); i++) {
                unloadInfoTable.getColumn(i).pack();
            }
        }
    }

    /**
     * Check the validation
     *
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    private boolean valid() {
        boolean isValidSchemaPath = true;
        boolean isValidObjPath = true;
        boolean isValidIndexPath = true;
        boolean isValidTriggerPath = true;
        boolean isValidFileSystem = true;
        if (selectUnloadFileFromSysButton.getSelection()) {
            isValidFileSystem = false;
            if (loadSchemaButton.getSelection()) {
                String schemaPath = loadSchemaText.getText();
                isValidSchemaPath = schemaPath.trim().length() > 0;
                if (isValidSchemaPath && database.getServer().getServerInfo().isLocalServer()) {
                    File file = new File(schemaPath);
                    if (!file.exists()) {
                        isValidSchemaPath = false;
                    }
                }
                isValidFileSystem = true;
            }
            if (loadObjButton.getSelection()) {
                String objPath = loadObjText.getText();
                isValidObjPath = objPath.trim().length() > 0;
                if (isValidObjPath && database.getServer().getServerInfo().isLocalServer()) {
                    File file = new File(objPath);
                    if (!file.exists()) {
                        isValidObjPath = false;
                    }
                }
                isValidFileSystem = true;
            }
            if (loadIndexButton.getSelection()) {
                String indexPath = loadIndexText.getText();
                isValidIndexPath = indexPath.trim().length() > 0;
                if (isValidIndexPath && database.getServer().getServerInfo().isLocalServer()) {
                    File file = new File(indexPath);
                    if (!file.exists()) {
                        isValidObjPath = false;
                    }
                }
                isValidFileSystem = true;
            }
            if (loadTriggerButton.getSelection()) {
                String triggerPath = loadTriggerText.getText();
                isValidTriggerPath = triggerPath.trim().length() > 0;
                if (isValidTriggerPath && database.getServer().getServerInfo().isLocalServer()) {
                    File file = new File(triggerPath);
                    if (!file.exists()) {
                        isValidTriggerPath = false;
                    }
                }
                isValidFileSystem = true;
            }
        }
        boolean isValidUnLoadDb = true;
        boolean isSelectedDbPath = true;
        if (selectLoadFileFromListButton.getSelection()) {
            isSelectedDbPath = false;
            String dbName = databaseCombo.getText();
            if (dbName == null || dbName.trim().length() <= 0) {
                isValidUnLoadDb = false;
            }
            for (int i = 0, n = unloadInfoTable.getItemCount(); i < n && isValidUnLoadDb; i++) {
                if (unloadInfoTable.getItem(i).getChecked()) {
                    isSelectedDbPath = true;
                    break;
                }
            }
        }
        boolean isValidCommitPeriod = true;
        if (commitPeriodButton.getSelection()) {
            String period = commitPeriodText.getText();
            isValidCommitPeriod = ValidateUtil.isNumber(period) && Integer.parseInt(period) > 0;
        }
        boolean isValidEstimatedSize = true;
        if (estimatedSizeButton.getSelection()) {
            String size = estimatedSizeText.getText();
            isValidEstimatedSize = ValidateUtil.isNumber(size) && Integer.parseInt(size) > 0;
        }
        boolean isValidErrorControlFile = true;
        if (useErrorControlFileButton.getSelection()) {
            String filePath = errorControlFileText.getText();
            isValidErrorControlFile = filePath.trim().length() > 0;
            if (isValidErrorControlFile && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(filePath);
                if (!file.exists()) {
                    isValidErrorControlFile = false;
                }
            }
        }
        boolean isValidIgnoreClassFile = true;
        if (ignoreClassFileButton.getSelection()) {
            String filePath = ignoredClassFileText.getText();
            isValidIgnoreClassFile = filePath.trim().length() > 0;
            if (isValidIgnoreClassFile && database.getServer().getServerInfo().isLocalServer()) {
                File file = new File(filePath);
                if (!file.exists()) {
                    isValidIgnoreClassFile = false;
                }
            }
        }
        if (!isValidUnLoadDb) {
            setErrorMessage(Messages.errLoadFileFromList);
            setEnabled(false);
            return false;
        }
        if (!isSelectedDbPath) {
            setErrorMessage(Messages.errNoSelectedPath);
            setEnabled(false);
            return false;
        }
        if (!isValidFileSystem) {
            setErrorMessage(Messages.errLoadFileFromSys);
            setEnabled(false);
            return false;
        }
        if (!isValidSchemaPath) {
            setErrorMessage(Messages.errLoadSchema);
            setEnabled(false);
            return false;
        }
        if (!isValidObjPath) {
            setErrorMessage(Messages.errLoadOjbects);
            setEnabled(false);
            return false;
        }
        if (!isValidIndexPath) {
            setErrorMessage(Messages.errLoadIndex);
            setEnabled(false);
            return false;
        }
        if (!isValidTriggerPath) {
            setErrorMessage(Messages.errLoadTrigger);
            setEnabled(false);
            return false;
        }
        if (!isValidEstimatedSize) {
            setErrorMessage(Messages.errNumOfInstances);
            setEnabled(false);
            return false;
        }
        if (!isValidCommitPeriod) {
            setErrorMessage(Messages.errInsertCount);
            setEnabled(false);
            return false;
        }
        if (!isValidErrorControlFile) {
            setErrorMessage(Messages.errControlFile);
            setEnabled(false);
            return false;
        }
        if (!isValidIgnoreClassFile) {
            setErrorMessage(Messages.errClassFile);
            setEnabled(false);
            return false;
        }
        setErrorMessage(null);
        setEnabled(true);

        return true;
    }

    /**
     * Enable or disable the OK button
     *
     * @param isEnabled whether enabled
     */
    private void setEnabled(boolean isEnabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(isEnabled);
        }
    }

    /**
     * Listen to modify event
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        valid();
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

    public void setDbUnloadInfoList(List<DbUnloadInfo> dbUnloadInfoList) {
        this.dbUnloadInfoList = dbUnloadInfoList;
    }

    /**
     * Load database task executor
     *
     * @author pangqiren
     * @version 1.0 - 2010-1-7 created by pangqiren
     */
    class LoadDbTaskExecutor extends TaskJobExecutor {
        private final int buttonId;

        public LoadDbTaskExecutor(int buttonId) {
            this.buttonId = buttonId;
        }

        /**
         * Execute to load database
         *
         * @param monitor the IProgressMonitor object
         * @return the IStatus
         */
        public IStatus exec(final IProgressMonitor monitor) {
            Display.getDefault()
                    .syncExec(
                            new Runnable() {
                                public void run() {
                                    getShell().setVisible(false);
                                }
                            });
            if (monitor.isCanceled()) {
                cancel();
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        close();
                                    }
                                });
                return Status.CANCEL_STATUS;
            }
            for (ITask task : taskList) {
                task.execute();
                final String msg = task.getErrorMsg();
                if (msg != null && msg.length() > 0 && !monitor.isCanceled() && !isCanceled()) {
                    Display.getDefault()
                            .syncExec(
                                    new Runnable() {
                                        public void run() {
                                            getShell().setVisible(true);
                                        }
                                    });
                    return new Status(IStatus.ERROR, CubridManagerUIPlugin.PLUGIN_ID, msg);
                }
                if (isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                if (task instanceof LoadDbTask) {
                    LoadDbTask loadDbTask = (LoadDbTask) task;
                    String[] result = loadDbTask.getLoadResult();
                    if (result != null && result.length > 0) {
                        for (int i = 0; i < result.length; i++) {
                            loadDbRusultStr += result[i] + StringUtil.NEWLINE;
                        }
                    }
                }
                if (monitor.isCanceled()) {
                    cancel();
                    Display.getDefault()
                            .syncExec(
                                    new Runnable() {
                                        public void run() {
                                            close();
                                        }
                                    });

                    return Status.CANCEL_STATUS;
                }
            }
            return Status.OK_STATUS;
        }

        /**
         * Notification that a job has completed execution
         *
         * @param event the event details
         */
        public void done(IJobChangeEvent event) {
            if (event.getResult() == Status.OK_STATUS) {
                Display.getDefault()
                        .syncExec(
                                new Runnable() {
                                    public void run() {
                                        LoadDatabaseResultDialog dialog =
                                                new LoadDatabaseResultDialog(getShell());
                                        dialog.setResultInfoStr(loadDbRusultStr);
                                        dialog.open();

                                        setReturnCode(buttonId);
                                        close();
                                    }
                                });
            }
        }
    }
}
