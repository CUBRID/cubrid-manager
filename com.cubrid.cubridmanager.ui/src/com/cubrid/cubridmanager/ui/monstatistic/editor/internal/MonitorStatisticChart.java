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
package com.cubrid.cubridmanager.ui.monstatistic.editor.internal;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticData;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.ChartType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.EditMultiHostStatisticItemDialog;
import com.cubrid.cubridmanager.ui.monstatistic.dialog.EditSingleHostStatisticItemDialog;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.slf4j.Logger;

public class MonitorStatisticChart {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(ChartCompositePart.class);

    private final String EMPTY_STRING = "";
    private final Composite composite;
    private final boolean isDetailView;
    private final boolean isMultiHost;
    private final int rangMin = 0;
    private final Image noDataImage =
            CubridManagerUIPlugin.getImage("icons/monitor/placeholder.png");
    private final XYDataset EMPTY_DATASET = new TimeSeriesCollection();
    private int rangMax = 0;
    private int dataSize = 0;
    private String groupName = null;
    private StatisticChartItem statisticChartItem;
    private List<StatisticData> statisticDataList;
    private Map<StatisticData, Double> maxValueMap = new HashMap<StatisticData, Double>();
    private Map<StatisticData, Double> minValueMap = new HashMap<StatisticData, Double>();

    private Group chartGroup;
    private Composite btnsComp;
    private Button checkBtn;
    private ChartComposite frame;
    private JFreeChart chart;
    private long recordMillisecs;
    private MonitorStatisticEditor editor;
    private String chartTitle = EMPTY_STRING;
    private String timeAxisLabel = EMPTY_STRING;
    private String valueAxisLabel = EMPTY_STRING;
    private boolean isSelected = false;
    private boolean isHasValidData = false;
    private boolean isEditMode = false;

    private ChartType chartType = ChartType.OTHERS;
    private Color[] colorAr =
            new Color[] {
                Color.GREEN,
                Color.RED,
                Color.YELLOW,
                Color.PINK,
                Color.CYAN,
                Color.MAGENTA,
                Color.LIGHT_GRAY,
                new Color(0x2d89dc),
                new Color(0xf2c951),
                new Color(0xAEEE00),
                new Color(0x00D13F)
            };

    public MonitorStatisticChart(Composite parent, boolean isDetailView, boolean isMultiHost) {
        this.composite = parent;
        this.isDetailView = isDetailView;
        this.isMultiHost = isMultiHost;
    }

