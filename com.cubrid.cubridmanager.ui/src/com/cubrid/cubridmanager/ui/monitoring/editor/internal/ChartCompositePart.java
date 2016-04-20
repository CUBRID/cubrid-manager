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

import static com.cubrid.common.ui.spi.util.CommonUITool.getColorElem;
import static com.cubrid.common.ui.spi.util.CommonUITool.trimPaintColor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.monitoring.model.IDiagPara;
import com.cubrid.cubridmanager.ui.monitoring.Messages;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.BasicCounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterFile;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.CounterType;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.RangeType;
import com.cubrid.cubridmanager.ui.monitoring.editor.count.Result;

/**
 * This type provides for other type the chart unit to show data changing.
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-24 created by lizhiqiang
 */
public class ChartCompositePart {
	private static final Logger LOGGER = LogUtil.getLogger(ChartCompositePart.class);
	private final Composite composite;
	private JFreeChart chart;
	private String chartTitle = "";

	protected TreeMap<String, String> valueMap;
	protected TreeMap<String, ShowSetting> settingMap;
	protected TreeMap<String, TimeSeries> seriesMap;
	private ColorFieldEditor colorField;
	private TableViewer seriesTableViewer;
	private Combo combo;
	private Button checkBtn;
	private TimeSeriesCollection timeseriescollection;
	private XYLineAndShapeRenderer xylineandshaperenderer;
	private boolean isShowSeriesList = false;
	// private CounterFile countFile;
	// title
	private String ttlBgColor;
	private StatusMonInstanceData monInstaceData;
	// history path
	private String historyPath;
	private ChartComposite chartComposite;
	private boolean isChangedHistoryPath;
	private CounterFile countFile;
	private String historyFileName;
	private static final int SWITCH_VALUE = 60 * 10;

	public ChartCompositePart(Composite parent, TreeMap<String, String> map) {
		//	composite = new Composite(parent, SWT.NONE);
		composite = parent;
		valueMap = map;
		settingMap = new TreeMap<String, ShowSetting>();

		for (Map.Entry<String, String> entry : valueMap.entrySet()) {
			ShowSetting showSetting = new ShowSetting();
			showSetting.setSeriesRgb(new RGB(0, 255, 0));
			settingMap.put(entry.getKey(), showSetting);
		}

	}

	/**
	 * Load the content into the part
	 *
	 */
	public void loadContent() {
		if (null != monInstaceData) {
			settingMap = monInstaceData.getSettingMap();
		}
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createPlotUnit(composite);
		createSpecialUnit(composite);
		if (isShowSeriesList) {
			createSeriesList(composite);
		}
		createSeriesTable(composite);
	}

	/**
	 * This method aims to sub type can create a content for special need.The
	 * default behavior is to do nothing.
	 *
	 * @param composite2 the parent composite
	 */
	protected void createSpecialUnit(Composite composite2) {
		// empty
	}

