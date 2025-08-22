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

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.Result;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;

/**
 * A combined chart includes the bar chart and time series.Generally, the bar chart is responsible
 * for showing the current value changing,the time series is responsible for showing the history
 * value changing with time elapse.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-6-6 created by lizhiqiang
 */
public class CombinedBarTimeSeriesChart {
    private static final Logger LOGGER = LogUtil.getLogger(CombinedBarTimeSeriesChart.class);
    private TreeMap<String, String> valueMap;
    private TreeMap<String, TimeSeries> seriesMap;
    private JFreeChart barChart;
    private JFreeChart seriesChart;
    private DefaultCategoryDataset bardataset;
    private Axis categoryAxis;

    private String barGroupName = "";
    private String seriesGroupName = "";
    private double barMax = 100.0D;
    private NumberAxis numberaxis;
    private boolean isAreaRender;
    private XYDataset seriesdataset;
    private Composite basicComposite;
    private TimeTableXYDataset timeTableXYCollection;
    private static final int SWITCH_VALUE = 60 * 10;
    private boolean hasBarChart = true;
    private boolean isShowSeriesAxis;

    /**
     * Load the chart into the given composite
     *
     * @param parent the given composite
     * @return the instance of composite
     */
    public Composite load(Composite parent) {
        basicComposite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        basicComposite.setLayout(layout);
        basicComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        if (hasBarChart) {
            loadBarChart(basicComposite);
        }
        loadSeriesChart(basicComposite);
        return parent;
    }

    /**
     * Load the bar chart into the given composite
     *
     * @param parent the given composite
     * @return the instance of composite
     */
    private Composite loadBarChart(Composite parent) {
        Group barGroup = new Group(parent, SWT.NONE);
        barGroup.setText(barGroupName);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 6;
        layout.marginWidth = 6;
        barGroup.setLayout(layout);
        barGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        barChart = createBarChart();
        barChart.setBackgroundPaint(Color.BLACK);
        ChartComposite chartComposite =
                new ChartComposite(barGroup, SWT.NONE, barChart, false, true, false, false, false);
        chartComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        FillLayout chartLayout = new FillLayout();
        chartLayout.marginHeight = 0;
        chartLayout.marginWidth = 0;
        chartComposite.setLayout(chartLayout);

        return parent;
    }

    /**
     * Load the series chart into the given composite
     *
     * @param parent the given composite
     * @return the instance of composite
     */
    private Composite loadSeriesChart(Composite parent) {
        Group seriesGroup = new Group(parent, SWT.NONE);
        seriesGroup.setText(seriesGroupName);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        seriesGroup.setLayout(layout);
        seriesGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        seriesChart = createSeriesChart();
        int red = seriesGroup.getBackground().getRed();
        int green = seriesGroup.getBackground().getGreen();
        int blue = seriesGroup.getBackground().getBlue();
        seriesChart.setBackgroundPaint(new Color(red, green, blue));
        ChartComposite chartComposite =
                new ChartComposite(
                        seriesGroup, SWT.NONE, seriesChart, false, true, false, false, false);
        FillLayout chartLayout = new FillLayout();
        chartLayout.marginHeight = 0;
        chartLayout.marginWidth = 0;
        chartComposite.setLayout(chartLayout);
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        return parent;
    }

