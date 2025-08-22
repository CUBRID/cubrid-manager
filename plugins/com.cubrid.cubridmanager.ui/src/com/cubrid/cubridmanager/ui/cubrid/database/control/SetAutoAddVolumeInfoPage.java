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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.GetAutoAddVolumeInfo;
import com.cubrid.cubridmanager.ui.cubrid.database.Messages;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Set auto adding volume information page for creating database
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class SetAutoAddVolumeInfoPage extends WizardPage
        implements IPageChangedListener, ModifyListener {

    public static final String PAGENAME = "CreateDatabaseWizard/SetAutoAddVolumeInfoPage";

    private Button dataUsingAutoVolButton;
    private Button indexUsingAutoVolButton;
    private Combo dataOutRateCombo;
    private Combo indexOutRateCombo;
    private Text dataVolumeText;
    private Text indexVolumeText;
    List<Map<String, String>> volumeList = null;

    private double initVolumeSize = 512;
    private static final int DEFAULT_RATE = 15;
    private static final int RATE_MIN = 5;
    private static final int RATE_MAX = 30;

    private boolean isSelectedUsingAutoDataVolume = false;
    private boolean isSelectedUsingAutoIndexVolume = false;

    /** The constructor */
    public SetAutoAddVolumeInfoPage(CubridServer server) {
        super(PAGENAME);

        String genericVolumeSize =
                CompatibleUtil.getConfigGenericVolumeSize(server.getServerInfo(), null);
        if (!StringUtil.isEmpty(genericVolumeSize)) {
            Long bytes = StringUtil.getByteNumber(genericVolumeSize);
            if (bytes > -1) {
                initVolumeSize = StringUtil.convertToM(bytes);
            }
        }
    }

    /**
     * Creates the controls for this page
     *
     * @param parent the parent composite
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        createDataParaGroup(composite);
        createIndexParaGroup(composite);
        init();
        setTitle(Messages.titleWizardPageAuto);
        setMessage(Messages.msgWizardPageAuto);
        setControl(composite);
    }

    /**
     * Creates dataParaGroup which is the part of Dialog area
     *
     * @param composite the parent composite
     */
    private void createDataParaGroup(Composite composite) {
        final Group dataParaGroup = new Group(composite, SWT.RESIZE);
        final GridData gdDataParaGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dataParaGroup.setLayoutData(gdDataParaGroup);
        dataParaGroup.setText(Messages.grpVolPurposeData);
        GridLayout groupLayout = new GridLayout(4, false);
        dataParaGroup.setLayout(groupLayout);

        dataUsingAutoVolButton = new Button(dataParaGroup, SWT.CHECK);
        final GridData gdUsingAutoVolButton = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
        dataUsingAutoVolButton.setLayoutData(gdUsingAutoVolButton);
        dataUsingAutoVolButton.setText(Messages.btnUsingAuto);

        final Label outOfSpaceLabel = new Label(dataParaGroup, SWT.NONE);
        outOfSpaceLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        outOfSpaceLabel.setText(Messages.lblOutOfSpaceWarning);

        dataOutRateCombo = new Combo(dataParaGroup, SWT.BORDER);
        dataOutRateCombo.setTextLimit(2);
        dataOutRateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label volumeLabel = new Label(dataParaGroup, SWT.NONE);
        volumeLabel.setText(Messages.lblVolSize);
        volumeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        dataVolumeText = new Text(dataParaGroup, SWT.BORDER);
        dataVolumeText.setTextLimit(20);
        dataVolumeText.setText(GeneralInfoPage.getIntSizeString(initVolumeSize, 1));
        final GridData gdDataVolumeText = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        dataVolumeText.setLayoutData(gdDataVolumeText);
        dataVolumeText.addFocusListener(
                new FocusListener() {
                    public void focusGained(FocusEvent event) {
                        dataVolumeText.addModifyListener(SetAutoAddVolumeInfoPage.this);
                    }

                    public void focusLost(FocusEvent event) {
                        dataVolumeText.removeModifyListener(SetAutoAddVolumeInfoPage.this);
                    }
                });
    }

    /**
     * Creates indexParaGroup which is the part of Dialog area
     *
     * @param composite the parent composite
     */
    private void createIndexParaGroup(Composite composite) {
        final Group indexParaGroup = new Group(composite, SWT.RESIZE);
        final GridData gdIndexParaGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        final GridLayout gridLayout = new GridLayout(4, false);
        indexParaGroup.setLayout(gridLayout);
        indexParaGroup.setLayoutData(gdIndexParaGroup);
        indexParaGroup.setText(Messages.grpVolPurposeIndex);

        indexUsingAutoVolButton = new Button(indexParaGroup, SWT.CHECK);
        indexUsingAutoVolButton.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
        indexUsingAutoVolButton.setText(Messages.btnUsingAuto);

        final Label outOfSpaceLabel = new Label(indexParaGroup, SWT.NONE);
        outOfSpaceLabel.setText(Messages.lblOutOfSpaceWarning);
        outOfSpaceLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        indexOutRateCombo = new Combo(indexParaGroup, SWT.BORDER);
        indexOutRateCombo.setTextLimit(2);
        indexOutRateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label volumeLabel = new Label(indexParaGroup, SWT.NONE);
        volumeLabel.setText(Messages.lblVolSize);
        volumeLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        indexVolumeText = new Text(indexParaGroup, SWT.BORDER);
        indexVolumeText.setTextLimit(20);
        indexVolumeText.setText(GeneralInfoPage.getIntSizeString(initVolumeSize, 1));
        final GridData gdDataVolumeText = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        indexVolumeText.setLayoutData(gdDataVolumeText);
        indexVolumeText.addFocusListener(
                new FocusListener() {
                    public void focusGained(FocusEvent event) {
                        indexVolumeText.addModifyListener(SetAutoAddVolumeInfoPage.this);
                    }

                    public void focusLost(FocusEvent event) {
                        indexVolumeText.removeModifyListener(SetAutoAddVolumeInfoPage.this);
                    }
                });
    }

    /** Initials some values */
    private void init() {
        for (int i = RATE_MIN; i <= RATE_MAX; i++) {
            dataOutRateCombo.add(String.valueOf(i));
            dataOutRateCombo.setText(String.valueOf(DEFAULT_RATE));
            indexOutRateCombo.add(String.valueOf(i));
            indexOutRateCombo.setText(String.valueOf(DEFAULT_RATE));
        }
        dataUsingAutoVolButton.setSelection(true);
        indexUsingAutoVolButton.setSelection(true);
        dataOutRateCombo.setEnabled(true);
        dataVolumeText.setEnabled(true);
        indexOutRateCombo.setEnabled(true);
        indexVolumeText.setEnabled(true);
        dataOutRateCombo.addModifyListener(this);
        indexOutRateCombo.addModifyListener(this);
        dataUsingAutoVolButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        isSelectedUsingAutoDataVolume = true;
                        changeButtonStatus(false);
                    }
                });
        indexUsingAutoVolButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        isSelectedUsingAutoIndexVolume = true;
                        changeButtonStatus(false);
                    }
                });
    }

    /**
     * Call this method when modify text
     *
     * @param event the modify event
     */
    public void modifyText(ModifyEvent event) {
        valid();
    }

    /** Check the value valid */
    private void valid() {
        String dataOutRate = dataOutRateCombo.getText();
        String dataVolumeSize = dataVolumeText.getText();
        String indexOutRate = indexOutRateCombo.getText();
        String indexVolumeSize = indexVolumeText.getText();
        boolean isValidDataOutRate = true;
        boolean isValidDataVolumeSize = true;
        boolean isValidDataPageNum = true;
        boolean isValidIndexOutRate = true;
        boolean isValidIndexVolumeSize = true;
        boolean isValidIndexPageNum = true;
        if (dataUsingAutoVolButton.getSelection()) {
            isValidDataOutRate = ValidateUtil.isNumber(dataOutRate);
            if (isValidDataOutRate) {
                isValidDataOutRate =
                        Integer.parseInt(dataOutRate) >= 5 && Integer.parseInt(dataOutRate) <= 30;
            }
            isValidDataVolumeSize =
                    ValidateUtil.isNumber(dataVolumeSize)
                            || ValidateUtil.isPositiveDouble(dataVolumeSize);
            if (isValidDataVolumeSize) {
                isValidDataVolumeSize = Double.parseDouble(dataVolumeSize) > 0;
            }
        }
        if (indexUsingAutoVolButton.getSelection()) {
            isValidIndexOutRate = ValidateUtil.isNumber(indexOutRate);
            if (isValidIndexOutRate) {
                isValidIndexOutRate =
                        Integer.parseInt(indexOutRate) >= 5 && Integer.parseInt(indexOutRate) <= 30;
            }
            isValidIndexVolumeSize =
                    ValidateUtil.isNumber(indexVolumeSize)
                            || ValidateUtil.isPositiveDouble(dataVolumeSize);
            if (isValidIndexVolumeSize) {
                isValidIndexVolumeSize = Double.parseDouble(indexVolumeSize) > 0;
            }
        }
        if (!isValidDataOutRate) {
            setErrorMessage(Messages.errDataOutOfSpace);
            setPageComplete(false);
            return;
        }
        if (!isValidDataVolumeSize) {
            setErrorMessage(Messages.errDataVolumeSize);
            setPageComplete(false);
            return;
        }
        if (!isValidDataPageNum) {
            setErrorMessage(Messages.errDataVolumePageNum);
            setPageComplete(false);
            return;
        }
        if (!isValidIndexOutRate) {
            setErrorMessage(Messages.errIndexOutOfSpace);
            setPageComplete(false);
            return;
        }
        if (!isValidIndexVolumeSize) {
            setErrorMessage(Messages.errIndexVolumeSize);
            setPageComplete(false);
            return;
        }
        if (!isValidIndexPageNum) {
            setErrorMessage(Messages.errIndexVolumePageNum);
            setPageComplete(false);
            return;
        }

        setErrorMessage(null);
        setPageComplete(true);
    }

    /**
     * Call this method when page changed
     *
     * @param event the page changed event
     */
    public void pageChanged(PageChangedEvent event) {
        IWizardPage page = (IWizardPage) event.getSelectedPage();
        if (page.getName().equals(PAGENAME)) {

            VolumeInfoPage volumeInfoPage =
                    (VolumeInfoPage) getWizard().getPage(VolumeInfoPage.PAGENAME);
            volumeList = volumeInfoPage.getVolumeList();
            changeButtonStatus(true);
        }
    }

    /**
     * Change button status
     *
     * @param isTestSelection isTestSelection
     */
    public void changeButtonStatus(boolean isTestSelection) {
        boolean isHasDataVolume = false;
        boolean isHasIndexVolume = false;
        if (volumeList != null) {
            for (int i = 0; i < volumeList.size(); i++) {
                Map<String, String> map = volumeList.get(i);
                String type = map.get("1");
                if ("data".equals(type)) {
                    isHasDataVolume = true;
                }
                if ("index".equals(type)) {
                    isHasIndexVolume = true;
                }
            }
        }
        dataUsingAutoVolButton.setEnabled(isHasDataVolume);
        if (!isHasDataVolume && isTestSelection) {
            dataUsingAutoVolButton.setSelection(false);
        }
        if (isHasDataVolume && dataUsingAutoVolButton.getSelection()) {
            dataOutRateCombo.setEnabled(true);
            dataVolumeText.setEnabled(true);
        } else if (!isHasDataVolume || !dataUsingAutoVolButton.getSelection()) {
            dataOutRateCombo.setEnabled(false);
            dataVolumeText.setEnabled(false);
        }

        indexUsingAutoVolButton.setEnabled(isHasIndexVolume);
        if (!isHasIndexVolume && isTestSelection) {
            indexUsingAutoVolButton.setSelection(false);
        }
        if (isHasIndexVolume && indexUsingAutoVolButton.getSelection()) {
            indexOutRateCombo.setEnabled(true);
            indexVolumeText.setEnabled(true);
        } else if (!isHasIndexVolume || !indexUsingAutoVolButton.getSelection()) {
            indexOutRateCombo.setEnabled(false);
            indexVolumeText.setEnabled(false);
        }
        if (isHasDataVolume || isHasIndexVolume) {
            setMessage(Messages.msgWizardPageAuto);
        } else {
            setMessage(Messages.errNoIndexAndDataVolume);
        }
        valid();
    }

    /**
     * Get auto adding volume information
     *
     * @return the GetAutoAddVolumeInfo object
     */
    public GetAutoAddVolumeInfo getAutoAddVolumeInfo() {
        if (!dataUsingAutoVolButton.getSelection() && !indexUsingAutoVolButton.getSelection()) {
            return null;
        }
        GetAutoAddVolumeInfo autoAddVolumeInfo = new GetAutoAddVolumeInfo();
        if (dataUsingAutoVolButton.getSelection()) {
            autoAddVolumeInfo.setData(OnOffType.ON.getText());
            String pageNum = String.valueOf(calcPageNum(dataVolumeText.getText()));
            double rate = Double.parseDouble(dataOutRateCombo.getText()) / 100;
            autoAddVolumeInfo.setData_ext_page(pageNum);
            autoAddVolumeInfo.setData_warn_outofspace(String.valueOf(rate));
        } else {
            autoAddVolumeInfo.setData(OnOffType.OFF.getText());
            autoAddVolumeInfo.setData_ext_page("0.0");
            autoAddVolumeInfo.setData_warn_outofspace("0.0");
        }
        if (indexUsingAutoVolButton.getSelection()) {
            autoAddVolumeInfo.setIndex(OnOffType.ON.getText());
            String pageNum = String.valueOf(calcPageNum(indexVolumeText.getText()));
            double rate = Double.parseDouble(indexOutRateCombo.getText()) / 100;
            autoAddVolumeInfo.setIndex_ext_page(pageNum);
            autoAddVolumeInfo.setIndex_warn_outofspace(String.valueOf(rate));
        } else {
            autoAddVolumeInfo.setIndex(OnOffType.OFF.getText());
            autoAddVolumeInfo.setIndex_ext_page("0.0");
            autoAddVolumeInfo.setIndex_warn_outofspace("0.0");
        }
        return autoAddVolumeInfo;
    }

    /**
     * Calculate volume size,if the pageNum is valid,return the page number,otherwise return -1;
     *
     * @return pageNumber - If the pageNum is valid,return the page number,otherwise return -1;
     */
    private long calcPageNum(String strVolumeSize) {
        boolean isValidGenericVolumeSize =
                ValidateUtil.isPositiveDouble(strVolumeSize)
                        || ValidateUtil.isNumber(strVolumeSize);

        GeneralInfoPage generalInfoPage =
                (GeneralInfoPage) getWizard().getPage(GeneralInfoPage.PAGENAME);
        String pageSizeStr = generalInfoPage.getPageSize();

        if (pageSizeStr != null && pageSizeStr.trim().length() > 0 && isValidGenericVolumeSize) {
            int pageSize = Integer.parseInt(pageSizeStr);
            double volumeSize = Double.parseDouble(strVolumeSize);
            double pageNumber = (1024 * 1024 / (double) pageSize) * volumeSize;

            return Math.round(pageNumber);
        }

        return -1;
    }

    /**
     * Set data volume size
     *
     * @param size the size string
     */
    public void setDataVolumeSize(String size) {
        if (dataVolumeText != null && !dataVolumeText.isDisposed()) {
            dataVolumeText.setText(size);
        }
    }

    /**
     * Set index volume size
     *
     * @param size the size
     */
    public void setIndexVolumeSize(String size) {
        if (indexVolumeText != null && !indexVolumeText.isDisposed()) {
            indexVolumeText.setText(size);
        }
    }

    /**
     * Set using auto data volume
     *
     * @param isUsingAutoDataVolume whether using auto data volume
     */
    public void setUsingAutoDataVolume(boolean isUsingAutoDataVolume) {
        if (dataUsingAutoVolButton != null
                && !dataUsingAutoVolButton.isDisposed()
                && !isSelectedUsingAutoDataVolume) {
            dataUsingAutoVolButton.setSelection(isUsingAutoDataVolume);
        }
    }

    /**
     * Set using auto index volume
     *
     * @param isUsingAutoIndexVolume whether using auot index volume
     */
    public void setUsingAutoIndexVolume(boolean isUsingAutoIndexVolume) {
        if (indexUsingAutoVolButton != null
                && !indexUsingAutoVolButton.isDisposed()
                && !isSelectedUsingAutoIndexVolume) {
            indexUsingAutoVolButton.setSelection(isUsingAutoIndexVolume);
        }
    }
}
