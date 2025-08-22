/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.monitoring.editor.internal;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This type is responsible for setting the chart property such as the title's color, the plot's
 * background
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-17 created by lizhiqiang
 */
public class ChartSettingDlg extends CMTitleAreaDialog {
    private ColorSelector appearanceColorEditor;
    private List appearanceColorList;

    // title
    private String titleName;
    private String ttlBgColor;
    private String ttlFontName = "";
    private int ttlFontSize;
    private String ttlFontColor = "0,0,0";

    // plot
    private String plotBgColor = "0,0,0";
    private String plotDomainGridColor = "0,128,64";
    private String plotRangGridColor = "0,128,64";
    private String plotDateAxisColor = "0,255,255";
    private String plotNumberAxisColor = "255,255,0";
    // arrays for appearance [][0]:chart appearance name, [][1]:color,
    // [][2]default color
    private String[][] appearanceColorListModel =
            new String[][] {
                {Messages.plotBackgroudTxt, "0,0,0", "0,0,0"},
                {Messages.plotDomainGridTxt, "0,128,64", "0,128,64"},
                {Messages.plotRangeGridTxt, "0,128,64", "0,128,64"},
                {Messages.plotDateAxisTxt, "0,255,255", "0,255,255"},
                {Messages.plotNumberAxisTxt, "255,255,0", "255,255,0"}
            };
    private ColorSelector colorSelector;
    private FontGroup fontGroup;

    // series
    private TreeMap<String, ShowSetting> settingMap;
    private boolean showTitlteContent;
    private Text contentTxt;
    private CTabFolder folder;
    private CTabItem seriesItem;
    private Scale widthScale;
    private boolean hasTitlSetting = true;
    private boolean hasPlotItemSetting = true;
    private boolean hasSeriesItemSetting = true;
    private boolean hasHistoryPath = true;
    private boolean hasChartSelection = false;
    // history path
    private Text historyPathTxt;
    private String historyPath;
    private String historyFileName;
    // chart selection
    private java.util.List<ChartShowingProp> chartSelectionLst;
    private Button[] chartSelectBtns;

    // Constructor
    public ChartSettingDlg(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above the button bar).
     *
     * @param parent The parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        comp.setLayout(layout);
        comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        setTitle(Messages.chartSettingDlgTtl);
        setMessage(Messages.chartSettingDlgMsg);