    public void loadChart() {
        recordMillisecs = System.currentTimeMillis();
        if (groupName != null) {
            final Group chartGroup = addGeneralGroup(composite);
            frame =
                    new ChartComposite(
                            chartGroup, SWT.NONE, createChart(), false, true, false, true, true);
        } else {
            frame =
                    new ChartComposite(
                            composite, SWT.NONE, createChart(), false, true, false, true, true);
        }
        frame.setDisplayToolTips(true);

        GridData gdFrame = new GridData(SWT.FILL, SWT.FILL, true, true);
        frame.setLayoutData(gdFrame);
        frame.setLayout(new FillLayout());
        if (!isDetailView) {
            frame.addChartMouseListener(
                    new ChartMouseListener() {
                        public void chartMouseMoved(ChartMouseEvent event) {}

                        public void chartMouseClicked(ChartMouseEvent event) {
                            if (!isHasValidData) {
                                return;
                            }
                            if (event.getTrigger().getButton() == 1) {
                                long interval = System.currentTimeMillis() - recordMillisecs;
                                if (interval < 300) {
                                    // trigger double click action
                                    if (editor == null) {
                                        return;
                                    }
                                    editor.openDetailView(statisticChartItem, statisticDataList);
                                } else {
                                    recordMillisecs = System.currentTimeMillis();
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Add group that contains toolbar for chart in MonitorStatisticEditor.
     *
     * @param parent
     * @return
     */
    private Group addGeneralGroup(Composite parent) {
        chartGroup = new Group(parent, SWT.RESIZE);
        chartGroup.setText(groupName);
        GridLayout chartGroupLayout = new GridLayout(1, false);
        chartGroupLayout.marginHeight = 3;
        chartGroupLayout.marginWidth = 6;
        chartGroup.setLayout(chartGroupLayout);
        chartGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        chartGroup.setBackground(ResourceManager.getColor(255, 255, 255));

        if (isEditMode) {
            addToolbar(chartGroup);
        }

        return chartGroup;
    }

    private void addToolbar(Composite parent) {
        btnsComp = new Composite(chartGroup, SWT.RESIZE);
        final GridLayout btnsGridLayout = new GridLayout(2, false);
        btnsComp.setLayout(btnsGridLayout);
        btnsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        btnsComp.setBackground(ResourceManager.getColor(255, 255, 255));

        checkBtn = new Button(btnsComp, SWT.CHECK);
        checkBtn.setText(Messages.btnSelect);
        checkBtn.setBackground(ResourceManager.getColor(255, 255, 255));
        checkBtn.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        isSelected = ((Button) event.widget).getSelection();
                    }
                });

        ToolBar toolbar = new ToolBar(btnsComp, SWT.RIGHT);
        toolbar.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));
        toolbar.setBackground(ResourceManager.getColor(255, 255, 255));

        ToolItem showDetailItem = new ToolItem(toolbar, SWT.PUSH);
        showDetailItem.setText(Messages.btnShowDetail);
        showDetailItem.setToolTipText(Messages.ShowDetailTooltip);
        showDetailItem.setImage(CubridManagerUIPlugin.getImage("icons/action/status_execute.png"));
        showDetailItem.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (editor == null) {
                            return;
                        }
                        editor.openDetailView(statisticChartItem, statisticDataList);
                    }
                });

