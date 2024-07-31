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

import com.cubrid.common.core.common.model.DBResolution;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog of add resolution
 *
 * @author pangqiren 2009-6-4
 */
public class AddResolutionDialog extends CMTitleAreaDialog implements ModifyListener {

    private final String[][] conflicts;
    private final String[][] classConflicts;
    private Text aliasText;
    private Combo superCombo;
    private Combo columnCombo;
    private String[] supers;
    private String[] columns;
    private DBResolution resolution;
    private String[][] currentConflicts = null;
    private Button instanceButton;
    private boolean isClassResolution;
    private final String tableName;
    private final SchemaInfo schema;

    /**
     * The constructor
     *
     * @param parentShell
     * @param conflicts
     * @param classConflicts
     * @param schema
     */
    public AddResolutionDialog(
            Shell parentShell, String[][] conflicts, String[][] classConflicts, SchemaInfo schema) {
        super(parentShell);
        if (conflicts == null || conflicts.length <= 0) {
            this.conflicts = null;
        } else {
            this.conflicts = new String[conflicts.length][];
            for (int i = 0; i < conflicts.length; i++) {
                this.conflicts[i] = (String[]) conflicts[i].clone();
            }
        }
        if (classConflicts == null || classConflicts.length <= 0) {
            this.classConflicts = null;
        } else {
            this.classConflicts = new String[classConflicts.length][];
            for (int i = 0; i < classConflicts.length; i++) {
                this.classConflicts[i] = (String[]) classConflicts[i].clone();
            }
        }
        this.tableName = schema.getClassname();
        this.schema = schema;
    }

