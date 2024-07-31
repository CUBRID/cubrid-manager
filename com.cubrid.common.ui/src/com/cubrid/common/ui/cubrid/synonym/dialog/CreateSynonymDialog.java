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
package com.cubrid.common.ui.cubrid.synonym.dialog;

import com.cubrid.common.core.common.model.Synonym;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.synonym.Messages;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.progress.CommonTaskExec;
import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.common.ui.spi.progress.TaskExecutor;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.synonym.SynonymDDL;
import com.cubrid.cubridmanager.core.cubrid.trigger.task.JDBCSqlExecuteTask;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class CreateSynonymDialog extends CMTitleAreaDialog {
    private StyledText sqlText;
    private final Color white = ResourceManager.getColor(SWT.COLOR_WHITE);
    private Text nameText = null;
    private Combo ownerCombo = null;
    private Text targetNameText = null;
    private Combo targetOwnerCombo = null;
    private Text commentText = null;

    private final CubridDatabase database;
    private final DatabaseInfo dbInfo;
    private List<String> dbUserList = null;

    private Synonym synonym = null;
    private TabFolder tabFolder;
    public static final int ALTER_SYNONYM_OK_ID = 100;

    public CreateSynonymDialog(
            Shell parentShell, CubridDatabase database, List<String> dbUserList) {
        super(parentShell);
        this.database = database;
        this.dbInfo = database.getDatabaseInfo();
        this.dbUserList = dbUserList;
    }

    public CreateSynonymDialog(
            Shell parentShell, CubridDatabase database, Synonym synonym, List<String> dbUserList) {
        super(parentShell);
        this.database = database;
        this.synonym = synonym;
        this.dbInfo = database.getDatabaseInfo();
        this.dbUserList = dbUserList;
    }

    /**
     * Create dialog area content
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite tabComposite = new Composite(parentComp, SWT.NONE);
        {
            final GridData gdComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
            tabComposite.setLayoutData(gdComposite);
            GridLayout tabCompositeLayout = new GridLayout();
            tabCompositeLayout.numColumns = 1;
            tabCompositeLayout.marginHeight =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            tabCompositeLayout.marginWidth =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            tabCompositeLayout.verticalSpacing =
                    convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            tabCompositeLayout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            tabCompositeLayout.numColumns = 1;
            tabComposite.setLayout(tabCompositeLayout);
        }

        tabFolder = new TabFolder(tabComposite, SWT.NONE);
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create synonym tab
        final TabItem synonymTabItem = new TabItem(tabFolder, SWT.NONE);
        synonymTabItem.setText(Messages.infoSynonymTab);
        synonymTabItem.setControl(createSynonymComposite(tabFolder));

        // create the SQL tab
        final Composite sqlScriptComposite = new Composite(tabFolder, SWT.NONE);
        sqlScriptComposite.setLayout(new GridLayout());
        final TabItem sqlScriptTabItem = new TabItem(tabFolder, SWT.NONE);
        sqlScriptTabItem.setText(Messages.infoSQLScriptTab);
        sqlScriptTabItem.setControl(sqlScriptComposite);

        sqlText =
                new StyledText(
                        sqlScriptComposite,
                        SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL | SWT.BORDER);
        CommonUITool.registerContextMenu(sqlText, false);
        sqlText.setBackground(white);
        sqlText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        setTitle(Messages.newSynonymMSGTitle);
        setMessage(Messages.newSynonymMsg);

        createInit();
        alterInit();
        addListener();
        return parentComp;
    }

    /**
     * Create the synonym tab composite
     *
     * @param parent Composite
     * @return Composite
     */
    private Composite createSynonymComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout();
            layout.numColumns = 5;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        Label SynonymInfoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        SynonymInfoLabel.setText(Messages.synonymInfoLabel);
        SynonymInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        ownerCombo = new Combo(composite, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        ownerCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 30, -1));
        nameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        nameText.setLayoutData(CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, 30, -1));

        Label targetInfoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        targetInfoLabel.setText(Messages.synonymTargetInfoLabel);
        targetInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        targetOwnerCombo = new Combo(composite, SWT.LEFT | SWT.BORDER | SWT.READ_ONLY);
        targetOwnerCombo.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 1, 1, 30, -1));
        targetNameText = new Text(composite, SWT.LEFT | SWT.BORDER);
        targetNameText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 3, 1, 30, -1));

        Label commentInfoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        commentInfoLabel.setText(Messages.synonymCommentLabel);
        commentInfoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        commentText = new Text(composite, SWT.LEFT | SWT.BORDER);
        commentText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_HORIZONTAL, 5, 1, 30, -1));
        commentText.setTextLimit(ValidateUtil.MAX_DB_OBJECT_COMMENT);
        return composite;
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        if (null == synonym) {
            createButton(parent, IDialogConstants.OK_ID, Messages.okBTN, false);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        } else {
            createButton(parent, ALTER_SYNONYM_OK_ID, Messages.okBTN, false);
            getButton(ALTER_SYNONYM_OK_ID).setEnabled(false);
        }
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.cancleBTN, false);
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId != IDialogConstants.OK_ID && buttonId != ALTER_SYNONYM_OK_ID) {
            super.buttonPressed(buttonId);
            return;
        }

        if (!validateAll()) {
            return;
        }
        executeByJDBC(buttonId);
    }

    /**
     * Execute to add or alter synonym by JDBC
     *
     * @param buttonId int
     */
    private void executeByJDBC(int buttonId) { // FIXME move this logic to core module
        String createSQL = generateSqlText().toString();
        if (StringUtil.isEmpty(createSQL)) {
            return;
        }

        String SynonyUniqueName = ownerCombo.getText() + "." + nameText.getText();
        String taskName = null;
        String message = null;
        if (buttonId == IDialogConstants.OK_ID) {
            taskName = Messages.bind(Messages.addSynonymTaskName, SynonyUniqueName);
            message = Messages.newSynonymSuccess;
        } else if (buttonId == ALTER_SYNONYM_OK_ID) {
            createSQL = createSQL.substring(createSQL.indexOf("ALTER SYNONYM "));
            message = Messages.alterSynonymSuccess;
            taskName = Messages.bind(Messages.alterSynonymTaskName, SynonyUniqueName);
        }
        // add or alter synonyms by JDBC
        JDBCSqlExecuteTask jdbcTask =
                new JDBCSqlExecuteTask(taskName, database.getDatabaseInfo(), createSQL);
        TaskExecutor taskExecutor = new CommonTaskExec(taskName);
        taskExecutor.addTask(jdbcTask);
        new ExecTaskWithProgress(taskExecutor).busyCursorWhile();
        if (taskExecutor.isSuccess()) {
            setReturnCode(buttonId);
            close();
            CommonUITool.openInformationBox(Messages.msgInformation, message);
        }
    }

    /** Init data when create synonym */
    private void createInit() {

        nameText.setToolTipText(Messages.ToolTipName);
        ownerCombo.setToolTipText(Messages.ToolTipOwner);
        targetNameText.setToolTipText(Messages.ToolTipTargetName);
        targetOwnerCombo.setToolTipText(Messages.ToolTipTargetOwner);
        commentText.setToolTipText(Messages.ToolTipComment);

        ownerCombo.setVisibleItemCount(20);
        targetOwnerCombo.setVisibleItemCount(20);

        if (dbInfo != null) {
            String ownerName;
            for (int i = 0; i < dbUserList.size(); i++) {
                ownerName = dbUserList.get(i);
                ownerCombo.add(ownerName);
                targetOwnerCombo.add(ownerName);
                boolean isSameOwner = false;
                boolean isSameTargetOwner = false;
                if (synonym == null) {
                    isSameOwner =
                            StringUtil.isEqualIgnoreCase(
                                    ownerName,
                                    database.getDatabaseInfo()
                                            .getAuthLoginedDbUserInfo()
                                            .getName());
                } else {
                    isSameOwner = StringUtil.isEqualIgnoreCase(ownerName, synonym.getOwner());
                    isSameTargetOwner =
                            StringUtil.isEqualIgnoreCase(ownerName, synonym.getTargetOwner());
                }
                if (isSameOwner) {
                    ownerCombo.select(i);
                    targetOwnerCombo.select(i);
                }

                if (isSameTargetOwner) {
                    targetOwnerCombo.select(i);
                }
            }
        }

        nameText.setFocus();
    }

    /** Init the data when alter synonym */
    private void alterInit() {
        if (synonym == null) {
            return;
        }
        setMessage(Messages.synonymAlterMSG);
        setTitle(Messages.synonymAlterMSGTitle);

        nameText.setText(synonym.getName());
        nameText.setEnabled(false);
        ownerCombo.setEnabled(false);

        targetNameText.setText(synonym.getTargetName());
        if (synonym.getComment() != null) {
            commentText.setText(synonym.getComment());
        }
    }

    /** Add listener */
    private void addListener() {
        if (synonym == null) {
            nameText.addModifyListener(
                    new ModifyListener() {
                        public void modifyText(ModifyEvent event) {
                            boolean valid = validateSynonymName();
                            if (valid) {
                                validateAll();
                            } else {
                                changeOKButtonStatus(false);
                            }
                            nameText.setFocus();
                        }
                    });
        }

        targetNameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        boolean valid = validateTargetName();
                        if (valid) {
                            validateAll();
                        } else {
                            changeOKButtonStatus(false);
                        }
                        targetNameText.setFocus();
                    }
                });

        targetOwnerCombo.addSelectionListener(
                new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        validateAll();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });

        commentText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        boolean valid = validateTargetName();
                        if (valid) {
                            validateAll();
                        } else {
                            changeOKButtonStatus(false);
                        }
                    }
                });

        tabFolder.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(final SelectionEvent event) {
                        if (tabFolder.getSelectionIndex() == 0) {
                            nameText.setFocus();
                        } else if (tabFolder.getSelectionIndex() == tabFolder.getItemCount() - 1) {
                            sqlText.setText(generateSqlText().toString());
                        }
                    }
                });
    }

    Synonym newSynonym = null;

    /**
     * Get new synonym
     *
     * @return the synonym object
     */
    private Synonym getNewSynonym() { // FIXME move this logic to core module
        newSynonym = new Synonym();
        String synoymName = nameText.getText();
        String ownerName = ownerCombo.getText();
        String targetName = targetNameText.getText();
        String targetOwner = targetOwnerCombo.getText();
        String comment = commentText.getText();

        newSynonym.setName(synoymName);
        newSynonym.setOwner(ownerName);
        newSynonym.setTargetName(targetName);
        newSynonym.setTargetOwner(targetOwner);
        newSynonym.setComment(comment);
        return newSynonym;
    }

    /**
     * Validate synonym information
     *
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    private boolean validateAll() {
        setErrorMessage(null);
        changeOKButtonStatus(false);
        if (!validateSynonymName()) {
            return false;
        }
        if (!validateTargetName()) {
            return false;
        }
        if (null != synonym && !isChanged(synonym, getNewSynonym())) {
            return false;
        }
        changeOKButtonStatus(true);
        return true;
    }

    /**
     * Change ok button status: enabled, or disabled
     *
     * @param valid whether it is enabled
     */
    private void changeOKButtonStatus(boolean valid) {
        if (null == synonym) {
            getButton(IDialogConstants.OK_ID).setEnabled(valid);
        } else {
            getButton(ALTER_SYNONYM_OK_ID).setEnabled(valid);
        }
    }

    /**
     * Validate synonym name
     *
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    private boolean validateSynonymName() {
        String synonymName = nameText.getText();
        if (!ValidateUtil.isValidIdentifier(synonymName)) {
            setErrorMessage(Messages.invalidSynonymNameError);
            nameText.setFocus();
            return false;
        }
        return true;
    }

    /**
     * Validate synonym name
     *
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    private boolean validateTargetName() {
        String targetName = targetNameText.getText();
        if (!ValidateUtil.isValidIdentifier(targetName)) {
            setErrorMessage(Messages.invalidTargetNameError);
            targetNameText.setFocus();
            return false;
        }
        return true;
    }

    /**
     * Return true if synonym is changed.
     *
     * @param oldSynonym the old synonym
     * @param newSynonym the new synonym
     * @return <code>true</code> if it is changed;<code>false</code>otherwise
     */
    public boolean isChanged(Synonym oldSynonym, Synonym newSynonym) {
        if (null != oldSynonym && null != newSynonym) {
            String oldDescription = oldSynonym.getComment();
            String newDescription = newSynonym.getComment();
            if (!newDescription.equals(oldDescription)) {
                return true;
            }

            if (!oldSynonym.equals(newSynonym)) {
                return true;
            }

            if (!oldSynonym.targetEquals(newSynonym)) {
                return true;
            }
        }
        return false;
    }

    /** @return Sql string buffer */
    private StringBuffer generateSqlText() { // FIXME move this logic to core module
        StringBuffer sql = new StringBuffer();
        Synonym newSynonym = getNewSynonym();
        if (null == synonym) {
            sql.append(SynonymDDL.getDDL(newSynonym));
        } else {
            sql.append(SynonymDDL.getDDL(newSynonym));
            sql.append(StringUtil.NEWLINE);
            sql.append(StringUtil.NEWLINE);
            sql.append(StringUtil.NEWLINE);
            sql.append(SynonymDDL.getAlterDDL(synonym, newSynonym));
        }
        return sql;
    }

    public Synonym getSynonym() {
        return newSynonym;
    }
}