    /**
     * Create a bar chart instance.
     *
     * @return a bar chart instance
     */
    private JFreeChart createBarChart() {
        bardataset = new DefaultCategoryDataset();
        if (valueMap != null) {
            for (String key : valueMap.keySet()) {
                bardataset.addValue(0D, key, "");
            }
        }
        bardataset.addValue(barMax, "100", "");

        JFreeChart chart =
                ChartFactory.createStackedBarChart(
                        "", "", "", bardataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBorderVisible(false);
        chart.setBorderStroke(new BasicStroke(0.0f));
        chart.setBackgroundPaint(Color.BLACK);
        // plot
        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        categoryplot.setOutlineVisible(false);
        RectangleInsets rectangleInsets = new RectangleInsets();
        categoryplot.setAxisOffset(rectangleInsets);

        categoryplot.setBackgroundPaint(Color.BLACK);
        categoryplot.setDomainGridlinesVisible(false);
        categoryplot.setRangeGridlinesVisible(false);
        // renderer
        StackedBarRenderer stackedbarrenderer = (StackedBarRenderer) categoryplot.getRenderer(0);
        stackedbarrenderer.setDrawBarOutline(false);
        stackedbarrenderer.setMaximumBarWidth(0.6);
        stackedbarrenderer.setItemMargin(0);
        // painter
        StandardBarPainter painter =
                new StandardBarPainter() {

                    private static final long serialVersionUID = -3124115075260902181L;

                    public void paintBar(
                            Graphics2D g2,
                            BarRenderer renderer,
                            int row,
                            int column,
                            RectangularShape bar,
                            RectangleEdge base) {
                        Paint itemPaint = renderer.getItemPaint(row, column);
                        GradientPaintTransformer t = renderer.getGradientPaintTransformer();
                        if (t != null && itemPaint instanceof GradientPaint) {
                            itemPaint = t.transform((GradientPaint) itemPaint, bar);
                        }
                        g2.setPaint(itemPaint);
                        double height = bar.getHeight();
                        double width = bar.getWidth();
                        double x = bar.getBounds2D().getX();
                        double y = bar.getBounds2D().getY();
                        int barNumber = (int) (height / 2 + 0.5);
                        if (height < 1 && height > 0.5) {
                            barNumber = 1;
                        }

                        for (int i = 0; i < barNumber; i++) {
                            RectangularShape subBarLeft =
                                    new Rectangle2D.Double(x, y, width / 2, 0.8);
                            g2.fill(subBarLeft);
                            RectangularShape subBarRight =
                                    new Rectangle2D.Double(x + width / 2 + 1, y, width / 2, 0.8);
                            g2.fill(subBarRight);
                            y += 2;
                        }

                        if (renderer.isDrawBarOutline()) {
                            Stroke stroke = renderer.getItemOutlineStroke(row, column);
                            Paint paint = renderer.getItemOutlinePaint(row, column);
                            if (stroke != null && paint != null) {
                                g2.setStroke(stroke);
                                g2.setPaint(paint);
                                g2.draw(bar);
                            }
                        }
                    }
                };
        stackedbarrenderer.setBarPainter(painter);
        stackedbarrenderer.setSeriesPaint(0, Color.GREEN);
        stackedbarrenderer.setSeriesPaint(1, Color.RED);
        int backPaintOrder = 1;
        if (valueMap != null) {
            backPaintOrder = valueMap.size();
        }
        stackedbarrenderer.setSeriesPaint(backPaintOrder, new Color(136, 200, 135));

        stackedbarrenderer.setShadowVisible(false);
        stackedbarrenderer.setDrawBarOutline(false);

        // categoryAxis
        categoryAxis = categoryplot.getDomainAxis();
        categoryAxis.setAxisLineVisible(false);
        //	categoryAxis.setCategoryMargin(0);
        categoryAxis.setMinorTickMarksVisible(false);
        categoryAxis.setTickLabelsVisible(false);
        categoryAxis.setTickMarksVisible(false);
        categoryAxis.setLabelPaint(Color.GREEN);
        categoryAxis.setLabelFont(new Font("", 0, 10));

        // valueAxis
        ValueAxis valueAxis = categoryplot.getRangeAxis();

        valueAxis.setVisible(false);
        return chart;
    }

    /**
     * Create the series chart
     *
     * @return an instance of series chart
     */
    private JFreeChart createSeriesChart() {
        if (isAreaRender) {
            seriesdataset = createTableSeriesDataset();
        } else {
            seriesdataset = createTimeSeriesDataset();
        }
        JFreeChart chart =
                ChartFactory.createTimeSeriesChart("", "", "", seriesdataset, false, false, false);
        chart.setBorderVisible(false);
        chart.setBorderStroke(new BasicStroke(0.0f));
        // plot
        XYPlot xyplot = (XYPlot) chart.getPlot();
        xyplot.setOutlineVisible(false);
        RectangleInsets rectangleInsets = new RectangleInsets();
        xyplot.setAxisOffset(rectangleInsets);

        xyplot.setDomainGridlineStroke(new BasicStroke(0.4f));
        xyplot.setRangeGridlineStroke(new BasicStroke(0.4f));

        xyplot.setBackgroundPaint(Color.BLACK);
        xyplot.setDomainGridlinePaint(new Color(0, 128, 64));
        xyplot.setRangeGridlinePaint(new Color(0, 128, 64));

        if (isAreaRender) {
            XYAreaRenderer2 render = new StackedXYAreaRenderer2();
            render.setSeriesPaint(0, Color.GREEN);
            render.setSeriesPaint(1, Color.RED);
            xyplot.setRenderer(render);

        } else {
            XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) xyplot.getRenderer();
            render.setSeriesPaint(0, Color.GREEN);
            render.setSeriesPaint(1, Color.RED);
        }

        // dateAxis
        DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
        dateaxis.setFixedAutoRange(300000d);
        dateaxis.setLowerMargin(0.0D);
        dateaxis.setUpperMargin(0.0D);
        dateaxis.setVisible(isShowSeriesAxis);

        numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setVisible(isShowSeriesAxis);
        numberaxis.setRange(0 - 0.5, barMax + 0.5);

        return chart;
    }