        ToolItem editItem = new ToolItem(toolbar, SWT.PUSH);
        editItem.setText(Messages.btnEdit);
        editItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_edit.png"));
        editItem.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        openEditStatisticItemDialog();
                    }
                });

        ToolItem deleteItem = new ToolItem(toolbar, SWT.PUSH);
        deleteItem.setText(Messages.btnDelete);
        deleteItem.setImage(CubridManagerUIPlugin.getImage("icons/action/auto_backup_delete.png"));
        deleteItem.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        if (!CommonUITool.openConfirmBox(
                                Messages.confirmStatisticChartRemoveWarn)) {
                            return;
                        }
                        editor.removeStatisticItem(statisticChartItem);
                    }
                });

        ToolItem refreshItem = new ToolItem(toolbar, SWT.PUSH);
        refreshItem.setText(Messages.btnRefresh);
        refreshItem.setImage(CommonUIPlugin.getImage("icons/action/refresh.png"));
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                        if (editor == null) {
                            return;
                        }
                        editor.refreshChartBySeries(statisticChartItem.getSeries());
                    }
                });
    }

    private XYDataset createDataset() {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        if (statisticDataList == null) {
            return dataset;
        }

        double maxForRang = 0;
        List<StatisticData> dataItemWithoutDataList = new ArrayList<StatisticData>();
        TimeType timeType = null;
        Date current = new Date();
        long interalMillisecs = 0;
        for (StatisticData statisticData : statisticDataList) {
            if (statisticData == null) {
                continue;
            }
            MetricType metricType = MetricType.getEnumByMetric(statisticData.getMetric());
            if (metricType == null) {
                continue;
            }
            int size = statisticData.getData().size();
            if (size == 0) {
                dataItemWithoutDataList.add(statisticData);
                continue;
            } else if (dataSize == 0) { // record data size for build invalid data
                dataSize = size;
            }
            // check data type, record 'isHasValidData = true'
            if (!isHasValidData) {
                isHasValidData = true;
                chartType = getChartType(statisticData.getMetric());
                if (chartType == ChartType.PERCENT) {
                    rangMax = 100;
                }
                String dateType = statisticData.getDtype();
                timeType = TimeType.getEnumByType(dateType);
                // CMS return -1 for invalid data, so for invalid data(<0), display as -1.
                switch (timeType) {
                    case DAILY:
                        interalMillisecs = 1000 * 60 * 60 * 24 / size;
                        break;
                    case WEEKLY:
                        interalMillisecs = 1000 * 60 * 60;
                        break;
                    case MONTHLY:
                        interalMillisecs = 1000 * 60 * 60;
                        break;
                    case YEARLY:
                        interalMillisecs = 1000 * 60 * 60 * 24;
                        break;
                    default:
                        break;
                }
            }
            TimeSeries series = null;
            if (isDetailView) {
                series = new TimeSeries(statisticData.getDescription(isMultiHost));
            } else {
                series = new TimeSeries(statisticData.getSimpleDescription(isMultiHost));
            }

            Date point = (Date) current.clone();
            long curMillisecs = current.getTime();

            int count = 0;
            int max = 0;
            int min = 0;
            boolean isInitMinVal = false;
            for (int val : statisticData.getData()) {
                point.setTime(curMillisecs - (size - count) * interalMillisecs);

                switch (timeType) {
                    case DAILY:
                        if (val < 0) {
                            series.add(new Second(point), -1);
                        } else {
                            series.add(new Second(point), val / 100);
                        }
                        break;
                    case WEEKLY:
                        if (val < 0) {
                            series.add(new Hour(point), -1);
                        } else {
                            series.add(new Hour(point), val / 100);
                        }
                        break;
                    case MONTHLY:
                        if (val < 0) {
                            series.add(new Hour(point), -1);
                        } else {
                            series.add(new Hour(point), val / 100);
                        }
                        break;
                    case YEARLY:
                        if (val < 0) {
                            series.add(new Day(point), -1);
                        } else {
                            series.add(new Day(point), val / 100);
                        }
                        break;
                    default:
                        break;
                }

                if (chartType != ChartType.PERCENT && val > maxForRang) {
                    maxForRang = val;
                }
                if (!isInitMinVal && val >= 0) {
                    min = val;
                    isInitMinVal = true;
                }
                if (val > max) {
                    max = val;
                }
                if (val < min && val >= 0) {
                    min = val;
                }
                count++;
            }
            maxValueMap.put(statisticData, max / 100d);
            minValueMap.put(statisticData, min / 100d);
            dataset.addSeries(series);
        }
        if (!isHasValidData) {
            return dataset;
        }
        if (chartType != ChartType.PERCENT) {
            decideRangMax(maxForRang / 100);
        }

        /*[TOOLS-3742] Build invalid data for StatisticData which has no data*/
        buildInvalidData(dataset, timeType, dataItemWithoutDataList, current, interalMillisecs);

        if (isDetailView) {
            chartTitle = Messages.monStatisticDetailChartTitle;
            timeAxisLabel = Messages.lblChartTime;
            valueAxisLabel = Messages.lblChartValue;
            switch (chartType) {
                case PERCENT:
                    valueAxisLabel += " (%)";
                    break;
                case MEMORY:
                case SPACE:
                    valueAxisLabel += " (MB)";
                    break;
                case OTHERS:
                    break;
                default:
                    break;
            }
        }

        return dataset;
    }

    /**
     * Build TimeSeries for StatisticData which has no data. After build, each TimeSeries accompany
     * with the StatisticData will have the same time series with ordinary TimeSeries, but all the
     * values will be -1.
     *
     * @param dataset
     * @param timeType
     * @param dataItemWithoutDataList
     * @param current
     * @param interalMillisecs
     */
    private void buildInvalidData(
            TimeSeriesCollection dataset,
            TimeType timeType,
            List<StatisticData> dataItemWithoutDataList,
            Date current,
            long interalMillisecs) {
        for (StatisticData statisticData : dataItemWithoutDataList) {
            TimeSeries series = null;
            if (isDetailView) {
                series = new TimeSeries(statisticData.getDescription(isMultiHost));
            } else {
                series = new TimeSeries(statisticData.getSimpleDescription(isMultiHost));
            }

            Date point = (Date) current.clone();
            long curMillisecs = current.getTime();
            for (int i = 0; i < dataSize; i++) {
                point.setTime(curMillisecs - (dataSize - i) * interalMillisecs);
                switch (timeType) {
                    case DAILY:
                        series.add(new Second(point), -1);
                        break;
                    case WEEKLY:
                        series.add(new Hour(point), -1);
                        break;
                    case MONTHLY:
                        series.add(new Hour(point), -1);
                        break;
                    case YEARLY:
                        series.add(new Day(point), -1);
                        break;
                    default:
                        break;
                }
            }
            maxValueMap.put(statisticData, -1d);
            minValueMap.put(statisticData, -1d);
            dataset.addSeries(series);
        }
    }

    public JFreeChart createChart() {
        final Color backGroundColor = Color.WHITE;
        // final Color backGroundColor = new Color(0xF0F0F0);
        XYDataset dataset = createDataset();
        final int seriesCount = dataset.getSeriesCount();
        chart =
                ChartFactory.createTimeSeriesChart(
                        chartTitle, timeAxisLabel, valueAxisLabel, dataset, true, true, false);
        chart.setBackgroundPaint(backGroundColor);
        if (!isHasValidData) {
            int red = composite.getBackground().getRed();
            int green = composite.getBackground().getGreen();
            int blue = composite.getBackground().getBlue();
            chart.setBackgroundPaint(new Color(red, green, blue));
            chart.setBackgroundImageAlpha(0.0f);
        }

        XYPlot plot = (XYPlot) chart.getPlot();
        if (!isHasValidData) {
            setNoDataPlot(plot);
        } else {
            setDataPlot(plot);
            chart.getLegend().setBackgroundPaint(Color.white);
        }

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        for (int i = 0; i < seriesCount; i++) {
            renderer.setSeriesPaint(i, colorAr[i % colorAr.length]);
        }

        return chart;
    }

    public void refreshChart(List<StatisticData> statisticDataList) {
        this.statisticDataList = statisticDataList;
        if (chart == null) {
            return;
        }
        int oldDataStatus = isHasValidData ? 1 : 0;
        isHasValidData = false;
        ChartType oldType = chartType;
        XYDataset dataset = createDataset();
        XYPlot plot = chart.getXYPlot();
        int newDataStatus = isHasValidData ? 1 : 0;
        if (!isHasValidData) {
            if (oldDataStatus != newDataStatus) {
                setNoDataPlot(plot);
            }
            plot.setDataset(EMPTY_DATASET);
        } else {
            if (oldDataStatus != newDataStatus) {
                setDataPlot(plot);
            }
            if (chartType != oldType) {
                plot.getRangeAxis().setRange(rangMin, rangMax);
            }
            plot.setDataset(dataset);
        }
    }

    /**
     * When no valid data, set background picture to display the status.
     *
     * @param plot
     */
    private void setNoDataPlot(XYPlot plot) {
        if (plot == null) {
            return;
        }
        plot.setOutlineVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setBackgroundImageAlpha(0.5f);
        plot.setBackgroundImage(CommonUITool.getAWTImage(noDataImage.getImageData()));
    }

    /**
     * Set the plot as black background with no background picture, and has grid line.
     *
     * @param plot
     */
    private void setDataPlot(XYPlot plot) {
        if (plot == null) {
            return;
        }
        plot.setOutlineVisible(isDetailView);
        plot.getDomainAxis().setVisible(isDetailView);
        plot.getRangeAxis().setVisible(isDetailView);
        plot.getRangeAxis().setRange(rangMin, rangMax);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlineStroke(new BasicStroke(0.4f));
        plot.setRangeGridlineStroke(new BasicStroke(0.4f));
        plot.setDomainGridlinePaint(new Color(0, 128, 64));
        plot.setRangeGridlinePaint(new Color(0, 128, 64));
        plot.setBackgroundImageAlpha(0.0f);
        plot.setBackgroundPaint(Color.BLACK);
    }

    public void updateGroupName(String groupName) {
        if (groupName == null) {
            return;
        }
        this.groupName = groupName;
        if (chartGroup == null) {
            return;
        }
        chartGroup.setText(groupName);
    }

    public void updateToolbar(boolean isEditMode) {
        if (isDetailView) {
            return;
        }
        this.isEditMode = isEditMode;
        if (isEditMode) {
            addToolbar(chartGroup);
            btnsComp.moveAbove(frame);
        } else {
            btnsComp.dispose();
        }
        chartGroup.layout();
        composite.layout();
    }

    private void openEditStatisticItemDialog() {
        if (isMultiHost) {
            EditMultiHostStatisticItemDialog dialog =
                    new EditMultiHostStatisticItemDialog(editor.getSite().getShell(), editor);
            dialog.setNew(false);
            dialog.setStatisticChartItem((StatisticChartItem) statisticChartItem.clone());
            if (dialog.open() == Dialog.OK) {
                statisticChartItem = dialog.getStatisticChartItem();
                editor.updateStatisticItem(statisticChartItem);
            }
        } else {
            EditSingleHostStatisticItemDialog dialog =
                    new EditSingleHostStatisticItemDialog(
                            editor.getSite().getShell(), editor, editor.getServerInfo());
            dialog.setNew(false);
            dialog.setStatisticChartItem((StatisticChartItem) statisticChartItem.clone());
            if (dialog.open() == Dialog.OK) {
                statisticChartItem = dialog.getStatisticChartItem();
                editor.updateStatisticItem(statisticChartItem);
            }
        }
    }

    public List<StatisticData> getStatisticDataList() {
        return statisticDataList;
    }

    public void setStatisticDataList(List<StatisticData> statisticDataList) {
        this.statisticDataList = statisticDataList;
    }

    public Map<StatisticData, Double> getMaxValueMap() {
        return maxValueMap;
    }

    public Map<StatisticData, Double> getMinValueMap() {
        return minValueMap;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isDetailView() {
        return isDetailView;
    }

    public int getRangMax() {
        return rangMax;
    }

    public void setRangMax(int rangMax) {
        this.rangMax = rangMax;
    }

    public MonitorStatisticEditor getEditor() {
        return editor;
    }

    public void setEditor(MonitorStatisticEditor editor) {
        this.editor = editor;
    }

    public StatisticChartItem getStatisticChartItem() {
        return statisticChartItem;
    }

    public void setStatisticChartItem(StatisticChartItem statisticChartItem) {
        this.statisticChartItem = statisticChartItem;
    }

    public boolean getSelection() {
        return isSelected;
    }

    public void setSelection(boolean isSelected) {
        this.isSelected = isSelected;
        if (checkBtn != null) {
            checkBtn.setSelection(isSelected);
        }
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    private ChartType getChartType(String metric) { // FIXME extract
        ChartType type = null;
        if (StatisticParamUtil.isPercentageData(metric)) {
            type = ChartType.PERCENT;
        } else if (StatisticParamUtil.isMemoryData(metric)) {
            type = ChartType.MEMORY;
        } else if (StatisticParamUtil.isDiskData(metric)) {
            type = ChartType.SPACE;
        } else {
            type = ChartType.OTHERS;
        }
        return type;
    }

    private void decideRangMax(double max) { // FIXME extract
        switch (chartType) {
            case PERCENT:
                break;
            case MEMORY:
            case SPACE:
                int sizeInGb = (int) max / 1024;
                sizeInGb = (int) max % 1024 > 0 ? sizeInGb + 1 : sizeInGb;
                rangMax = sizeInGb * 1024;
                break;
            case OTHERS:
                int maxInInt = (int) max;
                int count = 0;
                while ((maxInInt /= 10) > 0) {
                    count++;
                }
                int temp = (int) Math.pow(10, count);
                int digit = maxInInt % temp > 0 ? maxInInt / temp + 1 : maxInInt / temp;
                rangMax = digit * temp;
                rangMax = rangMax > 10 ? rangMax : 10;
            default:
                break;
        }
    }
}
