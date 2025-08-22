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
package com.cubrid.cubridmanager.ui.cubrid.dbspace.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.common.ui.spi.util.ValidateUtil;
import com.cubrid.cubridmanager.core.common.model.OnOffType;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.GetAutoAddVolumeInfo;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.Messages;
import java.math.BigDecimal;
import java.text.NumberFormat;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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
 * A dialog that show up when a user click the Database space context menu.
 *
 * @author lizhiqiang 2009-3-16
 */
public class SetAutoAddVolumeDialog extends CMTitleAreaDialog {

    private Button dataUsingAutoVolButton;
    private Button indexUsingAutoVolButton;
    private Combo dataOutRateCombo;
    private Combo indexOutRateCombo;
    private Text indexExtPageText;
    private Text dataExtPageText;

    private GetAutoAddVolumeInfo getAutoAddVolumeInfo;
    private Text dataVolumeText;
    private Text indexVolumeText;
    private BigDecimal pageSize;

    private static final int DEFAULT_RATE = 15;
    private static final int RATEMIN = 5;
    private static final int RATEMAX = 30;

    private static final BigDecimal MEGABYTES = new BigDecimal(1024 * 1024);
    private static final int INIT_VOLUME = 2048;
    private boolean[] isOkenable;

    private static BigDecimal initDataVol;
    private static BigDecimal initIndexVol;

    private VolumeModifyListener dataVolumeModifyListener;
    private VolumeModifyListener indexVolumeModifyListener;
    private PageModifyListener dataPageModifyListener;
    private PageModifyListener indexPageModifyListener;

    private String initDataExt;
    private String initIndexExt;

    /**
     * The constructor
     *
     * @param parentShell
     */
    public SetAutoAddVolumeDialog(Shell parentShell) {
        super(parentShell);
        isOkenable = new boolean[6];
        for (int i = 0; i < isOkenable.length; i++) {
            isOkenable[i] = true;
        }
    }

    /**
     * Create the dialog area
     *
     * @param parent the parent composite
     * @return the composite
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);
        setTitle(Messages.setDialogTitle);
        setMessage(Messages.setDialogMsg);
        final Composite composite = new Composite(parentComp, SWT.RESIZE);
        final GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdComposite.widthHint = 500;
        composite.setLayoutData(gdComposite);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        gridLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        gridLayout.horizontalSpacing =
                convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(gridLayout);

        createDataParaGroup(composite);
        createIndexParaGroup(composite);
        init();
        return parentComp;
    }

    /**
     * Creates dataParaGroup which is the part of Dialog area
     *
     * @param composite Composite
     */
    private void createDataParaGroup(Composite composite) {
        final Group dataParaGroup = new Group(composite, SWT.RESIZE);
        final GridData gdDataParaGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dataParaGroup.setLayoutData(gdDataParaGroup);
        dataParaGroup.setText(Messages.dataGroupTitle);
        GridLayout groupLayout = new GridLayout(4, false);
        dataParaGroup.setLayout(groupLayout);

        dataUsingAutoVolButton = new Button(dataParaGroup, SWT.CHECK);
        final GridData gdUsingAutoVolButton = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
        dataUsingAutoVolButton.setLayoutData(gdUsingAutoVolButton);
        dataUsingAutoVolButton.setText(Messages.dataUseAutoVolBtnText);

        final Label outOfSpaceLabel = new Label(dataParaGroup, SWT.NONE);
        outOfSpaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        outOfSpaceLabel.setText(Messages.dataOutOfSpaceRateLbl);

        dataOutRateCombo = new Combo(dataParaGroup, SWT.BORDER | SWT.RIGHT);
        dataOutRateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label volumeLabel = new Label(dataParaGroup, SWT.NONE);
        volumeLabel.setText(Messages.datavolumeLbl);
        volumeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        dataVolumeText = new Text(dataParaGroup, SWT.BORDER | SWT.RIGHT);
        final GridData gdDataVolumeText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        dataVolumeText.setLayoutData(gdDataVolumeText);

        final Label extPageLabel = new Label(dataParaGroup, SWT.NONE);
        extPageLabel.setText(Messages.dataExtPageLbl);
        extPageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        dataExtPageText = new Text(dataParaGroup, SWT.BORDER | SWT.RIGHT);
        final GridData gdDataPageText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        dataExtPageText.setLayoutData(gdDataPageText);
    }

