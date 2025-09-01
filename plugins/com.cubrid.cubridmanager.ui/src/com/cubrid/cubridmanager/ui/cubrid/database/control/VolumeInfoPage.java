/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.TableViewerSorter;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.EnvInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * Database volume information for creating database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class VolumeInfoPage extends WizardPage implements ModifyListener, IPageChangedListener {

    public static final String PAGENAME = "CreateDatabaseWizard/VolumeInfoPage";

    public static final String[] VOLUME_TYPES = new String[] {"data", "index", "temp", "generic"};
    public static final String PROP_NAME = "name";
    public static final String PROP_TYPE = "type";
    public static final String PROP_SIZE = "size";
    public static final String PROP_PATH = "path";
    public static final String PROP_IS_DEFAULT = "isDefault";
    public static final String[] PROPS = {PROP_NAME, PROP_TYPE, PROP_SIZE, PROP_PATH};

    private Text volumeNameText;
    private Text volumePathText;
    private Combo volumeTypeCombo;
    private Button addVolumeButton;
    private Table volumeTable;
    private TableViewer volumeTableViewer;
    private Button deleteVolumeButton;
    private final CubridServer server;
    private String databasePath = "";
    private String databaseName = "";
    private final List<Map<String, String>> volumeTableList = new ArrayList<Map<String, String>>();
    private Text volumeSizeText;
    private String pageSize = null;
    private boolean isAddedDefaultVolume = false;

    public VolumeInfoPage(CubridServer server) {
        super(PAGENAME);
        this.server = server;
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        createVolumeGroup(composite);
        createTable(composite);
        initial();

        setTitle(Messages.titleWizardPageAdditional);
        setMessage(Messages.msgWizardPageAdditional);
        setControl(composite);
    }

    /**
     * Create volume group area
     *
     * @param parent the parent composite
     */
    private void createVolumeGroup(Composite parent) {
        Group volumeGroup = new Group(parent, SWT.NONE);
        volumeGroup.setText(Messages.grpAddtionalVolInfo);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        volumeGroup.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        volumeGroup.setLayout(layout);

        Label volumeNameLabel = new Label(volumeGroup, SWT.LEFT | SWT.WRAP);
        volumeNameLabel.setText(Messages.lblVolName);
        volumeNameLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        volumeNameText = new Text(volumeGroup, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        volumeNameText.setLayoutData(gridData);
        volumeNameText.setEditable(false);

        Label volumePathLabel = new Label(volumeGroup, SWT.LEFT | SWT.WRAP);
        volumePathLabel.setText(Messages.lblVolPath);
        volumePathLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        volumePathText = new Text(volumeGroup, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        volumePathText.setLayoutData(gridData);

        Button selectDirectoryButton = new Button(volumeGroup, SWT.NONE);
        selectDirectoryButton.setText(Messages.btnBrowse);
        selectDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
        selectDirectoryButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        DirectoryDialog dlg = new DirectoryDialog(getShell());
                        if (databasePath != null && databasePath.trim().length() > 0) {
                            dlg.setFilterPath(databasePath);
                        }
                        dlg.setText(Messages.msgSelectDir);
                        dlg.setMessage(Messages.msgSelectDir);
                        String dir = dlg.open();
                        if (dir != null) {
                            volumePathText.setText(dir);
                        }
                    }
                });
        ServerInfo serverInfo = server.getServerInfo();
        if (serverInfo != null && !serverInfo.isLocalServer()) {
            selectDirectoryButton.setEnabled(false);
        }

        Label volumeTypeLabel = new Label(volumeGroup, SWT.LEFT | SWT.WRAP);
        volumeTypeLabel.setText(Messages.lblVolType);
        volumeTypeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        volumeTypeCombo = new Combo(volumeGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        volumeTypeCombo.setLayoutData(gridData);
        volumeTypeCombo.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        changeVolumeName();
                    }
                });

        Label sizeOfPageLabel = new Label(volumeGroup, SWT.LEFT | SWT.WRAP);
        sizeOfPageLabel.setText(Messages.lblVolSize);
        sizeOfPageLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        volumeSizeText = new Text(volumeGroup, SWT.BORDER);
        volumeSizeText.setTextLimit(20);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        volumeSizeText.setLayoutData(gridData);
        volumeSizeText.addFocusListener(
                new FocusListener() {
                    public void focusGained(FocusEvent event) {
                        volumeSizeText.addModifyListener(VolumeInfoPage.this);
                    }

                    public void focusLost(FocusEvent event) {
                        volumeSizeText.removeModifyListener(VolumeInfoPage.this);
                    }
                });
    }

    public void changeAutoVolumeButton() {
        boolean isHasDataVolume = false;
        boolean isHasIndexVolume = false;
        if (volumeTableList != null) {
            for (int i = 0; i < volumeTableList.size(); i++) {
                Map<String, String> map = volumeTableList.get(i);
                String type = map.get("1");
                if ("data".equals(type)) {
                    isHasDataVolume = true;
                }
                if ("index".equals(type)) {
                    isHasIndexVolume = true;
                }
            }
        }

        SetAutoAddVolumeInfoPage setAutoAddVolumeInfoPage =
                (SetAutoAddVolumeInfoPage) getWizard().getPage(SetAutoAddVolumeInfoPage.PAGENAME);
        if (setAutoAddVolumeInfoPage != null) {
            setAutoAddVolumeInfoPage.setUsingAutoDataVolume(isHasDataVolume);
            setAutoAddVolumeInfoPage.setUsingAutoIndexVolume(isHasIndexVolume);
        }
    }

    public void changeVolumeTable() {
        if (volumeTableViewer != null) {
            volumeTableList.clear();
            volumeTableViewer.refresh();
        }
        this.isAddedDefaultVolume = false;
    }

    /**
     * Create volume table area
     *
     * @param parent the parent composite
     */
    private void createTable(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);

        Label tipLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        tipLabel.setText(Messages.msgVolumeList);
        tipLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        tipLabel.setLayoutData(gridData);

        addVolumeButton = new Button(composite, SWT.NONE);
        addVolumeButton.setText(Messages.btnAddVolume);
        addVolumeButton.setEnabled(false);
        addVolumeButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        String volumeName = volumeNameText.getText();
                        String volumeType = volumeTypeCombo.getText();
                        String volumeSize = volumeSizeText.getText();
                        String pageNumber =
                                String.valueOf(
                                        CreateDatabaseWizard.calcVolumePageNum(
                                                volumeSize, pageSize));
                        String volumePath = volumePathText.getText();
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("0", volumeName);
                        map.put("1", volumeType);
                        map.put("2", volumeSize);
                        map.put("3", pageNumber);
                        map.put("4", volumePath);
                        volumeTableList.add(map);
                        for (int i = 0; i < volumeTable.getColumnCount(); i++) {
                            volumeTable.getColumn(i).pack();
                        }
                        volumeTableViewer.refresh();
                        changeVolumeName();
                        changeAutoVolumeButton();
                    }
                });

        deleteVolumeButton = new Button(composite, SWT.NONE);
        deleteVolumeButton.setText(Messages.btnDelVolume);
        deleteVolumeButton.setEnabled(false);
        deleteVolumeButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        StructuredSelection selection =
                                (StructuredSelection) volumeTableViewer.getSelection();
                        if (selection != null && !selection.isEmpty()) {
                            volumeTableList.removeAll(selection.toList());
                        }
                        volumeTableViewer.refresh();
                        deleteVolumeButton.setEnabled(volumeTable.getSelectionCount() > 0);
                        changeAutoVolumeButton();
                        changeVolumeName();
                    }
                });

        final String[] columnNameArr =
                new String[] {
                    Messages.tblColumnVolName, Messages.tblColumnVolType,
                    Messages.tblColumnVolSize, Messages.tblColumnVolPath
                };
        volumeTableViewer =
                CommonUITool.createCommonTableViewer(
                        parent,
                        new TableViewerSorter(),
                        columnNameArr,
                        CommonUITool.createGridData(GridData.FILL_BOTH, 4, 1, -1, -1));
        volumeTableViewer.setLabelProvider(new VolumeInfoTableProvider());
        volumeTableViewer.setInput(volumeTableList);
        volumeTable = volumeTableViewer.getTable();
        for (int i = 0; i < volumeTable.getColumnCount(); i++) {
            volumeTable.getColumn(i).pack();
        }

        volumeTable.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        deleteVolumeButton.setEnabled(volumeTable.getSelectionCount() > 0);
                    }
                });

        // Create the cell editors
        CellEditor[] editors = new CellEditor[4];
        editors[0] = new TextCellEditor(volumeTable);
        editors[1] = new ComboBoxCellEditor(volumeTable, VOLUME_TYPES, SWT.READ_ONLY);
        editors[2] = new TextCellEditor(volumeTable);
        editors[3] = null;

        editors[0].setValidator(new VolumeNameValidator(volumeTableViewer));
        editors[0].addListener(
                new ICellEditorListener() {
                    public void editorValueChanged(boolean oldValidState, boolean newValidState) {
                        if (!newValidState) {
                            VolumeInfoPage.this.setErrorMessage(Messages.errVolumeName);
                        } else {
                            VolumeInfoPage.this.setErrorMessage(null);
                        }
                    }

                    public void cancelEditor() {
                        VolumeInfoPage.this.setErrorMessage(null);
                    }

                    public void applyEditorValue() {
                        VolumeInfoPage.this.setErrorMessage(null);
                    }
                });

        editors[2].setValidator(new VolumeSizeValidator());
        editors[2].addListener(
                new ICellEditorListener() {
                    public void editorValueChanged(boolean oldValidState, boolean newValidState) {
                        if (!newValidState) {
                            VolumeInfoPage.this.setErrorMessage(Messages.errVolumeSize);
                        } else {
                            VolumeInfoPage.this.setErrorMessage(null);
                        }
                    }

                    public void cancelEditor() {
                        VolumeInfoPage.this.setErrorMessage(null);
                    }

                    public void applyEditorValue() {
                        VolumeInfoPage.this.setErrorMessage(null);
                    }
                });

        volumeTableViewer.setCellEditors(editors);
        volumeTableViewer.setColumnProperties(PROPS);
        volumeTableViewer.setCellModifier(new VolumnCellEditor(this, volumeTableViewer));
    }

    public void modifyText(ModifyEvent event) {

        String volumePath = volumePathText.getText();
        boolean isValidVolumePath = ValidateUtil.isValidPathName(volumePath);
        if (!isValidVolumePath) {
            setErrorMessage(Messages.errVolumePath);
            addVolumeButton.setEnabled(false);
            return;
        }
        String volumeSize = volumeSizeText.getText();
        boolean isValidVolmueSize =
                (ValidateUtil.isNumber(volumeSize) || ValidateUtil.isPositiveDouble(volumeSize))
                        && Double.parseDouble(volumeSize) > 0;
        if (!isValidVolmueSize) {
            setErrorMessage(Messages.errVolumeSize);
            addVolumeButton.setEnabled(false);
            return;
        }

        setErrorMessage(null);
        addVolumeButton.setEnabled(true);
    }

    public void pageChanged(PageChangedEvent event) {
        IWizardPage page = (IWizardPage) event.getSelectedPage();
        if (page.getName().equals(PAGENAME)) {
            GeneralInfoPage generalInfoPage =
                    (GeneralInfoPage) getWizard().getPage(GeneralInfoPage.PAGENAME);
            pageSize = generalInfoPage.getPageSize();
            String volumeSize = volumeSizeText.getText();
            if (volumeSize.trim().equals("")) {
                volumeSizeText.setText(generalInfoPage.getDefaultDatVolumeSize());
            }
            if (!isAddedDefaultVolume) {
                addDefaultVolume(generalInfoPage);
                isAddedDefaultVolume = true;
            }
            changeVolumeName();

            /*Update Page number*/
            for (Map<String, String> map : volumeTableList) {
                String totalSize = map.get("2");
                long pageNum = CreateDatabaseWizard.calcVolumePageNum(totalSize, pageSize);
                map.put("3", String.valueOf(pageNum));
            }
        }
    }

    private void addDefaultVolume(GeneralInfoPage generalInfoPage) {

        String volumeSize = generalInfoPage.getDefaultDatVolumeSize();
        String pageNumber =
                String.valueOf(CreateDatabaseWizard.calcVolumePageNum(volumeSize, pageSize));
        String volumePath = generalInfoPage.getGenericVolumePath();
        String[] volumeTypes = {"data", "index", "temp"};
        for (String volumeType : volumeTypes) {
            String volumeName = databaseName + "_" + volumeType + "_x001";
            Map<String, String> map = new HashMap<String, String>();
            map.put("0", volumeName);
            map.put("1", volumeType);
            map.put("2", volumeSize);
            map.put("3", pageNumber);
            map.put("4", volumePath);
            map.put("5", "true"); // whether is default volume
            volumeTableList.add(map);
        }
        for (int i = 0; i < volumeTable.getColumnCount(); i++) {
            volumeTable.getColumn(i).pack();
        }
        volumeTableViewer.refresh();
    }

    private void initial() {
        volumeTypeCombo.setItems(VOLUME_TYPES);
        volumeTypeCombo.select(0);
        EnvInfo envInfo = server.getServerInfo().getEnvInfo();
        if (envInfo != null) {
            databasePath = envInfo.getDatabaseDir();
            ServerInfo serverInfo = server.getServerInfo();
            if (serverInfo != null) {
                databasePath =
                        FileUtil.changeSeparatorByOS(databasePath, serverInfo.getServerOsInfo());
            }
        }
        volumeNameText.addModifyListener(this);
        volumePathText.addModifyListener(this);
    }

    public void changeVolumePath() {
        GeneralInfoPage generalInfoPage =
                (GeneralInfoPage) getWizard().getPage(GeneralInfoPage.PAGENAME);
        databaseName = generalInfoPage.getDatabaseName();
        volumePathText.setText(
                databasePath + server.getServerInfo().getPathSeparator() + databaseName);
    }

    private void changeVolumeName() {
        String type = volumeTypeCombo.getText();
        int count = 1;
        TableItem[] items = volumeTable.getItems();
        while (true) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(3);
            String volumeName = databaseName + "_" + type + "_x" + nf.format(count);
            boolean isExist = false;
            for (int i = 0; i < items.length; i++) {
                String str = items[i].getText(0);
                if (str.trim().equals(volumeName)) {
                    isExist = true;
                }
            }
            if (!isExist) {
                volumeNameText.setText(volumeName);
                break;
            }
            count++;
        }
    }

    public String getPageSize() {
        return pageSize;
    }

    public List<Map<String, String>> getVolumeList() {
        return volumeTableList;
    }
}

