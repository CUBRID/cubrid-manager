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
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.monitoring.editor.internal.ChartCompositePart;
import com.cubrid.cubridmanager.ui.monstatistic.editor.MonitorStatisticEditor;
import java.awt.Color;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.slf4j.Logger;

/**
 * Similar with MonitorStatisticChart, but use as Add button or hide element.
 *
 * @author Santiago Wang
 */
public class WhiteChart {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogUtil.getLogger(ChartCompositePart.class);

    private final String EMPTY_STRING = "";
    private final Composite composite;
    private final String ADD_ITEM_ICON_PATH = "icons/monitor/add_monitor_chart.png";
    private final String EMPTY_DATA_ICON_PATH = "icons/monitor/";
    private MonitorStatisticEditor editor;

    private String groupName = null;
    private String backgroundIconPath = null;
    private boolean isAddItemChart = false;
    private boolean isEmptyDataChart = false;

    public WhiteChart(Composite parent, MonitorStatisticEditor editor) {
        this.composite = parent;
        this.editor = editor;
    }

    public void loadChart() {
        ChartComposite frame;
        if (groupName != null) {
            final Group chartGroup = new Group(composite, SWT.RESIZE);
            chartGroup.setText(groupName);
            GridLayout chartGroupLayout = new GridLayout();
            chartGroupLayout.marginHeight = 6;
            chartGroupLayout.marginWidth = 6;
            chartGroup.setLayout(chartGroupLayout);
            chartGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
            frame =
                    new ChartComposite(
                            chartGroup, SWT.NONE, createChart(), false, false, false, false, false);
        } else {
            frame =
                    new ChartComposite(
                            composite, SWT.NONE, createChart(), false, false, false, false, false);
        }

        GridData gdFrame = new GridData(SWT.FILL, SWT.FILL, true, true);
        frame.setLayoutData(gdFrame);
        frame.setLayout(new FillLayout());
        frame.setDomainZoomable(false);
        frame.setRangeZoomable(false);

        frame.addChartMouseListener(
                new ChartMouseListener() {

                    public void chartMouseMoved(ChartMouseEvent event) {}

                    public void chartMouseClicked(ChartMouseEvent event) {
                        if (event.getTrigger().getButton() == 1) {
                            editor.openEditStatisticItemDialog();
                        }
                    }
                });
    }

    public JFreeChart createChart() {
        final Color backGroundColor = Color.WHITE;
        JFreeChart chart =
                ChartFactory.createTimeSeriesChart(
                        EMPTY_STRING,
                        EMPTY_STRING,
                        EMPTY_STRING,
                        new TimeSeriesCollection(),
                        false,
                        false,
                        false);
        chart.setBackgroundImageAlpha(0.0f);
        chart.setBackgroundPaint(backGroundColor);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setOutlineVisible(false);
        if (backgroundIconPath != null) {
            plot.setBackgroundImage(
                    CommonUITool.getAWTImage(
                            CubridManagerUIPlugin.getImage(backgroundIconPath).getImageData()));
        }

        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);
        return chart;
    }

    public String getBackgroundIconPath() {
        return backgroundIconPath;
    }

    public void setBackgroundIconPath(String backgroundIconPath) {
        this.backgroundIconPath = backgroundIconPath;
    }

    public boolean isAddItemChart() {
        return isAddItemChart;
    }

    public void setAddItemChart(boolean isAddItemChart) {
        this.isAddItemChart = isAddItemChart;
        if (isAddItemChart) {
            backgroundIconPath = ADD_ITEM_ICON_PATH;
        }
    }

    public boolean isEmptyDataChart() {
        return isEmptyDataChart;
    }

    public void setEmptyDataChart(boolean isEmptyDataChart) {
        this.isEmptyDataChart = isEmptyDataChart;
        if (isEmptyDataChart) {
            backgroundIconPath = EMPTY_DATA_ICON_PATH;
        }
    }
}