    /** initializes some values. */
    private void init() {
        instanceButton.setSelection(true);
        fireResolutionTypeChanged();
        superCombo.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        fireSuperComboChanged();
                    }
                });
        columnCombo.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        fireColumnComboChanged();
                    }
                });
        superCombo.addModifyListener(this);
        columnCombo.addModifyListener(this);
        aliasText.addModifyListener(this);
    }

    /** fire Super Combo Changed */
    private void fireSuperComboChanged() {
        String sup = superCombo.getText();
        if ("".equals(sup)) { // $NON-NLS-1$
            fillColumnCombo();
        } else if ("".equals(columnCombo.getText())) {
            columnCombo.removeAll();
            for (String[] str : currentConflicts) {
                if (sup.equals(str[2])) {
                    columnCombo.add(str[0]);
                }
            }
        }
    }

    /** fire Column Combo Changed */
    private void fireColumnComboChanged() {
        String column = columnCombo.getText();
        if (StringUtil.isNotEmpty(column)
                && StringUtil.isEmpty(superCombo.getText())) { // $NON-NLS-1$
            superCombo.removeAll();
            for (String[] str : currentConflicts) {
                if (column.equals(str[0]) && !str[2].equals(tableName)) {
                    superCombo.add(str[2]);
                }
            }
        }
    }

    /** fill Super Combo */
    private void fillSuperCombo() {
        superCombo.removeAll();
        superCombo.add(""); // $NON-NLS-1$
        for (String str : supers) {
            superCombo.add(str);
        }
    }

    /** fill Column Combo */
    private void fillColumnCombo() {
        columnCombo.removeAll();
        columnCombo.add(""); // $NON-NLS-1$
        for (String str : columns) {
            columnCombo.add(str);
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        createComposite(composite);

        setTitle(Messages.msgTitleSetResolution);
        setMessage(Messages.msgSetResolution);

        init();
        return parentComp;
    }

    /** @see com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog#constrainShellSize() */
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().setText(Messages.titleTitleSetResolution);
        getShell().setSize(500, 400);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
        validate();
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     * @param buttonId the id of the button that was pressed (see <code>IDialogConstants.*_ID</code>
     *     constants)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String sup = superCombo.getText();
            String col = columnCombo.getText();
            String alias = aliasText.getText().trim();
            resolution = new DBResolution();
            resolution.setName(col);
            resolution.setTableName(sup);
            resolution.setAlias(alias);
            resolution.setClassResolution(isClassResolution);
        } else {
            resolution = null;
        }
        super.buttonPressed(buttonId);
    }

    /**
     * create Composite
     *
     * @param parent Composite
     */
    private void createComposite(Composite parent) {

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
        Group group = new Group(parent, SWT.NONE);
        group.setLayoutData(gridData);
        group.setLayout(gridLayout);

        final Label resolutionTypeLabel = new Label(group, SWT.NONE);
        resolutionTypeLabel.setText(Messages.lblResolutionType);

        final Composite btnComposite = new Composite(group, SWT.NONE);
        btnComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        btnComposite.setLayout(gridLayout);

        Button classButton = new Button(btnComposite, SWT.RADIO);
        classButton.setText(Messages.typeClass);
        classButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        fireResolutionTypeChanged();
                    }
                });

        instanceButton = new Button(btnComposite, SWT.RADIO);
        instanceButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        instanceButton.setText(Messages.typeInstance);
        instanceButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        fireResolutionTypeChanged();
                    }
                });

        Label superTblLabel = new Label(group, SWT.LEFT | SWT.WRAP);
        superTblLabel.setText(Messages.lblSuperClass);

        superCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        superCombo.setLayoutData(gridData);
        superCombo.setToolTipText(Messages.lblTipSuperClass);

        Label colNameLabel = new Label(group, SWT.LEFT | SWT.WRAP);
        colNameLabel.setText(Messages.lblColumnName);
        columnCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        columnCombo.setLayoutData(gridData);

        Label aliasNameLabel = new Label(group, SWT.LEFT | SWT.WRAP);
        aliasNameLabel.setText(Messages.lblAlias);
        aliasText = new Text(group, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        aliasText.setLayoutData(gridData);
    }

    /** Fire Resolution Type Changed */
    protected void fireResolutionTypeChanged() {
        if (instanceButton.getSelection()) {
            currentConflicts = conflicts;
            isClassResolution = false;
        } else {
            currentConflicts = classConflicts;
            isClassResolution = true;
        }
        Set<String> set = new HashSet<String>();
        if (currentConflicts != null) {
            for (String[] str : currentConflicts) {
                if (!str[2].equals(tableName)) {
                    set.add(str[2]);
                }
            }
        }
        supers = set.toArray(new String[set.size()]);

        set.clear();
        if (currentConflicts != null) {
            for (String[] str : currentConflicts) {
                set.add(str[0]);
            }
        }
        columns = set.toArray(new String[set.size()]);
        fillSuperCombo();
        fillColumnCombo();
        aliasText.setText("");
    }

    /**
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     * @param event an event containing information about the modify
     */
    public void modifyText(ModifyEvent event) {
        validate();
    }

    /**
     * Validate the content
     *
     * @return boolean
     */
    private boolean validate() {
        setErrorMessage(null);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        String sup = superCombo.getText();
        String col = columnCombo.getText();
        String alias = aliasText.getText();
        if ("".equals(sup)) { // $NON-NLS-1$
            setErrorMessage(Messages.errNoSelectedSuperClass);
            return false;
        }
        if ("".equals(col)) { // $NON-NLS-1$
            setErrorMessage(Messages.errNoSelectedColumn);
            return false;
        }
        if ("".equals(alias)) {
            for (String[] str : currentConflicts) {
                if (str[0].equals(col) && str[2].equals(tableName)) {
                    String msg = Messages.bind(Messages.errExistLocColumn, col);
                    setErrorMessage(msg);
                    aliasText.setFocus();
                    return false;
                }
            }
        }
        List<DBResolution> resolutions = null;
        if (isClassResolution) {
            resolutions = schema.getClassResolutions();
        } else {
            resolutions = schema.getResolutions();
        }
        for (DBResolution r : resolutions) {
            if (r.getName().equals(col)
                    && r.getTableName().equals(sup)
                    && r.getAlias().equals(alias)) {
                setErrorMessage(Messages.errExistResolution);
                return false;
            }
        }
        getButton(IDialogConstants.OK_ID).setEnabled(true);
        return true;
    }

    public DBResolution getResolution() {
        return resolution;
    }

    public void setResolution(DBResolution resolution) {
        this.resolution = resolution;
    }

    public boolean isClassResolution() {
        return isClassResolution;
    }
}