    /**
     * Creates indexParaGroup which is the part of Dialog area
     *
     * @param composite Composite
     */
    private void createIndexParaGroup(Composite composite) {
        final Group indexParaGroup = new Group(composite, SWT.RESIZE);
        final GridData gdIndexParaGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        final GridLayout gridLayout = new GridLayout(4, false);
        indexParaGroup.setLayout(gridLayout);
        indexParaGroup.setLayoutData(gdIndexParaGroup);
        indexParaGroup.setText(Messages.indexGroupTitle);

        indexUsingAutoVolButton = new Button(indexParaGroup, SWT.CHECK);
        indexUsingAutoVolButton.setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
        indexUsingAutoVolButton.setText(Messages.indexUseAutoVolBtnText);

        final Label outOfSpaceLabel = new Label(indexParaGroup, SWT.NONE);
        outOfSpaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        outOfSpaceLabel.setText(Messages.indexOutOfSpaceRateLbl);

        indexOutRateCombo = new Combo(indexParaGroup, SWT.BORDER | SWT.RIGHT);
        indexOutRateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label volumeLabel = new Label(indexParaGroup, SWT.NONE);
        volumeLabel.setText(Messages.indexvolumeLbl);
        volumeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        indexVolumeText = new Text(indexParaGroup, SWT.BORDER | SWT.RIGHT);
        final GridData gdDataVolumeText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        indexVolumeText.setLayoutData(gdDataVolumeText);

        final Label extPageLabel = new Label(indexParaGroup, SWT.NONE);
        extPageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        extPageLabel.setText(Messages.indexExtPageLbl);

        indexExtPageText = new Text(indexParaGroup, SWT.BORDER | SWT.RIGHT);
        final GridData gdDataText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        indexExtPageText.setLayoutData(gdDataText);
    }

    /** */
    public void okPressed() {
        buildGetAutoAddVolumeInfo();
        super.okPressed();
    }

    /**
     * Gets the instance of GetAutoAddVolumeInfo
     *
     * @return getAutoAddVolumeInfo
     */
    public GetAutoAddVolumeInfo getGetAutoAddVolumeInfo() {
        return getAutoAddVolumeInfo;
    }

    /**
     * Sets the instance of GetAutoAddVolumeInfo
     *
     * @param getAutoAddVolumeInfo GetAutoAddVolumeInfo
     */
    public void setGetAutoAddVolumeInfo(GetAutoAddVolumeInfo getAutoAddVolumeInfo) {
        this.getAutoAddVolumeInfo = getAutoAddVolumeInfo;
    }