        folder = new CTabFolder(comp, SWT.BORDER);
        folder.setLayout(new GridLayout());
        GridData gdTabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);

        folder.setLayoutData(gdTabFolder);

        folder.setSimple(false);
        if (hasTitlSetting) {
            createTtlTab();
        }
        if (hasPlotItemSetting) {
            createPlotItem();
        }
        if (hasSeriesItemSetting) {
            createSeriesItemByDefault();
        }
        if (hasHistoryPath) {
            createHistoryPathItem();
        }
        if (hasChartSelection) {
            createChartSelectionItem();
        }
        return parent;
    }

    /** create chart title tab item */
    public void createTtlTab() {

        CTabItem ttlItem = new CTabItem(folder, SWT.NONE);
        ttlItem.setText(Messages.tabItemChartTtl);

        Composite tabComp = new Composite(folder, SWT.NONE);

        tabComp.setLayout(new GridLayout());
        tabComp.setLayoutData(GridData.FILL_BOTH);
        ttlItem.setControl(tabComp);

        if (showTitlteContent) {
            Group contentGrp = new Group(tabComp, SWT.NONE);
            contentGrp.setLayout(new GridLayout(2, false));
            contentGrp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            contentGrp.setText(Messages.chartTtlContentGrp);

            Label contentLbl = new Label(contentGrp, SWT.NONE);
            contentLbl.setText(Messages.chartTtlName);

            contentTxt = new Text(contentGrp, SWT.BORDER);
            final GridData gdContentTxt = new GridData(SWT.FILL, SWT.CENTER, true, false);
            contentTxt.setLayoutData(gdContentTxt);
            if (titleName != null) {
                contentTxt.setText(titleName);
            }
        }

        Group colorGroup = new Group(tabComp, SWT.NONE);

        colorGroup.setLayout(new GridLayout(2, false));
        colorGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        colorGroup.setText(Messages.chartTtlGroupTxt);

        Label bgColorLbl = new Label(colorGroup, SWT.NONE);
        bgColorLbl.setText(Messages.chartTtlBgLbl);
        colorSelector = new ColorSelector(colorGroup);
        colorSelector.getButton().setLayoutData(new GridData());
        if (ttlBgColor != null) {
            RGB rgb = StringConverter.asRGB(ttlBgColor);
            colorSelector.setColorValue(rgb);
        }

        fontGroup = new FontGroup(tabComp);

        fontGroup.setFontName(ttlFontName);

        fontGroup.setFontSize(ttlFontSize);
        fontGroup.setChosenRgb(StringConverter.asRGB(ttlFontColor));
        fontGroup.loadContent();
    }

    /** Create plot appearance tab item */
    public void createPlotItem() {
        CTabItem ttlItem = new CTabItem(folder, SWT.NONE);
        ttlItem.setText(Messages.tabItemPlotTtl);

        Composite tabComp = new Composite(folder, SWT.NONE);
        tabComp.setLayout(new GridLayout());
        tabComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        ttlItem.setControl(tabComp);

        Group group = new Group(tabComp, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setText(Messages.plotAppearanceGroupTxt);
        appearanceColorList = new List(group, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        GC gc = new GC(folder.getParent());
        gc.setFont(folder.getParent().getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        gd.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 15);
        appearanceColorList.setLayoutData(gd);
        for (int i = 0; i < appearanceColorListModel.length; i++) {
            appearanceColorList.add(appearanceColorListModel[i][0]);
        }

        Composite stylesComposite = new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout(2, true);
        stylesComposite.setLayout(layout);
        stylesComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        Label colorlbl = new Label(stylesComposite, SWT.LEFT);
        colorlbl.setText(Messages.plotColorLbl);
        gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        colorlbl.setLayoutData(gd);

        appearanceColorEditor = new ColorSelector(stylesComposite);
        Button foregroundColorButton = appearanceColorEditor.getButton();
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        foregroundColorButton.setLayoutData(gd);

        appearanceColorList.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent ex) {
                        widgetSelected(ex);
                    }

                    public void widgetSelected(SelectionEvent ex) {
                        int i = appearanceColorList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        RGB rgb = StringConverter.asRGB(appearanceColorListModel[i][1]);
                        appearanceColorEditor.setColorValue(rgb);
                    }
                });
        foregroundColorButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent ex) {
                        widgetSelected(ex);
                    }

                    public void widgetSelected(SelectionEvent ex) {
                        int i = appearanceColorList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        RGB rgb = appearanceColorEditor.getColorValue();
                        appearanceColorListModel[i][1] = StringConverter.asString(rgb);
                    }
                });
        appearanceColorListModel[0][1] = plotBgColor;
        appearanceColorListModel[1][1] = plotDomainGridColor;
        appearanceColorListModel[2][1] = plotRangGridColor;
        if (appearanceColorListModel.length > 3) {
            appearanceColorListModel[3][1] = plotDateAxisColor;
        }
        if (appearanceColorListModel.length > 4) {
            appearanceColorListModel[4][1] = plotNumberAxisColor;
        }

        if (appearanceColorList.getItemCount() > 0) {
            appearanceColorList.select(0);
            RGB rgb = StringConverter.asRGB(appearanceColorListModel[0][1]);
            appearanceColorEditor.setColorValue(rgb);
        }
    }

    /**
     * Create the series tab item based on the index
     *
     * @param index the index of TabItem
     */
    public void createSeriesItemByIndex(int index) {
        seriesItem = new CTabItem(folder, SWT.NONE, index);
        createSeriesItem();
    }

    /** Create the series tab item based on the default order */
    public void createSeriesItemByDefault() {
        seriesItem = new CTabItem(folder, SWT.NONE);
        createSeriesItem();
    }

    /** Create Series tab item */
    private void createSeriesItem() {
        seriesItem.setText(Messages.tabItemSeriesTtl);
        Composite tabComp = new Composite(folder, SWT.NONE);
        tabComp.setLayout(new GridLayout());
        tabComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        seriesItem.setControl(tabComp);

        Group group = new Group(tabComp, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        final List seriesSelectionList = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        group.setText(Messages.seriesGroupTxt);
        GridData gd = new GridData(GridData.FILL_BOTH);
        GC gc = new GC(folder.getParent());
        gc.setFont(folder.getParent().getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();

        gd.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 15);
        seriesSelectionList.setLayoutData(gd);

        for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
            String key = entry.getKey();
            seriesSelectionList.add(key);
        }

        Composite optionsComp = new Composite(group, SWT.NONE);
        optionsComp.setLayout(new GridLayout());
        optionsComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

        Composite checkComposite = new Composite(optionsComp, SWT.NONE);
        checkComposite.setLayout(new GridLayout());
        checkComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        Composite colorComposite = new Composite(optionsComp, SWT.NONE);
        GridLayout colorlayout = new GridLayout(2, false);
        colorComposite.setLayout(colorlayout);
        colorComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));

        final Button checkBtn = new Button(checkComposite, SWT.CHECK);
        checkBtn.setText(Messages.seriesCheckBtnLbl);
        checkBtn.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

        Label colorlbl = new Label(colorComposite, SWT.LEFT);
        colorlbl.setText(Messages.seriesColorLbl);

        colorlbl.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

        final ColorSelector seriesColorEditor = new ColorSelector(colorComposite);
        Button foregroundColorButton = seriesColorEditor.getButton();

        foregroundColorButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false));

        Composite widthComposite = new Composite(optionsComp, SWT.NONE);
        widthComposite.setLayout(new GridLayout());
        widthComposite.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));

        final Label widthLbl = new Label(widthComposite, SWT.LEFT);

        widthScale = new Scale(widthComposite, SWT.HORIZONTAL);
        widthScale.setMinimum(1);
        widthScale.setMaximum(5);
        widthScale.setIncrement(1);
        widthScale.setPageIncrement(1);
        widthScale.setSelection(2);
        float width = ((float) widthScale.getSelection()) / 2;
        widthLbl.setText(Messages.bind(Messages.seriesWidthLbl, width));

        seriesSelectionList.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent ex) {
                        widgetSelected(ex);
                    }

                    public void widgetSelected(SelectionEvent ex) {
                        int i = seriesSelectionList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        if (seriesSelectionList.getSelectionCount() == 1) {
                            String[] selection = seriesSelectionList.getSelection();
                            for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
                                String key = entry.getKey();
                                ShowSetting showSetting = entry.getValue();
                                if (key.equals(selection[0])) {
                                    checkBtn.setSelection(showSetting.isChecked());
                                    seriesColorEditor.setColorValue(showSetting.getSeriesRgb());
                                    int widthInScale = (int) showSetting.getWidth() * 2;
                                    widthScale.setSelection(widthInScale);
                                    break;
                                }
                            }
                        } else {
                            checkBtn.setSelection(false);
                            Color colorBtnBg = seriesColorEditor.getButton().getBackground();
                            seriesColorEditor.setColorValue(
                                    new RGB(
                                            colorBtnBg.getRed(),
                                            colorBtnBg.getGreen(),
                                            colorBtnBg.getBlue()));
                            widthScale.setSelection(2); // default value
                        }
                        float width = ((float) widthScale.getSelection()) / 2;
                        widthLbl.setText(Messages.bind(Messages.seriesWidthLbl, width));
                    }
                });
        foregroundColorButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetDefaultSelected(SelectionEvent ex) {
                        widgetSelected(ex);
                    }

                    public void widgetSelected(SelectionEvent ex) {
                        int i = seriesSelectionList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        RGB rgb = seriesColorEditor.getColorValue();
                        float width = ((float) widthScale.getSelection()) / 2;
                        boolean isChecked = checkBtn.getSelection();
                        String[] selection = seriesSelectionList.getSelection();
                        updateValueInSettingMap(rgb, width, isChecked, selection);
                    }
                });

        checkBtn.addSelectionListener(
                new SelectionAdapter() {
                    /**
                     * Sent when selection occurs in the control.
                     *
                     * @param event an event containing information about the selection
                     */
                    public void widgetSelected(SelectionEvent event) {
                        widgetDefaultSelected(event);
                    }

                    /**
                     * Sent when default selection occurs in the control.
                     *
                     * @param event an event containing information about the default selection
                     */
                    public void widgetDefaultSelected(SelectionEvent event) {
                        int i = seriesSelectionList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        RGB rgb = seriesColorEditor.getColorValue();
                        float width = ((float) widthScale.getSelection()) / 2;
                        boolean isChecked = checkBtn.getSelection();
                        String[] selection = seriesSelectionList.getSelection();
                        updateValueInSettingMap(rgb, width, isChecked, selection);
                    }
                });
        widthScale.addListener(
                SWT.Selection,
                new Listener() {
                    public void handleEvent(Event event) {
                        int i = seriesSelectionList.getSelectionIndex();
                        if (i == -1) {
                            return;
                        }
                        float width = ((float) widthScale.getSelection()) / 2;
                        widthLbl.setText(Messages.bind(Messages.seriesWidthLbl, width));
                        RGB rgb = seriesColorEditor.getColorValue();
                        boolean isChecked = checkBtn.getSelection();
                        String[] selection = seriesSelectionList.getSelection();
                        updateValueInSettingMap(rgb, width, isChecked, selection);
                    }
                });
        if (seriesSelectionList.getItemCount() > 0) {
            seriesSelectionList.select(0);
            String[] selectedKey = seriesSelectionList.getSelection();
            ShowSetting setting = settingMap.get(selectedKey[0]);
            checkBtn.setSelection(setting.isChecked());
            seriesColorEditor.setColorValue(setting.getSeriesRgb());
            widthLbl.setText(Messages.bind(Messages.seriesWidthLbl, setting.getWidth()));
            int widthInScale = (int) setting.getWidth() * 2;
            widthScale.setSelection(widthInScale);
        }
    }

    /**
     * Create history path item.This item can be controlled to show or hide by setting the field of
     * isSetHistoryPath, whose default value is true.
     */
    public void createHistoryPathItem() {
        CTabItem historyItem = new CTabItem(folder, SWT.NONE);
        historyItem.setText(Messages.tabItemHitoryTtl);

        Composite tabComp = new Composite(folder, SWT.NONE);
        tabComp.setLayout(new GridLayout());
        tabComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        historyItem.setControl(tabComp);

        Group group = new Group(tabComp, SWT.NONE);
        group.setText(Messages.msgHistoryPathGrp);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Label pathLabel = new Label(group, SWT.RESIZE);
        final GridData gdPathLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        pathLabel.setLayoutData(gdPathLabel);
        pathLabel.setText(Messages.msgHistoryPathLbl);

        historyPathTxt = new Text(group, SWT.BORDER);
        final GridData gdPathText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdPathText.widthHint = 240;
        historyPathTxt.setLayoutData(gdPathText);
        if (null != historyPath) {
            historyPathTxt.setText(historyPath);
        }
        historyPathTxt.setEditable(false);

        Button selectTargetDirectoryButton = new Button(group, SWT.NONE);
        selectTargetDirectoryButton.setText(Messages.btnBrowse);
        selectTargetDirectoryButton.setLayoutData(CommonUITool.createGridData(1, 1, 80, -1));
        selectTargetDirectoryButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        DirectoryDialog dlg = new DirectoryDialog(folder.getShell());
                        String historyDir = historyPathTxt.getText();
                        if (historyDir != null && historyDir.trim().length() > 0) {
                            dlg.setFilterPath(historyDir);
                        }
                        dlg.setText(Messages.msgSelectDir);
                        dlg.setMessage(Messages.msgSelectDir);
                        String dir = dlg.open();
                        if (dir != null) {
                            String newPath = dir + File.separator + historyFileName;
                            historyPathTxt.setText(newPath);
                        }
                    }
                });
    }

    /**
     * Create chart selection item.This item can be controlled to show or hide by setting the field
     * of hasChartSelection, whose default value is false.
     */
    public void createChartSelectionItem() {
        CTabItem chartSelectionItem = new CTabItem(folder, SWT.NONE);
        chartSelectionItem.setText(Messages.tabItemChartSelectTtl);

        Composite tabComp = new Composite(folder, SWT.NONE);
        tabComp.setLayout(new GridLayout());
        tabComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        chartSelectionItem.setControl(tabComp);

        if (null == chartSelectionLst) {
            return;
        }
        chartSelectBtns = new Button[chartSelectionLst.size()];
        int buttonNum = 0;
        int showingCount = 0;
        int soleSelection = 0;
        for (ChartShowingProp prop : chartSelectionLst) {
            chartSelectBtns[buttonNum] = new Button(tabComp, SWT.CHECK | SWT.RIGHT);
            chartSelectBtns[buttonNum].setText(prop.getName());
            boolean showing = prop.isShowing();
            chartSelectBtns[buttonNum].setSelection(showing);
            if (showing) {
                showingCount++;
                soleSelection = buttonNum;
            }
            buttonNum++;
        }
        if (showingCount == 1) {
            chartSelectBtns[soleSelection].setEnabled(false);
        }
        for (Button button : chartSelectBtns) {
            button.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent ex) {
                            int count = 0;
                            Button soleBtn = null;
                            for (Button btn : chartSelectBtns) {
                                if (btn.getSelection()) {
                                    count++;
                                    if (count == 1) {
                                        soleBtn = btn;
                                    }
                                }
                            }
                            if (count == 1) {
                                soleBtn.setEnabled(false);
                            } else {
                                for (Button btn : chartSelectBtns) {
                                    btn.setEnabled(true);
                                }
                            }
                        }
                    });
        }
    }

    /** When press "ok" button,call it */
    public void okPressed() {
        performGetData();
        super.okPressed();
    }

    /** Perform to get the result. */
    public void performGetData() {
        // title
        if (colorSelector != null) {
            RGB rgb = colorSelector.getColorValue();
            if (rgb == null) {
                ttlBgColor = null;
            } else {
                ttlBgColor = StringConverter.asString(rgb);
            }
        }
        if (fontGroup != null) {
            ttlFontName = fontGroup.getFontNameText().getText();
            ttlFontSize = Integer.parseInt(fontGroup.getFontSizeText().getText());
            ttlFontColor = StringConverter.asString(fontGroup.getChosenRgb());
        }
        // plot
        plotBgColor = appearanceColorListModel[0][1];
        plotDomainGridColor = appearanceColorListModel[1][1];
        plotRangGridColor = appearanceColorListModel[2][1];
        if (appearanceColorListModel.length > 3) {
            plotDateAxisColor = appearanceColorListModel[3][1];
        }
        if (appearanceColorListModel.length > 4) {
            plotNumberAxisColor = appearanceColorListModel[4][1];
        }

        // history path
        if (historyPathTxt != null) {
            historyPath = historyPathTxt.getText().trim();
        }
        // chart selection
        if (chartSelectBtns != null) {
            for (Button button : chartSelectBtns) {
                String key = button.getText();
                boolean selection = button.getSelection();
                for (ChartShowingProp prop : chartSelectionLst) {
                    if (prop.getName().equals(key)) {
                        prop.setShowing(selection);
                    }
                }
            }
        }
    }

    /** Constrain the shell size */
    protected void constrainShellSize() {
        super.constrainShellSize();
        CommonUITool.centerShell(getShell());
        getShell().setText(Messages.shlChartSetTxt);
    }

    /**
     * Get the title background color.
     *
     * @return the ttlBgColor
     */
    public String getTtlBgColor() {
        return ttlBgColor;
    }

    /**
     * Set the title background color.
     *
     * @param ttlBgColor the ttlBgColor to set
     */
    public void setTtlBgColor(String ttlBgColor) {
        this.ttlBgColor = ttlBgColor;
    }

    /**
     * Get the title font name
     *
     * @return the ttlFontName
     */
    public String getTtlFontName() {
        return ttlFontName;
    }

    /**
     * Set the title font name.
     *
     * @param ttlFontName the ttlFontName to set
     */
    public void setTtlFontName(String ttlFontName) {
        this.ttlFontName = ttlFontName;
    }

    /**
     * Get the title Font size.
     *
     * @return the ttlFontSize
     */
    public int getTtlFontSize() {
        return ttlFontSize;
    }

    /**
     * Set the title font size
     *
     * @param ttlFontSize the ttlFontSize to set
     */
    public void setTtlFontSize(int ttlFontSize) {
        this.ttlFontSize = ttlFontSize;
    }

    /**
     * Get the title font color
     *
     * @return the ttlFontColor
     */
    public String getTtlFontColor() {
        return ttlFontColor;
    }

    /**
     * Set the title font color
     *
     * @param ttlFontColor the ttlFontColor to set
     */
    public void setTtlFontColor(String ttlFontColor) {
        this.ttlFontColor = ttlFontColor;
    }

    /**
     * Get the plot background color
     *
     * @return the plotBgColor
     */
    public String getPlotBgColor() {
        return plotBgColor;
    }

    /**
     * Set the plot background color
     *
     * @param plotBgColor the plotBgColor to set
     */
    public void setPlotBgColor(String plotBgColor) {
        this.plotBgColor = plotBgColor;
    }

    /**
     * Get the plot Domain grid line color
     *
     * @return the plotDomainGridColor
     */
    public String getPlotDomainGridColor() {
        return plotDomainGridColor;
    }

    /**
     * Set the plot domain grid color
     *
     * @param plotDomainGridColor the plotDomainGridColor to set
     */
    public void setPlotDomainGridColor(String plotDomainGridColor) {
        this.plotDomainGridColor = plotDomainGridColor;
    }

    /**
     * Get the plot range grid line color
     *
     * @return the plotRangGridColor
     */
    public String getPlotRangGridColor() {
        return plotRangGridColor;
    }

    /**
     * Set the plot grid line color
     *
     * @param plotRangGridColor the plotRangGridColor to set
     */
    public void setPlotRangGridColor(String plotRangGridColor) {
        this.plotRangGridColor = plotRangGridColor;
    }

    /**
     * Get the plot date axis color
     *
     * @return the plotDateAxisColor
     */
    public String getPlotDateAxisColor() {
        return plotDateAxisColor;
    }

    /**
     * Set the plot date axis color
     *
     * @param plotDateAxisColor the plotDateAxisColor to set
     */
    public void setPlotDateAxisColor(String plotDateAxisColor) {
        this.plotDateAxisColor = plotDateAxisColor;
    }

    /**
     * Get the plot number axis color
     *
     * @return the plotNumberAxisColor
     */
    public String getPlotNumberAxisColor() {
        return plotNumberAxisColor;
    }

    /**
     * Set the plot number axis color
     *
     * @param plotNumberAxisColor the plotNumberAxisColor to set
     */
    public void setPlotNumberAxisColor(String plotNumberAxisColor) {
        this.plotNumberAxisColor = plotNumberAxisColor;
    }

    /**
     * Get the settingMap value
     *
     * @return the settingMap
     */
    public TreeMap<String, ShowSetting> getSettingMap() {
        TreeMap<String, ShowSetting> instance = new TreeMap<String, ShowSetting>();
        for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
            instance.put(entry.getKey(), entry.getValue().clone());
        }
        return instance;
    }

    /** @param settingMap the settingMap to set */
    public void setSettingMap(TreeMap<String, ShowSetting> settingMap) {
        TreeMap<String, ShowSetting> instance = new TreeMap<String, ShowSetting>();
        for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
            instance.put(entry.getKey(), entry.getValue().clone());
        }
        this.settingMap = instance;
        boolean isSeriesItem = false;
        int selectionIndex = 0;
        if (null != folder && null != seriesItem && folder.getSelection() == seriesItem) {
            isSeriesItem = true;
            selectionIndex = folder.getSelectionIndex();
        }
        if (null != seriesItem) {
            seriesItem.dispose();
            createSeriesItemByIndex(selectionIndex);
        }
        if (isSeriesItem) {
            folder.setSelection(seriesItem);
        }
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.btnOK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.btnCancel, false);
    }

    /** @param showTitlteContent the showTitlteContent to set */
    public void setShowTitlteContent(boolean showTitlteContent) {
        this.showTitlteContent = showTitlteContent;
    }

    /**
     * Get the content of the control of contentTxt
     *
     * @return the contentTxt
     */
    public String getTitleName() {
        return contentTxt.getText();
    }

    /** @param titleName the titleName to set */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
        if (contentTxt != null) {
            contentTxt.setText(titleName);
        }
    }

    /**
     * Get the folder
     *
     * @return the folder
     */
    public CTabFolder getFolder() {
        return folder;
    }

    /** @param folder the folder to set */
    public void setFolder(CTabFolder folder) {
        this.folder = folder;
    }

    /**
     * Update the value in the instance of SettingMap
     *
     * @param rgb the RGB object
     * @param width the width
     * @param isChecked Whether is checked
     * @param selection the selected series
     */
    private void updateValueInSettingMap(
            RGB rgb, float width, boolean isChecked, String[] selection) {
        for (String item : selection) {
            for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
                String key = entry.getKey();
                ShowSetting showSetting = entry.getValue();
                if (item.equals(key)) {
                    showSetting.setChecked(isChecked);
                    showSetting.setSeriesRgb(rgb);
                    showSetting.setWidth(width);
                    break;
                }
            }
        }
    }

    /**
     * Get the history path
     *
     * @return the historyPath
     */
    public String getHistoryPath() {
        if (null != historyPathTxt && !historyPathTxt.isDisposed()) {
            historyPath = historyPathTxt.getText().trim();
        }
        return historyPath;
    }

    /** @param historyPath the historyPath to set */
    public void setHistoryPath(String historyPath) {
        this.historyPath = historyPath;
        if (null != historyPathTxt && !historyPathTxt.isDisposed()) {
            historyPathTxt.setText(historyPath);
        }
    }

    /**
     * Whether has the history path
     *
     * @return the hasHistoryPath
     */
    public boolean isHasHistoryPath() {
        return hasHistoryPath;
    }

    /** @param hasHistoryPath the hasHistoryPath to set */
    public void setHasHistoryPath(boolean hasHistoryPath) {
        this.hasHistoryPath = hasHistoryPath;
    }

    /** @param historyFileName the historyFileName to set */
    public void setHistoryFileName(String historyFileName) {
        this.historyFileName = historyFileName;
    }

    /** @param hasTitlSetting the hasTitlSetting to set */
    public void setHasTitlSetting(boolean hasTitlSetting) {
        this.hasTitlSetting = hasTitlSetting;
    }

    /** @param hasPlotItemSetting the hasPlotItemSetting to set */
    public void setHasPlotItemSetting(boolean hasPlotItemSetting) {
        this.hasPlotItemSetting = hasPlotItemSetting;
    }

    /** @param hasSeriesItemSetting the hasSeriesItemSetting to set */
    public void setHasSeriesItemSetting(boolean hasSeriesItemSetting) {
        this.hasSeriesItemSetting = hasSeriesItemSetting;
    }

    /**
     * Set appearanceColorListModel if axis is not exist.
     *
     * @param hasAxis reset appearanceColorListModel if it is false
     */
    public void setHasAxisSetting(boolean hasAxis) {
        if (!hasAxis) {
            appearanceColorListModel =
                    new String[][] {
                        {Messages.plotBackgroudTxt, "0,0,0", "0,0,0"},
                        {Messages.plotDomainGridTxt, "0,128,64", "0,128,64"},
                        {Messages.plotRangeGridTxt, "0,128,64", "0,128,64"}
                    };
        }
    }

    /** @param hasChartSelection the hasChartSelection to set */
    public void setHasChartSelection(boolean hasChartSelection) {
        this.hasChartSelection = hasChartSelection;
    }

    /**
     * Get the chartSelectionLst
     *
     * @return the chartSelectionLst
     */
    public java.util.List<ChartShowingProp> getChartSelectionLst() {
        return chartSelectionLst;
    }

    /** @param chartSelectionLst the chartSelectionLst to set */
    public void setChartSelectionLst(java.util.List<ChartShowingProp> chartSelectionLst) {
        this.chartSelectionLst = chartSelectionLst;
    }
}
