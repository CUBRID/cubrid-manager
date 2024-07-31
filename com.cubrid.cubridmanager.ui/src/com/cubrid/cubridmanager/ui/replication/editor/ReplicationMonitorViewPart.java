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
package com.cubrid.cubridmanager.ui.replication.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationInfo;
import com.cubrid.cubridmanager.core.replication.model.ReplicationParamConstants;
import com.cubrid.cubridmanager.core.replication.task.GetReplPerformanceTask;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;

/**
 * Monitor replication performance UI
 *
 * @author pangqiren
 * @version 1.0 - 2009-9-7 created by pangqiren
 */
public class ReplicationMonitorViewPart extends CubridViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(ReplicationMonitorViewPart.class);
    public static final String ID =
            "com.cubrid.cubridmanager.ui.replication.editor.ReplicationMonitorViewPart";
    private TimeSeries replTimeSeries;
    private int startRun = 0;
    private int term = 1;
    private Composite composite;
    private boolean runflag = true;
    private ReplicationInfo replInfo;
    private ServerInfo serverInfo;

    /**
     * @see com.cubrid.common.ui.spi.part.CubridViewPart#init(org.eclipse.ui.IViewSite)
     * @param site the view site
     * @exception PartInitException if this view was not initialized successfully
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        serverInfo = getCubridNode().getServer().getServerInfo();
        replInfo = (ReplicationInfo) getCubridNode().getAdapter(ReplicationInfo.class);
        if (replInfo != null
                && replInfo.getSlaveList() != null
                && replInfo.getSlaveList().size() > 0
                && replInfo.getSlaveList().get(0).getParamInfo() != null) {
            String samplingTerm =
                    replInfo.getSlaveList()
                            .get(0)
                            .getParamInfo()
                            .getParamValue(ReplicationParamConstants.PERF_POLL_INTERVAL);
            term = Integer.parseInt(samplingTerm);
        }
    }

    /**
     * Create the part
     *
     * @param parent the parent composite
     */
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.RESIZE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createPlotTableUnit(composite);
        new DataGenerator().start();
    }

    /**
     * Creates the plot table unit
     *
     * @param composite Composite
     */
    private void createPlotTableUnit(Composite composite) {
        final JFreeChart chart = createChart();
        ChartComposite chartFrame =
                new ChartComposite(composite, SWT.NONE, chart, false, true, false, true, true);
        GridData gdFrame = new GridData(SWT.FILL, SWT.FILL, true, true);
        chartFrame.setLayoutData(gdFrame);
        chartFrame.setLayout(new FillLayout());
    }

    /**
     * Create chart unit
     *
     * @return chart
     */
    private JFreeChart createChart() {
        // create data set
        replTimeSeries = new TimeSeries(Messages.msgDelayValue + "    ");
        replTimeSeries.setMaximumItemAge(2592000);

        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(replTimeSeries);

        // create X axis
        DateAxis dateaxis = new DateAxis(Messages.msgSlaveTimes);
        dateaxis.setTickLabelFont(new Font("SansSerif", 0, 10));
        dateaxis.setLabelFont(new Font("SansSerif", 0, 7));
        dateaxis.setFixedAutoRange(300000d);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setTickLabelsVisible(true);

        // create Y axis
        NumberAxis numberaxis = new NumberAxis(Messages.msgDelayValue);
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // create display model
        XYLineAndShapeRenderer xylineandshaperenderer = new XYLineAndShapeRenderer(true, true);
        xylineandshaperenderer.setSeriesPaint(0, new Color(146, 208, 80));
        xylineandshaperenderer.setSeriesStroke(0, new BasicStroke(2F, 0, 2));

        XYPlot xyplot =
                new XYPlot(timeseriescollection, dateaxis, numberaxis, xylineandshaperenderer);
        // set backcolor of grid
        xyplot.setBackgroundPaint(Color.BLACK);
        // set vertical line color of grid
        xyplot.setDomainGridlinePaint(new Color(130, 130, 130));
        // set horizontal line color of grid
        xyplot.setRangeGridlinePaint(new Color(130, 130, 130));
        xyplot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 10D));

        JFreeChart chart =
                new JFreeChart(
                        Messages.titlePerformancePart, new Font("SansSerif", 1, 15), xyplot, true);

        return chart;
    }

    /**
     * A inner class that update the data of chart in a single thread
     *
     * @author pangqiren
     * @version 1.0 - 2009-9-7 created by pangqiren
     */
    class DataGenerator extends Thread {
        private List<Map<String, String>> updateData;
        private int count;

        /** @see java.lang.Thread#run() */
        public void run() {
            while (getRunflag()) {
                try {
                    updateData = getUpdateValue();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                if (startRun <= 1) {
                    startRun++;
                } else {
                    if (count % term == 0) {
                        Display.getDefault()
                                .asyncExec(
                                        new Runnable() {
                                            public void run() {
                                                if (composite != null && !composite.isDisposed()) {
                                                    update(updateData);
                                                }
                                            }
                                        });
                        count = 0;
                    }
                    count++;
                }
            }
        }
    }

    /**
     * get update value
     *
     * @return task.loadPerformanceData();
     */
    private List<Map<String, String>> getUpdateValue() {
        GetReplPerformanceTask task = new GetReplPerformanceTask(serverInfo);
        task.setFilePath(
                replInfo.getDistInfo().getDistDbPath()
                        + serverInfo.getPathSeparator()
                        + replInfo.getDistInfo().getDistDbName()
                        + ".perf");
        task.execute();
        if (!task.isSuccess()) {
            return null;
        }
        return task.loadPerformanceData();
    }

    /**
     * Update the data of chart
     *
     * @param updatedData List<Map<String, String>>
     */
    private void update(List<Map<String, String>> updatedData) {
        if (updatedData == null) {
            return;
        }
        for (int i = 0; i < updatedData.size(); i++) {
            Map<String, String> data = updatedData.get(i);
            String slaveTime = data.get("slave_time");
            if (slaveTime == null) {
                continue;
            }
            String value = data.get("delay");
            if (value == null || !value.matches("\\d+")) {
                continue;
            }
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
            try {
                Date date = df.parse(slaveTime);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Second sec =
                        new Second(
                                cal.get(Calendar.SECOND),
                                cal.get(Calendar.MINUTE),
                                cal.get(Calendar.HOUR),
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.YEAR));
                replTimeSeries.addOrUpdate(sec, Integer.parseInt(value));
            } catch (ParseException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /** @see com.cubrid.common.ui.spi.part.CubridViewPart#setFocus() */
    public void setFocus() {
        if (null != cubridNode) {
            LayoutManager.getInstance()
                    .getTitleLineContrItem()
                    .changeTitleForViewOrEditPart(cubridNode, this);
            LayoutManager.getInstance()
                    .getStatusLineContrItem()
                    .changeStuatusLineForViewOrEditPart(cubridNode, this);
        }
    }

    /**
     * Gets the value of runflag
     *
     * @return boolean
     */
    public boolean getRunflag() {
        synchronized (this) {
            return runflag;
        }
    }

    /** Dispose this view when it closed */
    public void dispose() {
        synchronized (this) {
            runflag = false;
            super.dispose();
        }
    }

    /**
     * Responses when the node changes
     *
     * @param event CubridNodeChangedEvent
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        // empty
    }
}