    /** Initials some values */
    private void init() {
        int itemsSize = RATEMAX - RATEMIN + 1;
        String[] itemsOfOutRate;
        itemsOfOutRate = new String[itemsSize];
        for (int i = 0; i < itemsSize; i++) {
            itemsOfOutRate[i] = Integer.toString(RATEMIN + i);
        }
        // Sets the initial value
        if (getAutoAddVolumeInfo.getData().equals(OnOffType.ON.getText())) {
            dataUsingAutoVolButton.setSelection(true);
            dataOutRateCombo.setEnabled(true);
            dataVolumeText.setEnabled(true);
            dataExtPageText.setEnabled(true);
        } else {
            dataUsingAutoVolButton.setSelection(false);
            dataOutRateCombo.setEnabled(false);
            dataVolumeText.setEnabled(false);
            dataExtPageText.setEnabled(false);
        }

        if (getAutoAddVolumeInfo.getIndex().equals(OnOffType.ON.getText())) {
            indexUsingAutoVolButton.setSelection(true);
            indexOutRateCombo.setEnabled(true);
            indexVolumeText.setEnabled(true);
            indexExtPageText.setEnabled(true);
        } else {
            indexUsingAutoVolButton.setSelection(false);
            indexOutRateCombo.setEnabled(false);
            indexVolumeText.setEnabled(false);
            indexExtPageText.setEnabled(false);
        }

        int dataWarnOutofSpace =
                (int)
                        ((Double.parseDouble(getAutoAddVolumeInfo.getData_warn_outofspace()) * 100)
                                + 0.5);
        int indexWarnOutofSpace =
                (int)
                        ((Double.parseDouble(getAutoAddVolumeInfo.getIndex_warn_outofspace()) * 100)
                                + 0.5);
        if (dataWarnOutofSpace < RATEMIN) {
            dataWarnOutofSpace = DEFAULT_RATE;
        } else if (dataWarnOutofSpace > RATEMAX) {
            dataWarnOutofSpace = RATEMAX;
        }
        if (indexWarnOutofSpace < RATEMIN) {
            indexWarnOutofSpace = DEFAULT_RATE;
        } else if (dataWarnOutofSpace > RATEMAX) {
            indexWarnOutofSpace = RATEMAX;
        }
        dataOutRateCombo.setItems(itemsOfOutRate);
        dataOutRateCombo.setText(Integer.toString(dataWarnOutofSpace));
        indexOutRateCombo.setItems(itemsOfOutRate);
        indexOutRateCombo.setText(Integer.toString(indexWarnOutofSpace));

        BigDecimal dataExtPage = new BigDecimal(getAutoAddVolumeInfo.getData_ext_page());
        BigDecimal indexExtPage = new BigDecimal(getAutoAddVolumeInfo.getIndex_ext_page());

        if (dataExtPage.compareTo(BigDecimal.ZERO) <= 0) {
            initDataVol = new BigDecimal(INIT_VOLUME);
            dataExtPage =
                    initDataVol.multiply(MEGABYTES).divide(pageSize, 0, BigDecimal.ROUND_HALF_UP);
        } else {
            initDataVol =
                    dataExtPage.multiply(pageSize).divide(MEGABYTES, 3, BigDecimal.ROUND_HALF_UP);
        }
        if (indexExtPage.compareTo(BigDecimal.ZERO) <= 0) {
            initIndexVol = new BigDecimal(INIT_VOLUME);
            indexExtPage =
                    initIndexVol.multiply(MEGABYTES).divide(pageSize, 0, BigDecimal.ROUND_HALF_UP);
        } else {
            initIndexVol =
                    indexExtPage.multiply(pageSize).divide(MEGABYTES, 3, BigDecimal.ROUND_HALF_UP);
        }

        initDataExt = dataExtPage.toString();
        initIndexExt = indexExtPage.toString();

        dataExtPageText.setText(initDataExt);
        dataVolumeText.setText(initDataVol.setScale(3).toString());
        indexExtPageText.setText(initIndexExt);
        indexVolumeText.setText(initIndexVol.setScale(3).toString());

        dataOutRateCombo.addVerifyListener(new NumberVerifyListener());
        dataOutRateCombo.addModifyListener(new DataRateModifyListener());
        dataVolumeModifyListener = new VolumeModifyListener();
        dataPageModifyListener = new PageModifyListener();
        dataVolumeText.addFocusListener(
                new FocusListener() {

                    public void focusGained(FocusEvent event) {
                        dataVolumeText.addModifyListener(dataVolumeModifyListener);
                    }

                    public void focusLost(FocusEvent event) {
                        dataVolumeText.removeModifyListener(dataVolumeModifyListener);
                    }
                });
        dataExtPageText.addFocusListener(
                new FocusListener() {

                    public void focusGained(FocusEvent event) {
                        dataExtPageText.addModifyListener(dataPageModifyListener);
                    }

                    public void focusLost(FocusEvent event) {
                        dataExtPageText.removeModifyListener(dataPageModifyListener);
                    }
                });

        indexOutRateCombo.addVerifyListener(new NumberVerifyListener());
        indexOutRateCombo.addModifyListener(new IndexRateModifyListener());
        indexVolumeModifyListener = new VolumeModifyListener();
        indexPageModifyListener = new PageModifyListener();
        indexVolumeText.addFocusListener(
                new FocusListener() {

                    public void focusGained(FocusEvent event) {
                        indexVolumeText.addModifyListener(indexVolumeModifyListener);
                    }

                    public void focusLost(FocusEvent event) {
                        indexVolumeText.removeModifyListener(indexVolumeModifyListener);
                    }
                });
        indexExtPageText.addFocusListener(
                new FocusListener() {

                    public void focusGained(FocusEvent event) {
                        indexExtPageText.addModifyListener(indexPageModifyListener);
                    }

                    public void focusLost(FocusEvent event) {
                        indexExtPageText.removeModifyListener(indexPageModifyListener);
                    }
                });

        dataUsingAutoVolButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (dataUsingAutoVolButton.getSelection()) {
                            dataOutRateCombo.setEnabled(true);
                            dataVolumeText.setEnabled(true);
                            dataExtPageText.setEnabled(true);
                        } else {
                            dataOutRateCombo.setEnabled(false);
                            dataOutRateCombo.setText(Integer.toString(DEFAULT_RATE));
                            dataVolumeText.setText(initDataVol.toString());
                            dataExtPageText.setText(initDataExt);
                            enableOk();
                            dataVolumeText.setEnabled(false);
                            dataExtPageText.setEnabled(false);
                        }
                    }
                });
        indexUsingAutoVolButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (indexUsingAutoVolButton.getSelection()) {
                            indexOutRateCombo.setEnabled(true);
                            indexVolumeText.setEnabled(true);
                            indexExtPageText.setEnabled(true);
                        } else {
                            indexOutRateCombo.setEnabled(false);
                            indexOutRateCombo.setText(Integer.toString(DEFAULT_RATE));
                            indexVolumeText.setText(initIndexVol.toString());
                            indexExtPageText.setText(initIndexExt);
                            enableOk();
                            indexVolumeText.setEnabled(false);
                            indexExtPageText.setEnabled(false);
                        }
                    }
                });
    }

    /** Builds the instance of GetAutoAddVolumeInfo */
    private void buildGetAutoAddVolumeInfo() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);
        if (dataUsingAutoVolButton.getSelection()) {
            getAutoAddVolumeInfo.setData(OnOffType.ON.getText());
            double dataOutRate = Double.valueOf(dataOutRateCombo.getText()) / 100.0;
            getAutoAddVolumeInfo.setData_warn_outofspace(nf.format(dataOutRate));
            getAutoAddVolumeInfo.setData_ext_page(dataExtPageText.getText().trim());
        } else {
            getAutoAddVolumeInfo.setData(OnOffType.OFF.getText());
            getAutoAddVolumeInfo.setData_warn_outofspace("0");
            getAutoAddVolumeInfo.setData_ext_page("0");
        }
        if (indexUsingAutoVolButton.getSelection()) {
            getAutoAddVolumeInfo.setIndex(OnOffType.ON.getText());
            double indexOutRate = Double.valueOf(indexOutRateCombo.getText()) / 100.0;
            getAutoAddVolumeInfo.setIndex_warn_outofspace(nf.format(indexOutRate));
            getAutoAddVolumeInfo.setIndex_ext_page(indexExtPageText.getText().trim());

        } else {
            getAutoAddVolumeInfo.setIndex(OnOffType.OFF.getText());
            getAutoAddVolumeInfo.setIndex_warn_outofspace("0");
            getAutoAddVolumeInfo.setIndex_ext_page("0");
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.setDialogTitle);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = new BigDecimal(pageSize);
    }

    /**
     * A class that verify the entering of rate Spinner
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class DataRateModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String sRate = ((Combo) event.widget).getText().trim();
            if (sRate.length() == 0) {
                isOkenable[0] = false;
                enableOk();
                return;
            }
            int rate = Integer.valueOf(sRate);
            if (rate > RATEMAX || rate < RATEMIN) {
                isOkenable[0] = false;
            } else {
                isOkenable[0] = true;
            }
            enableOk();
        }
    }

    /**
     * A class that verify the entering of rate Spinner
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class IndexRateModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            String sRate = ((Combo) event.widget).getText().trim();
            if (sRate.length() == 0) {
                isOkenable[2] = false;
                enableOk();
                return;
            }
            int rate = Integer.valueOf(sRate);
            if (rate > RATEMAX || rate < RATEMIN) {
                isOkenable[2] = false;
            } else {
                isOkenable[2] = true;
            }
            enableOk();
        }
    }

    /**
     * A class that verify the entering of volumeText
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private static class NumberVerifyListener implements VerifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void verifyText(VerifyEvent event) {
            if (("").equals(event.text)) {
                return;
            }
            if (ValidateUtil.isNumber(event.text)) {
                event.doit = true;
            } else {
                event.doit = false;
            }
        }
    }

    /**
     * A class that response to the modify of volumeText
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class VolumeModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            if (event.widget == dataVolumeText) {
                String sVolume = dataVolumeText.getText();

                if (!ValidateUtil.isPositiveDouble(sVolume)) {
                    isOkenable[1] = false;
                    enableOk();
                    return;
                }
                BigDecimal volume = new BigDecimal(sVolume);
                BigDecimal page = volume.multiply(MEGABYTES.divide(pageSize));

                if (page.compareTo(BigDecimal.ONE) < 0) {
                    isOkenable[1] = false;
                    enableOk();
                    return;
                }

                isOkenable[1] = true;
                isOkenable[4] = true;
                enableOk();
                page = page.setScale(0, BigDecimal.ROUND_HALF_UP);
                dataExtPageText.setText(page.toString());

            } else if (event.widget == indexVolumeText) {
                String sVolume = indexVolumeText.getText();

                if (!ValidateUtil.isPositiveDouble(sVolume)) {
                    isOkenable[3] = false;
                    enableOk();
                    return;
                }

                BigDecimal volume = new BigDecimal(sVolume);
                BigDecimal page = volume.multiply(MEGABYTES.divide(pageSize));

                if (page.compareTo(BigDecimal.ONE) < 0) {
                    isOkenable[3] = false;
                    enableOk();
                    return;
                }
                isOkenable[3] = true;
                isOkenable[5] = true;
                enableOk();
                page = page.setScale(0, BigDecimal.ROUND_HALF_UP);
                indexExtPageText.setText(page.toString());
            }
        }
    }

    /**
     * A class that response to the modify of volumeText
     *
     * @author lizhiqing
     * @version 1.0 - 2009-12-28 created by lizhiqing
     */
    private class PageModifyListener implements ModifyListener {
        /**
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         * @param event an event containing information about the modify
         */
        public void modifyText(ModifyEvent event) {
            if (event.widget == dataExtPageText) {
                String sPage = dataExtPageText.getText();

                if (!ValidateUtil.isInteger(sPage)) {
                    isOkenable[4] = false;
                    enableOk();
                    return;
                }

                BigDecimal page = new BigDecimal(sPage);
                if (page.compareTo(BigDecimal.ONE) < 0) {
                    isOkenable[4] = false;
                    enableOk();
                }
                isOkenable[4] = true;
                isOkenable[1] = true;
                enableOk();

                BigDecimal volume =
                        page.multiply(pageSize).divide(MEGABYTES, 3, BigDecimal.ROUND_HALF_UP);
                dataVolumeText.setText(volume.toString());
            } else if (event.widget == indexExtPageText) {
                String sPage = indexExtPageText.getText();

                if (!ValidateUtil.isInteger(sPage)) {
                    isOkenable[5] = false;
                    enableOk();
                    return;
                }

                BigDecimal page = new BigDecimal(sPage);
                if (page.compareTo(BigDecimal.ONE) < 0) {
                    isOkenable[5] = false;
                    enableOk();
                }
                isOkenable[5] = true;
                isOkenable[3] = true;
                enableOk();

                BigDecimal volume =
                        page.multiply(pageSize).divide(MEGABYTES, 3, BigDecimal.ROUND_HALF_UP);
                indexVolumeText.setText(volume.toString());
            }
        }
    }

    /** Enable the "OK" button */
    private void enableOk() {
        boolean is = true;
        for (int i = 0; i < isOkenable.length; i++) {
            is = is && isOkenable[i];
        }
        if (is) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            setErrorMessage(null);
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
        if (!isOkenable[0] || !isOkenable[2]) {
            setErrorMessage(Messages.bind(Messages.errorRate, RATEMIN, RATEMAX));
            return;
        }
        if (!isOkenable[1] || !isOkenable[3]) {
            setErrorMessage(Messages.errorVolume);
            return;
        }
        if (!isOkenable[4] || !isOkenable[5]) {
            setErrorMessage(Messages.errorPage);
            return;
        }
    }
}