/**
 * The Volumn Cell Editor
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-29 created by Kevin.Wang
 */
class VolumnCellEditor implements ICellModifier {

    private TableViewer volumeTableViewer;
    private VolumeInfoPage volumeInfoPage;

    public VolumnCellEditor(VolumeInfoPage volumeInfoPage, TableViewer volumeTableViewer) {
        this.volumeInfoPage = volumeInfoPage;
        this.volumeTableViewer = volumeTableViewer;
    }

    public boolean canModify(Object element, String property) {
        return true;
    }

    public Object getValue(Object element, String property) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) element;
        if (VolumeInfoPage.PROP_NAME.equals(property)) {
            return map.get("0");
        }

        if (VolumeInfoPage.PROP_TYPE.equals(property)) {
            String str = map.get("1");
            int index = 0;
            if (str != null) {
                for (int i = 0; i < VolumeInfoPage.VOLUME_TYPES.length; i++) {
                    if (str.equals(VolumeInfoPage.VOLUME_TYPES[i])) {
                        index = i;
                        break;
                    }
                }
            }
            return Integer.valueOf(index);
        }

        if (VolumeInfoPage.PROP_SIZE.equals(property)) {
            return map.get("2");
        }

        if (VolumeInfoPage.PROP_PATH.equals(property)) {
            return map.get("4");
        }
        return null;
    }

    public void modify(Object element, String property, Object value) {
        if (element instanceof Item) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) ((Item) element).getData();

            String strValue = "";
            if (value != null) {
                strValue = value.toString();
            }
            if (VolumeInfoPage.PROP_NAME.equals(property) && strValue.length() > 0) {
                map.put("0", value.toString());
            }

            if (VolumeInfoPage.PROP_TYPE.equals(property)) {
                map.put("1", VolumeInfoPage.VOLUME_TYPES[StringUtil.intValue(value.toString(), 0)]);
                volumeInfoPage.changeAutoVolumeButton();
            }

            if (VolumeInfoPage.PROP_SIZE.equals(property) && strValue.length() > 0) {
                String pageNumber =
                        String.valueOf(
                                CreateDatabaseWizard.calcVolumePageNum(
                                        value.toString(), volumeInfoPage.getPageSize()));
                map.put("2", value.toString());
                map.put("3", pageNumber);
            }

            if (VolumeInfoPage.PROP_PATH.equals(property) && strValue.length() > 0) {
                map.put("4", value.toString());
            }
            volumeTableViewer.refresh();
        }
    }
}