	/**
	 * Create the sub composite of Series selection combo and its related
	 * properties such as check and color
	 *
	 * @param composite the parent composite
	 */
	private void createSeriesList(Composite composite) {
		Composite comp = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		combo = new Combo(comp, SWT.DROP_DOWN);
		for (Map.Entry<String, String> entry : valueMap.entrySet()) {
			combo.add(entry.getKey());
		}
		combo.setLayout(new GridLayout());
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

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
			 * @param event an event containing information about the default
			 *        selection
			 */
			public void widgetDefaultSelected(SelectionEvent event) {
				String selectedItem = combo.getItem(combo.getSelectionIndex());
				boolean checked = settingMap.get(selectedItem).isChecked();
				RGB rgb = settingMap.get(selectedItem).getSeriesRgb();
				checkBtn.setSelection(checked);
				colorField.getColorSelector().setColorValue(rgb);
			}

		});

		checkBtn = new Button(comp, SWT.CHECK);
		checkBtn.setText(Messages.seriesSelectCheckBtn);
		checkBtn.addSelectionListener(new SelectionAdapter() {
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
			 * @param event an event containing information about the default
			 *        selection
			 */
			public void widgetDefaultSelected(SelectionEvent event) {
				handleUpdateSettingChange();
			}
		});

		Composite selComp = new Composite(comp, SWT.NONE);
		colorField = new ColorFieldEditor(Messages.seriesSelectColorBtnName,
				Messages.seriesSelectColorBtnLbl, selComp);
		colorField.getColorSelector().addListener(new IPropertyChangeListener() {
			/**
			 * Notification that a property has changed.
			 *
			 * @param event the property change event object describing which
			 *        property changed and how
			 */
			public void propertyChange(PropertyChangeEvent event) {
				handleUpdateSettingChange();
			}

		});

	}

	/**
	 * Create plot unit which show the data changing.
	 *
	 * @param parent the parent composite
	 */
	public void createPlotUnit(Composite parent) {
		chart = createChart();
		int red = parent.getBackground().getRed();
		int green = parent.getBackground().getGreen();
		int blue = parent.getBackground().getBlue();
		chart.setBackgroundPaint(new Color(red, green, blue));
		chartComposite = new ChartComposite(parent, SWT.NONE, chart, false, true, false, false,
				false);

		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 0;
		fillLayout.marginWidth = 0;
		chartComposite.setLayout(fillLayout);
		chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (null != monInstaceData) {
			TextTitle title = chart.getTitle();
			XYPlot plot = chart.getXYPlot();

			// title
			String ttlBgColor = monInstaceData.getTitleBgColor();
			String ttlFontName = monInstaceData.getTitleFontName();
			int ttlFontSize = monInstaceData.getTitleFontSize();
			String ttlFontColor = monInstaceData.getTitleFontColor();
			// plot
			String plotBgColor = monInstaceData.getPlotBgColor();
			String plotDomainGridColor = monInstaceData.getPlotDomainGridColor();
			String plotRangGridColor = monInstaceData.getPlotRangGridColor();
			String plotDateAxisColor = monInstaceData.getPlotDateAxisColor();
			String plotNumberAxisColor = monInstaceData.getPlotNumberAxisColor();
			// history path
			historyPath = monInstaceData.getHistoryPath();
			isChangedHistoryPath = true;

			if (ttlBgColor != null) {
				red = getColorElem(ttlBgColor, 0);
				green = getColorElem(ttlBgColor, 1);
				blue = getColorElem(ttlBgColor, 2);
				title.setBackgroundPaint(new Color(red, green, blue));
			}
			if (!"".equals(ttlFontName)) {
				Font titleFont = new Font(ttlFontName, 0, ttlFontSize);
				title.setFont(titleFont);
			}

			red = getColorElem(ttlFontColor, 0);
			green = getColorElem(ttlFontColor, 1);
			blue = getColorElem(ttlFontColor, 2);
			title.setPaint(new Color(red, green, blue));

			red = getColorElem(plotBgColor, 0);
			green = getColorElem(plotBgColor, 1);
			blue = getColorElem(plotBgColor, 2);
			plot.setBackgroundPaint(new Color(red, green, blue));

			red = getColorElem(plotDomainGridColor, 0);
			green = getColorElem(plotDomainGridColor, 1);
			blue = getColorElem(plotDomainGridColor, 2);
			plot.setDomainGridlinePaint(new Color(red, green, blue));

			red = getColorElem(plotRangGridColor, 0);
			green = getColorElem(plotRangGridColor, 1);
			blue = getColorElem(plotRangGridColor, 2);
			plot.setRangeGridlinePaint(new Color(red, green, blue));

			red = getColorElem(plotDateAxisColor, 0);
			green = getColorElem(plotDateAxisColor, 1);
			blue = getColorElem(plotDateAxisColor, 2);
			plot.getRangeAxis().setAxisLinePaint(new Color(red, green, blue));

			red = getColorElem(plotNumberAxisColor, 0);
			green = getColorElem(plotNumberAxisColor, 1);
			blue = getColorElem(plotNumberAxisColor, 2);
			plot.getDomainAxis().setAxisLinePaint(new Color(red, green, blue));

		}
	}

	/**
	 *
	 */
	public void addChartMouseListener() {
		chartComposite.addChartMouseListener(new ChartMouseListener() {

			public void chartMouseClicked(ChartMouseEvent event) {
				// do nothing
			}

			@SuppressWarnings("unchecked")
			public void chartMouseMoved(ChartMouseEvent event) {
				chartComposite.setHorizontalAxisTrace(true);
				XYPlot plot = (XYPlot) (event.getChart().getPlot());
				int xPos = event.getTrigger().getX();

				ValueAxis avalueAxis = plot.getDomainAxis();
				ChartRenderingInfo chartRenderingInfo = chartComposite.getChartRenderingInfo();
				Rectangle2D rectangle2D = chartRenderingInfo.getPlotInfo().getDataArea();
				RectangleEdge rectangleEdge1 = plot.getDomainAxisEdge();
				double time = avalueAxis.java2DToValue(xPos, rectangle2D, rectangleEdge1);

				Second second = new Second(new Date((long) time));
				List<String> tempLst = new ArrayList<String>();
				for (Map.Entry<String, String> entry : valueMap.entrySet()) {
					String item = entry.getKey();
					List<TimeSeries> list = timeseriescollection.getSeries();
					boolean found = false;
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getKey().equals(item)) {
							TimeSeriesDataItem dataItem = list.get(i).getDataItem(second);
							if (dataItem == null) {
								continue;
							}
							int intVal = dataItem.getValue().intValue();
							tempLst.add(String.valueOf(intVal));
							found = true;
							break;
						}
					}
					if (!found) {
						tempLst.add("0");
					}
				}
				String[] arrays = tempLst.toArray(new String[tempLst.size()]);
				String[] arrays2 = new String[arrays.length + 1];
				arrays2[0] = "";
				System.arraycopy(arrays, 0, arrays2, 1, arrays.length);
				seriesTableViewer.getTable().getItem(0).setText(arrays2);
			}

		});
	}

	/**
	 * Create the chart
	 *
	 * @return the instance of JFreeChart
	 */
	private JFreeChart createChart() {
		timeseriescollection = new TimeSeriesCollection();
		seriesMap = new TreeMap<String, TimeSeries>();
		xylineandshaperenderer = new XYLineAndShapeRenderer(true, false);
		int number = 0;

		for (Map.Entry<String, String> entry : valueMap.entrySet()) {
			String key = entry.getKey();
			TimeSeries series = new TimeSeries(key);

			seriesMap.put(key, series);
			if (settingMap.get(key).isChecked()) {
				timeseriescollection.addSeries(series);

				RGB seriesRgb = settingMap.get(key).getSeriesRgb();
				float width = settingMap.get(key).getWidth();
				Color color = new Color(seriesRgb.red, seriesRgb.green, seriesRgb.blue);
				xylineandshaperenderer.setSeriesPaint(number, color);
				xylineandshaperenderer.setSeriesStroke(number, new BasicStroke(width, 0, 2));
				number++;
			}
		}

		DateAxis dateaxis = new DateAxis("");
		NumberAxis numberaxis = new NumberAxis("");

		dateaxis.setTickLabelFont(new Font("SansSerif", 0, 10));
		dateaxis.setLabelFont(new Font("SansSerif", 0, 7));
		
		XYPlot xyplot = new XYPlot(timeseriescollection, dateaxis, numberaxis,
				xylineandshaperenderer);

		RectangleInsets rectangleInsets = new RectangleInsets();
		xyplot.setAxisOffset(rectangleInsets);

		xyplot.setDomainGridlineStroke(new BasicStroke(0.4f));
		xyplot.setRangeGridlineStroke(new BasicStroke(0.4f));
		xyplot.setOutlineVisible(false);

		xyplot.setBackgroundPaint(Color.BLACK);
		xyplot.setDomainGridlinePaint(new Color(0, 128, 64));
		xyplot.setRangeGridlinePaint(new Color(0, 128, 64));

		dateaxis.setFixedAutoRange(300000d);
		dateaxis.setLowerMargin(0.0D);
		dateaxis.setUpperMargin(0.0D);
		dateaxis.setTickLabelsVisible(true);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberaxis.setLowerMargin(0.01D);
		numberaxis.setUpperMargin(0.01D);
		JFreeChart chart = new JFreeChart(chartTitle, new Font("SansSerif", 1, 15), xyplot, false);
		chart.setBorderVisible(false);
		chart.setBorderStroke(new BasicStroke(0.0f));
		return chart;
	}

	/**
	 * Create basic info table
	 *
	 * @param parent the parent composite
	 *
	 */
	private void createSeriesTable(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		GridData gdBasic = new GridData(SWT.FILL, SWT.NONE, false, false);
		comp.setLayoutData(gdBasic);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		comp.setLayout(layout);

		final Label label = new Label(comp, SWT.CENTER);
		label.setText(Messages.tblSeriesTtl);

		seriesTableViewer = new TableViewer(comp, SWT.NO_SCROLL | SWT.BORDER);
		Table seriesTable = seriesTableViewer.getTable();
		seriesTable.setHeaderVisible(true);
		seriesTable.setLinesVisible(true);
		GridData tblSeries = new GridData(SWT.FILL, SWT.TOP, true, false);

		tblSeries.heightHint = CommonUITool.getHeightHintOfTable(seriesTable);
		seriesTable.setLayoutData(tblSeries);

		TableLayout seriesLayout = new TableLayout();
		setSeriesTableLayout(seriesLayout);
		seriesTable.setLayout(seriesLayout);

		TableColumn tblColumn = new TableColumn(seriesTable, SWT.CENTER);
		tblColumn.setText("");
		tblColumn.setResizable(false);

		for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
			tblColumn = new TableColumn(seriesTable, SWT.CENTER);
			tblColumn.setText(entry.getKey());
			tblColumn.setToolTipText(entry.getKey());
			tblColumn.setResizable(false);
		}
		List<String[]> chartTblLst = new ArrayList<String[]>();
		String[] arrays = valueMap.values().toArray(new String[valueMap.size()]);
		String[] arrays2 = new String[arrays.length + 1];
		arrays2[0] = "";
		System.arraycopy(arrays, 0, arrays2, 1, arrays.length);
		chartTblLst.add(arrays2);
		ChartTableContentProvider chartTableContentProvider = new ChartTableContentProvider();
		seriesTableViewer.setContentProvider(chartTableContentProvider);
		seriesTableViewer.setLabelProvider(new ChartTableLabelProvider());
		seriesTableViewer.setInput(chartTblLst);
		int index = 0;
		for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
			org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(
					comp.getDisplay(), entry.getValue().getSeriesRgb());
			seriesTableViewer.getTable().getItem(0).setBackground(index + 1, color);
			index++;
		}
		seriesTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				updateTableLayout();
			}
		});
	}

	/**
	 * Update table layout
	 */
	private void updateTableLayout() {
		TableLayout seriesLayout = new TableLayout();
		setSeriesTableLayout(seriesLayout);
		seriesTableViewer.getTable().setLayout(seriesLayout);
		seriesTableViewer.getTable().layout(true);
	}

	/**
	 * Set the basic info table layout based on the different column.
	 *
	 * @param layout the table layout
	 */
	private void setSeriesTableLayout(TableLayout layout) {
		layout.addColumnData(new ColumnWeightData(0, 0, false));
		int count = 0;
		for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
			if (entry.getValue().isChecked()) {
				layout.addColumnData(new ColumnWeightData(10, 50, true));
			} else {
				layout.addColumnData(new ColumnWeightData(0, 0, false));
				count++;
			}
		}
		if (count == settingMap.size()) {
			seriesTableViewer.getTable().setVisible(false);
		} else {
			seriesTableViewer.getTable().setVisible(true);
		}
	}

	/**
	 * Handle the update setting change
	 *
	 */
	private void handleUpdateSettingChange() {
		String selectedItem = combo.getItem(combo.getSelectionIndex());
		boolean checked = checkBtn.getSelection();
		RGB rgb = colorField.getColorSelector().getColorValue();
		updateSettingChart(selectedItem, checked, rgb);
		updateSettingTable(selectedItem, checked, rgb);

	}

	/**
	 * Update the setting table
	 *
	 * @param selectedItem the selected item
	 * @param checked whether show in the table
	 * @param rgb the item background color
	 */
	private void updateSettingTable(String selectedItem, boolean checked, RGB rgb) {
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(
				composite.getDisplay(), rgb);
		int index = 0;
		for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
			if (entry.getKey().equals(selectedItem)) {
				entry.getValue().setChecked(checked);
				entry.getValue().setSeriesRgb(rgb);
				seriesTableViewer.getTable().getItem(0).setBackground(index + 1, color);
			}
			index++;
		}
		TableLayout seriesLayout = new TableLayout();
		setSeriesTableLayout(seriesLayout);
		seriesTableViewer.getTable().setLayout(seriesLayout);
		seriesTableViewer.getTable().layout(true);
	}

	/**
	 * Update setting series
	 *
	 */
	public void updateSettingSeries() {
		int index = 0;
		for (Map.Entry<String, ShowSetting> entry : settingMap.entrySet()) {
			String item = entry.getKey();
			ShowSetting showSetting = entry.getValue();
			boolean checked = showSetting.isChecked();
			RGB rgb = showSetting.getSeriesRgb();
			float width = showSetting.getWidth();

			org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(
					composite.getDisplay(), rgb);
			seriesTableViewer.getTable().getItem(0).setBackground(index + 1, color);
			updateSettingChart(item, checked, rgb, width);

			index++;
		}
		TableLayout seriesLayout = new TableLayout();
		setSeriesTableLayout(seriesLayout);
		seriesTableViewer.getTable().setLayout(seriesLayout);
		seriesTableViewer.getTable().layout(true);

	}

	/**
	 * Update setting chart
	 *
	 * @param item the selected item
	 * @param checked whether show in the table
	 * @param seriesRgb the background color of the selected item
	 */
	private void updateSettingChart(String item, boolean checked, RGB seriesRgb) {
		updateSettingChart(item, checked, seriesRgb, 1.0f);
	}

	/**
	 * Update setting chart
	 *
	 * @param item the selected item
	 * @param checked whether show in the table
	 * @param seriesRgb the background color of the selected item
	 * @param width the width of the selected item
	 */
	@SuppressWarnings("unchecked")
	private void updateSettingChart(String item, boolean checked, RGB seriesRgb, float width) {
		Color color = new Color(seriesRgb.red, seriesRgb.green, seriesRgb.blue);
		if (checked) {
			if (timeseriescollection.getSeries(item) == null) {
				timeseriescollection.addSeries(seriesMap.get(item));
			}
			List<TimeSeries> list = timeseriescollection.getSeries();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getKey().equals(item)) {
					xylineandshaperenderer.setSeriesPaint(i, color);
					xylineandshaperenderer.setSeriesStroke(i, new BasicStroke(width, 0, 2));
					break;
				}

			}
		} else {
			if (timeseriescollection.getSeries(item) != null) {
				timeseriescollection.removeSeries(seriesMap.get(item));
			}
		}
	}

	/**
	 *
	 * Get the chart title
	 *
	 * @return the chartTitle
	 */
	public String getChartTitle() {
		return chartTitle;
	}

	/**
	 * @param chartTitle the chartTitle to set
	 */
	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	/**
	 * Get the chart
	 *
	 * @return the chart
	 */
	public JFreeChart getChart() {
		return chart;
	}

	/**
	 * @param chart the chart to set
	 */
	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	/**
	 * Get the instance of ColorFieldEditor
	 *
	 * @return the colorField
	 */
	public ColorFieldEditor getColorField() {
		return colorField;
	}

	/**
	 * @param colorField the colorField to set
	 */
	public void setColorField(ColorFieldEditor colorField) {
		this.colorField = colorField;
	}

	/**
	 * This method is responsible for preparing data for ChartSettingDlg and
	 * dealing with the results of chartSettingDlg
	 *
	 */
	public void fireChartSetting() {
		TextTitle title = chart.getTitle();
		if (title.getBackgroundPaint() == null) {
			ttlBgColor = trimPaintColor(chart.getBackgroundPaint().toString());
		} else {
			ttlBgColor = trimPaintColor(title.getBackgroundPaint().toString());
		}

		Font titleFont = title.getFont();

		String ttlFontName = titleFont.getFontName();
		int ttlFontSize = titleFont.getSize();
		String ttlFontColor = trimPaintColor(title.getPaint().toString());

		// plot
		XYPlot plot = chart.getXYPlot();
		String plotBgColor = trimPaintColor(plot.getBackgroundPaint().toString());
		String plotDomainGridColor = trimPaintColor(plot.getDomainGridlinePaint().toString());

		String plotRangGridColor = trimPaintColor(plot.getRangeGridlinePaint().toString());
		String plotDateAxisColor = trimPaintColor(plot.getRangeAxis().getAxisLinePaint().toString());
		String plotNumberAxisColor = trimPaintColor(plot.getDomainAxis().getAxisLinePaint().toString());

		ChartSettingDlg chartSettingDlg = new ChartSettingDlg(composite.getShell());
		if (ttlBgColor != null) {
			chartSettingDlg.setTtlBgColor(ttlBgColor);
		}
		// title
		chartSettingDlg.setTtlFontName(ttlFontName);
		chartSettingDlg.setTtlFontSize(ttlFontSize);
		chartSettingDlg.setTtlFontColor(ttlFontColor);
		// plot appearance
		chartSettingDlg.setPlotBgColor(plotBgColor);
		chartSettingDlg.setPlotDomainGridColor(plotDomainGridColor);
		chartSettingDlg.setPlotRangGridColor(plotRangGridColor);
		chartSettingDlg.setPlotDateAxisColor(plotDateAxisColor);
		chartSettingDlg.setPlotNumberAxisColor(plotNumberAxisColor);
		// series
		chartSettingDlg.setSettingMap(settingMap);
		// history path
		chartSettingDlg.setHistoryPath(historyPath);
		chartSettingDlg.setHistoryFileName(historyFileName);

		if (chartSettingDlg.open() == Dialog.OK) {
			// title
			ttlBgColor = chartSettingDlg.getTtlBgColor();
			ttlFontName = chartSettingDlg.getTtlFontName();
			ttlFontSize = chartSettingDlg.getTtlFontSize();
			ttlFontColor = chartSettingDlg.getTtlFontColor();
			// plot
			plotBgColor = chartSettingDlg.getPlotBgColor();
			plotDomainGridColor = chartSettingDlg.getPlotDomainGridColor();
			plotRangGridColor = chartSettingDlg.getPlotRangGridColor();
			plotDateAxisColor = chartSettingDlg.getPlotDateAxisColor();
			plotNumberAxisColor = chartSettingDlg.getPlotNumberAxisColor();
			// history path

			String newHistoryPath = chartSettingDlg.getHistoryPath();
			isChangedHistoryPath = historyPath.equals(newHistoryPath) ? false : true;
			if (isChangedHistoryPath) {
				historyPath = newHistoryPath;
			}

			int red = 0;
			int green = 0;
			int blue = 0;
			if (ttlBgColor != null) {
				red = getColorElem(ttlBgColor, 0);
				green = getColorElem(ttlBgColor, 1);
				blue = getColorElem(ttlBgColor, 2);
				title.setBackgroundPaint(new Color(red, green, blue));
			}
			titleFont = new Font(ttlFontName, 1, ttlFontSize);
			title.setFont(titleFont);

			red = getColorElem(ttlFontColor, 0);
			green = getColorElem(ttlFontColor, 1);
			blue = getColorElem(ttlFontColor, 2);
			title.setPaint(new Color(red, green, blue));

			red = getColorElem(plotBgColor, 0);
			green = getColorElem(plotBgColor, 1);
			blue = getColorElem(plotBgColor, 2);
			plot.setBackgroundPaint(new Color(red, green, blue));

			red = getColorElem(plotDomainGridColor, 0);
			green = getColorElem(plotDomainGridColor, 1);
			blue = getColorElem(plotDomainGridColor, 2);
			plot.setDomainGridlinePaint(new Color(red, green, blue));

			red = getColorElem(plotRangGridColor, 0);
			green = getColorElem(plotRangGridColor, 1);
			blue = getColorElem(plotRangGridColor, 2);
			plot.setRangeGridlinePaint(new Color(red, green, blue));

			red = getColorElem(plotDateAxisColor, 0);
			green = getColorElem(plotDateAxisColor, 1);
			blue = getColorElem(plotDateAxisColor, 2);
			plot.getRangeAxis().setAxisLinePaint(new Color(red, green, blue));

			red = getColorElem(plotNumberAxisColor, 0);
			green = getColorElem(plotNumberAxisColor, 1);
			blue = getColorElem(plotNumberAxisColor, 2);
			plot.getDomainAxis().setAxisLinePaint(new Color(red, green, blue));

			// series
			settingMap = chartSettingDlg.getSettingMap();
			updateSettingSeries();
		}
	}

	/**
	 * @param valueMap the valueMap to set
	 */
	public void updateValueMap(Map<String, String> valueMap) {
		for (Map.Entry<String, String> entry : valueMap.entrySet()) {
			if (this.valueMap.containsKey(entry.getKey())) {
				this.valueMap.put(entry.getKey(), entry.getValue());
			}
		}
		//	this.valueMap = valueMap;
		String[] arrays = this.valueMap.values().toArray(new String[this.valueMap.size()]);
		String[] arrays2 = new String[arrays.length + 1];
		arrays2[0] = "";
		System.arraycopy(arrays, 0, arrays2, 1, arrays.length);
		seriesTableViewer.getTable().getItem(0).setText(arrays2);

		for (Map.Entry<String, String> entry : this.valueMap.entrySet()) {
			int value = Integer.parseInt(entry.getValue());
			seriesMap.get(entry.getKey()).addOrUpdate(new Millisecond(), value);
		}
	}

	/**
	 * Update the plot in chart from scratch.
	 *
	 */
	public void updateChart() {
		XYPlot plot = (XYPlot) (getChart().getPlot());
		TimeSeriesCollection timeseriescollection = (TimeSeriesCollection) (plot.getDataset());
		timeseriescollection.removeAllSeries();
		XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) plot.getRenderer();
		seriesMap.clear();
		int number = 0;
		for (Map.Entry<String, String> entry : valueMap.entrySet()) {
			String key = entry.getKey();
			TimeSeries series = new TimeSeries(key);
			seriesMap.put(key, series);
			if (settingMap.get(key).isChecked()) {
				timeseriescollection.addSeries(series);
				RGB seriesRgb = settingMap.get(key).getSeriesRgb();
				float width = settingMap.get(key).getWidth();
				Color color = new Color(seriesRgb.red, seriesRgb.green, seriesRgb.blue);
				xylineandshaperenderer.setSeriesPaint(number, color);
				xylineandshaperenderer.setSeriesStroke(number, new BasicStroke(width, 0, 2));
				number++;
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
	 */
	public void executeQueryWithBusyCursor(final CounterFile file, final List<String> types,
			final long beginTime, final long endTime) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						stuffedChart(file, types, beginTime, endTime);
					}
				});
			}
		});
	}

	/**
	 * This method provides data from local fill for History chart showing
	 *
	 * @param file the instance of CounterFile, which includes data info
	 * @param types a array which includes one or more data type
	 * @param beginTime the begin time
	 * @param endTime the ending time
	 */
	private void stuffedChart(CounterFile file, List<String> types, long beginTime, long endTime) {
		List<String> fileTypes = new ArrayList<String>();
		for (String type : types) {
			fileTypes.add(type);
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
					double result = results[length].getAvgAsDouble(type);
					if (result < 0) {
						result = 0;
					}
					// if (result > 0) {
					seriesMap.get(type).addOrUpdate(new Second(new Date(time)), result);
					// }
				}
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
			length++;
		}
	}

	/**
	 *
	 * Storage the data into the local file.
	 *
	 * @param <T> the generic type which is the sub type of IDiagPara
	 * @param updateMap a instance of TreeMap which include all the data that
	 *        will be storage.
	 * @param ts a generic array, for instance
	 *        BrokerDiagEnum.values(),DbStatDumpEnum.values()
	 * @param dbName the database name, this value can be set null
	 */

	public <T extends IDiagPara> void storageData(Map<String, String> updateMap, T[] ts,
			String dbName) {
		CounterFile countFile = getHistoryFile(ts, dbName);
		long time = System.currentTimeMillis();
		for (T diagName : ts) {
			try {
				String type = diagName.getName();
				long value = Long.valueOf(updateMap.get(diagName.getName()));
				if (dbName != null) {
					type = dbName + "_" + type;
				}
				countFile.updateData(time, type, value);
			} catch (NumberFormatException e) {
				LOGGER.error(e.getMessage());
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * Open a history file while reading the history data
	 *
	 * @return the instance of CounterFile
	 */
	public CounterFile openHistoryFile() {
		File counter = new File(historyPath);
		if (counter.exists() && counter.isFile()) {
			try {
				countFile = new BasicCounterFile(counter, null);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
		} else {
			// confirm
			CommonUITool.openErrorBox(Messages.errHistoryRecordFile);
		}
		return countFile;
	}

	/**
	 * Get the instance of CounterFile when the history data should be restored.
	 *
	 * @param <T> the generic type which is the sub type of IDiagPara
	 * @param ts the instance of generic array
	 * @param dbName the database name
	 * @return the instance of CounterFile
	 */
	public <T extends IDiagPara> CounterFile getHistoryFile(T[] ts, String dbName) {

		if (countFile == null || isChangedHistoryPath) {
			closeHistroyFile();
			countFile = createOrOpenHistoryFile(ts, dbName);
			isChangedHistoryPath = false;
		}
		return countFile;
	}

	/**
	 * Create or Open a history file when the history data should be restored.
	 *
	 *
	 * @param <T> the generic type which is the sub type of IDiagPara
	 * @param ts the instance of generic array
	 * @param dbName the database name
	 * @return the instance of CounterFile
	 */
	private <T extends IDiagPara> CounterFile createOrOpenHistoryFile(T[] ts, String dbName) {
		File counter = new File(historyPath);
		if (counter.exists() && counter.isFile()) {
			try {
				countFile = new BasicCounterFile(counter, null);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
		} else {
			List<CounterType> counterLst = new ArrayList<CounterType>();
			for (T diagName : ts) {
				String type = diagName.getName();
				if (dbName != null) {
					type = dbName + "_" + type;
				}
				CounterType counterType = new CounterType(type, true, false, RangeType.INT);
				counterLst.add(counterType);
			}
			CounterType[] types = counterLst.toArray(new CounterType[counterLst.size()]);
			Properties props = new Properties();
			try {
				countFile = new BasicCounterFile(counter, types, 36000, 3, 0, props);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage());
			}
		}
		return countFile;
	}

	/**
	 * Get the clone of settingMap
	 *
	 * @return the settingMap
	 */
	public TreeMap<String, ShowSetting> getSettingMap() {
		return settingMap;
	}

	/**
	 * @param settingMap the settingMap to set
	 */
	public void setSettingMap(TreeMap<String, ShowSetting> settingMap) {
		this.settingMap = settingMap;
	}

	/**
	 * Judgment whether show the series list composite
	 *
	 * @return the isShowSeriesList
	 */
	public boolean isShowSeriesList() {
		return isShowSeriesList;
	}

	/**
	 * @param isShowSeriesList the isShowSeriesList to set
	 */
	public void setShowSeriesList(boolean isShowSeriesList) {
		this.isShowSeriesList = isShowSeriesList;
	}

	/**
	 *
	 * @param monInstaceData the monInstaceData to set
	 */
	public void setSettingData(StatusMonInstanceData monInstaceData) {
		this.monInstaceData = monInstaceData;
	}

	/**
	 * @return the historyPath
	 */
	public String getHistoryPath() {
		return historyPath;
	}

	/**
	 * @param historyPath the historyPath to set
	 */
	public void setHistoryPath(String historyPath) {
		this.historyPath = historyPath;
	}

	/**
	 * Close the opened file
	 *
	 */
	public void closeHistroyFile() {
		if (countFile == null) {
			return;
		}
		try {
			countFile.close();
			countFile = null;
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage());
		}

	}

	public void setHistoryFileName(String historyFileName) {
		this.historyFileName = historyFileName;

	}

	/**
	 * Set the isChangedHistoryPath
	 *
	 * @return the isChangedHistoryPath
	 */
	public boolean isChangedHistoryPath() {
		return isChangedHistoryPath;
	}

	/**
	 * @param isChangedHistoryPath the isChangedHistoryPath to set
	 */
	public void setChangedHistoryPath(boolean isChangedHistoryPath) {
		this.isChangedHistoryPath = isChangedHistoryPath;
	}

}