    /**
     * Creates a table dataset for series
     *
     * @return Series 2.
     */
    private XYDataset createTableSeriesDataset() {
        timeTableXYCollection = new TimeTableXYDataset();
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            timeTableXYCollection.add(new Second(), 0, key);
        }
        return timeTableXYCollection;
    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 2.
     */
    private XYDataset createTimeSeriesDataset() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        seriesMap = new TreeMap<String, TimeSeries>();
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            TimeSeries series = new TimeSeries(key);
            seriesMap.put(key, series);
            timeseriescollection.addSeries(series);
        }
        return timeseriescollection;
    }

    /**
     * Update the value based on the new value
     *
     * @param valueMap the valueMap to set
     */
    public void updateValueMap(TreeMap<String, String> valueMap) {
        String barValueStr = "%";
        updateValueMap(valueMap, barValueStr);
    }

    /**
     * Update the value based on the new value
     *
     * @param valueMap the valueMap to set
     * @param barLabel the label text in the bar chart
     */
    public void updateValueMap(TreeMap<String, String> valueMap, String barLabel) {
        if (isAreaRender) {
            updateValueMapIfArea(valueMap, barLabel);
        } else {
            updateValueMapIfSeries(valueMap, barLabel);
        }
    }

    /**
     * Update the value based on the new value if series chart uses the area renderer
     *
     * @param valueMap the valueMap to set
     * @param barLabel the label text in the bar chart
     */
    private void updateValueMapIfArea(TreeMap<String, String> valueMap, String barLabel) {
        this.valueMap = valueMap;
        bardataset.clear();
        double allValue = 0;
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            if (value != null) {
                double newValue = Double.parseDouble(value);
                bardataset.addValue(newValue, key, "");
                ((TimeTableXYDataset) seriesdataset).add(new Second(), newValue, key);
                allValue += newValue;
            }
        }
        if (allValue > barMax) {
            barMax = allValue;
        }
        bardataset.addValue(barMax - allValue, "100", "");
        numberaxis.setRange(0 - 1, barMax + barMax / 100);
        String label = barLabel;
        if ("%".equals(label)) {
            label = Integer.toString((int) (allValue + 0.5)) + label;
        }
        categoryAxis.setLabel(label);
    }

    /**
     * Update the value based on the new value if series chart uses series renderer
     *
     * @param valueMap the valueMap to set
     * @param barLabel the label text in the bar chart
     */
    private void updateValueMapIfSeries(TreeMap<String, String> valueMap, String barLabel) {
        this.valueMap = valueMap;
        bardataset.clear();
        double allValue = 0;
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                double newValue = Double.parseDouble(value);
                bardataset.addValue(newValue, entry.getKey(), "");
                allValue += newValue;
            }
        }
        if (allValue > barMax) {
            barMax = allValue;
        }
        bardataset.addValue(barMax - allValue, "100", "");
        numberaxis.setRange(0 - 1, barMax + barMax / 100);
        String label = barLabel;
        if ("%".equals(label)) {
            label = Integer.toString((int) (allValue + 0.5)) + label;
        }
        categoryAxis.setLabel(label);

        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                double newValue = Double.parseDouble(value);
                seriesMap.get(entry.getKey()).addOrUpdate(new Second(), newValue);
            }
        }
    }

    /**
     * Get the valueMap
     *
     * @return the valueMap
     */
    public TreeMap<String, String> getValueMap() {
        return valueMap;
    }

    /** @param valueMap the valueMap to set */
    public void setValueMap(TreeMap<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    /**
     * Get the bar chart
     *
     * @return the barChart
     */
    public JFreeChart getBarChart() {
        return barChart;
    }

    /**
     * Get the series chart
     *
     * @return the seriesChart
     */
    public JFreeChart getSeriesChart() {
        return seriesChart;
    }

    /** @param barGroupName the barGroupName to set */
    public void setBarGroupName(String barGroupName) {
        this.barGroupName = barGroupName;
    }

    /** @param seriesGroupName the seriesGroupName to set */
    public void setSeriesGroupName(String seriesGroupName) {
        this.seriesGroupName = seriesGroupName;
    }

    /** update series chart from scratch */
    public void updateFromScratch() {
        if (isAreaRender) {
            ((TimeTableXYDataset) seriesdataset).clear();
        } else {
            ((TimeSeriesCollection) seriesdataset).removeAllSeries();
            if (seriesMap != null) {
                seriesMap.clear();
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    String key = entry.getKey();
                    TimeSeries series = new TimeSeries(key);
                    seriesMap.put(key, series);
                    ((TimeSeriesCollection) seriesdataset).addSeries(series);
                }
            }
        }
    }

    /**
     * Execute the stuffedChart method using busy cursor
     *
     * @param file the instance of CounterFile, which includes data info
     * @param types a array which includes one or more data type
     * @param beginTime the begin time
     * @param endTime the ending time
     * @param maxType the type name representative of the proportion of the given types.
     */
    public void executeQueryWithBusyCursor(
            final CounterFile file,
            final List<String> types,
            final long beginTime,
            final long endTime,
            final String maxType) {
        BusyIndicator.showWhile(
                Display.getDefault(),
                new Runnable() {
                    public void run() {
                        Display.getDefault()
                                .syncExec(
                                        new Runnable() {
                                            public void run() {
                                                stuffedChart(
                                                        file, types, beginTime, endTime, maxType);
                                            }
                                        });
                    }
                });
    }

    /**
     * Execute the stuffedChart method using busy cursor
     *
     * @param file the instance of CounterFile, which includes data info
     * @param types a array which includes one or more data type
     * @param beginTime the begin time
     * @param endTime the ending time
     */
    public void executeQueryWithBusyCursor(
            final CounterFile file,
            final List<String> types,
            final long beginTime,
            final long endTime) {
        executeQueryWithBusyCursor(file, types, beginTime, endTime, null);
    }

    /**
     * This method provides data from local fill for History chart showing
     *
     * @param file the instance of CounterFile, which includes data info
     * @param types a array which includes one or more data type
     * @param beginTime the begin time
     * @param endTime the ending time
     * @param maxType the type name representative of the proportion of the given types.
     */
    private void stuffedChart(
            CounterFile file, List<String> types, long beginTime, long endTime, String maxType) {
        List<String> fileTypes = new ArrayList<String>();
        for (String type : types) {
            fileTypes.add(type);
        }
        if (maxType != null) {
            fileTypes.add(maxType);
        }
        long distance = endTime - beginTime;
        int interval = file.getInterval() * 1000; // in milliseconds
        long quotient = distance / interval;
        if (distance % interval != 0) {
            quotient++;
        }
        int factor = (int) (quotient / SWITCH_VALUE);
        if (quotient % SWITCH_VALUE != 0) {
            factor++;
        }
        Result[] results = new Result[(int) (quotient / factor + 1L)];
        int length = 0;
        for (long time = beginTime; time < endTime; time += (interval * factor)) {
            Result res;
            try {
                res = file.readData(time, fileTypes.toArray(new String[fileTypes.size()]));
                results[length] = res;
                for (String type : types) {
                    if (isAreaRender) {
                        double result = results[length].getAvgAsDouble(type);
                        timeTableXYCollection.add(new Second(new Date(time)), result, type);
                    } else {
                        double result = results[length].getAvgAsDouble(type);
                        seriesMap.get(type).addOrUpdate(new Second(new Date(time)), result);
                        if (maxType == null) {
                            if (barMax < result) {
                                barMax = result;
                                numberaxis.setRange(0 - 1, barMax + 1);
                                numberaxis.setVisible(isShowSeriesAxis);
                            }
                        } else {
                            double maxPercent = results[length].getAvgAsDouble(maxType);
                            double max = maxPercent <= 0 ? barMax : result / maxPercent * 10000;
                            if (barMax < max) {
                                barMax = max;
                                numberaxis.setRange(0 - 1, barMax + 1);
                                numberaxis.setVisible(isShowSeriesAxis);
                            }
                        }
                    }
                }

            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
            length++;
        }
    }

    /** @param isAreaRender the isAreaRender to set */
    public void setAreaRender(boolean isAreaRender) {
        this.isAreaRender = isAreaRender;
    }

    /**
     * Get the basicComposite
     *
     * @return the basicComposite
     */
    public Composite getBasicComposite() {
        return basicComposite;
    }

    /** @param hasBarChart the hasBarChart to set */
    public void setHasBarChart(boolean hasBarChart) {
        this.hasBarChart = hasBarChart;
    }

    /** @param isShowSeriesAxis the isShowSeriesAxis to set */
    public void setShowSeriesAxis(boolean isShowSeriesAxis) {
        this.isShowSeriesAxis = isShowSeriesAxis;
    }
}