/**
 * The Volume Name Validator
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-29 created by Kevin.Wang
 */
class VolumeNameValidator implements ICellEditorValidator {
    private TableViewer volumeTableViewer;

    public VolumeNameValidator(TableViewer volumeTableViewer) {
        this.volumeTableViewer = volumeTableViewer;
    }

    @SuppressWarnings("unchecked")
    public String isValid(Object value) {
        String str = (String) value;
        if (StringUtil.isEmpty(str)) {
            return "error";
        }

        List<Map<String, String>> volumeTableList =
                (List<Map<String, String>>) volumeTableViewer.getInput();
        TableItem[] items = volumeTableViewer.getTable().getSelection();
        if (items.length > 0) {
            Map<String, String> selected = (Map<String, String>) items[0].getData();
            String strValue = (String) value;

            for (Map<String, String> map : volumeTableList) {
                if (selected != map && StringUtil.isEqualNotIgnoreNull(map.get("0"), strValue)) {
                    return "error";
                }
            }
        }
        return null;
    }
}

/**
 * The Volume Size Validator
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-8-29 created by Kevin.Wang
 */
class VolumeSizeValidator implements ICellEditorValidator {

    public String isValid(Object value) {
        if (StringUtil.intValue(value.toString(), 0) <= 0) {
            return "error";
        }
        return null;
    }
}
