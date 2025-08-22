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
package com.cubrid.cubridmanager.ui.monstatistic.editor;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.model.MonitorStatistic;
import com.cubrid.common.ui.spi.part.CubridViewPart;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticData;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.MetricType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.StatisticType;
import com.cubrid.cubridmanager.core.monstatistic.model.StatisticParamUtil.TimeType;
import com.cubrid.cubridmanager.ui.monstatistic.Messages;
import com.cubrid.cubridmanager.ui.monstatistic.editor.internal.MonitorStatisticChart;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * This type provides a dialog for user to configuration a monitor statistic item in monitor
 * statistic page
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-08-05 created by Santiago Wang
 */
public class MonitorStatisticDetailViewPart extends CubridViewPart {

    private static final Logger LOGGER = LogUtil.getLogger(MonitorStatisticDetailViewPart.class);
    public static final String ID = MonitorStatisticDetailViewPart.class.getName();

    private final String VALUE_DEFAULT = "";

    private MonitorStatistic pageNode;
    private MonitorStatisticEditor editor;
    private MonitorStatisticChart chart;
    private TableViewer hostTableViewer;
    private boolean isMultiHost;
    private List<StatisticData> statisticDataList;
    private StatisticChartItem statisticChartItem;

    private Composite rootComp;

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.setSite(site);
    }

    public void init(MonitorStatistic pageNode, MonitorStatisticEditor editor) {
        setPartName(Messages.bind(Messages.monStatisticDetailViewTitle, pageNode.getId()));
        this.pageNode = pageNode;
        if (!isMultiHost) {
            super.setCubridNode(pageNode);
        }
        this.editor = editor;
    }

    public void createPartControl(Composite parent) {
        rootComp = new Composite(parent, SWT.NONE);
        {
            final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.heightHint = 500;
            gd.widthHint = 200;
            rootComp.setLayoutData(gd);

            GridLayout gl = new GridLayout();
            gl.numColumns = 1;
            rootComp.setLayout(gl);
        }
        GridLayout layout = new GridLayout(1, false);
        rootComp.setLayout(layout);
        rootComp.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    public void reloadChart() {
        if (statisticDataList == null || statisticDataList.size() == 0) {
            return;
        }
        if (rootComp.getChildren() != null) {
            for (Control ctrl : rootComp.getChildren()) {
                ctrl.dispose();
            }
        }

        chart = new MonitorStatisticChart(rootComp, true, isMultiHost);
        chart.setStatisticDataList(statisticDataList);
        chart.loadChart();

        createInfoGroup(rootComp);

        rootComp.layout();
    }

    private void createInfoGroup(Composite parent) {
        final int DEFAULT_WIDTH = 80;
        final Group infoGroup = new Group(parent, SWT.RESIZE);
        GridLayout infoGroupLayout = new GridLayout();
        infoGroupLayout.verticalSpacing = 10;
        infoGroup.setLayout(infoGroupLayout);
        final GridData gdInfoGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
        infoGroup.setLayoutData(gdInfoGroup);
        infoGroup.setText(Messages.msgGpChartInfo);

        Composite compInfo = new Composite(infoGroup, SWT.RESIZE);
        compInfo.setLayout(new FillLayout());
        compInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        hostTableViewer =
                new TableViewer(
                        compInfo,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
        hostTableViewer.getTable().setHeaderVisible(true);
        hostTableViewer.getTable().setLinesVisible(true);

        if (isMultiHost) {
            // Host Name
            final TableViewerColumn nameColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
            nameColumn.getColumn().setWidth(DEFAULT_WIDTH);
            nameColumn.getColumn().setText(Messages.lblHostName);

            // IP
            final TableViewerColumn ipColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
            ipColumn.getColumn().setWidth(DEFAULT_WIDTH + 20);
            ipColumn.getColumn().setText(Messages.lblIp);

            // Port
            final TableViewerColumn portColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
            portColumn.getColumn().setWidth(50);
            portColumn.getColumn().setText(Messages.lblPort);
        }
        // Data Type
        final TableViewerColumn dataTypeColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        dataTypeColumn.getColumn().setWidth(DEFAULT_WIDTH);
        dataTypeColumn.getColumn().setText(Messages.lblDataType);

        // Time Type
        final TableViewerColumn timeTypeColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        timeTypeColumn.getColumn().setWidth(DEFAULT_WIDTH);
        timeTypeColumn.getColumn().setText(Messages.lblTimeType);

        // Database Name
        final TableViewerColumn dbNameColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        dbNameColumn.getColumn().setWidth(DEFAULT_WIDTH + 20);
        dbNameColumn.getColumn().setText(Messages.lblDbName);

        // Database Volume Name
        final TableViewerColumn volNameColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        volNameColumn.getColumn().setWidth(DEFAULT_WIDTH + 40);
        volNameColumn.getColumn().setText(Messages.lblVolName);

        // Broker Name
        final TableViewerColumn brokerColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        brokerColumn.getColumn().setWidth(DEFAULT_WIDTH + 20);
        brokerColumn.getColumn().setText(Messages.lblBrokerName);

        // Metric
        final TableViewerColumn metricColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        metricColumn.getColumn().setWidth(DEFAULT_WIDTH * 2);
        metricColumn.getColumn().setText(Messages.lblMetric);

        // max value
        final TableViewerColumn maxColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        maxColumn.getColumn().setWidth(DEFAULT_WIDTH);
        maxColumn.getColumn().setText(Messages.msgMaxValue);

        // min value
        final TableViewerColumn minColumn = new TableViewerColumn(hostTableViewer, SWT.LEFT);
        minColumn.getColumn().setWidth(DEFAULT_WIDTH);
        minColumn.getColumn().setText(Messages.msgMinValue);

        hostTableViewer.setContentProvider(new HostTableViewerContentProvider());
        hostTableViewer.setLabelProvider(new HostTableViewerLabelProvider());
        hostTableViewer
                .getTable()
                .addFocusListener(
                        new FocusAdapter() {
                            @Override
                            public void focusGained(FocusEvent event) {
                                ActionManager.getInstance()
                                        .changeFocusProvider(hostTableViewer.getTable());
                            }
                        });

        if (statisticDataList != null) {
            hostTableViewer.setInput(statisticDataList);
            hostTableViewer.refresh();
        }
    }

    public List<StatisticData> getStatisticDataList() {
        return statisticDataList;
    }

    public void setStatisticDataList(List<StatisticData> statisticDataList) {
        this.statisticDataList = statisticDataList;
    }

    public StatisticChartItem getStatisticChartItem() {
        return statisticChartItem;
    }

    public void setStatisticChartItem(StatisticChartItem statisticChartItem) {
        this.statisticChartItem = statisticChartItem;
    }

    public MonitorStatistic getPageNode() {
        return pageNode;
    }

    public MonitorStatisticEditor getEditor() {
        return editor;
    }

    public boolean isMultiHost() {
        return isMultiHost;
    }

    public void setMultiHost(boolean isMultiHost) {
        this.isMultiHost = isMultiHost;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void nodeChanged(CubridNodeChangedEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * Host table label provider. Used for multi-host.
     *
     * @author Santiago Wang
     */
    public class HostTableViewerLabelProvider extends LabelProvider implements ITableLabelProvider {
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof StatisticData) {
                StatisticData dataItem = (StatisticData) element;
                if (dataItem != null) {
                    StatisticType type = dataItem.getType();
                    TimeType enumTimeType = TimeType.getEnumByType(dataItem.getDtype());
                    String timeType =
                            enumTimeType != null ? enumTimeType.getMessage() : VALUE_DEFAULT;
                    MetricType metricType = MetricType.getEnumByMetric(dataItem.getMetric());

                    if (isMultiHost) {
                        ServerInfo serverInfo = dataItem.getServerInfo();
                        switch (columnIndex) {
                            case 0: // Host Name
                                return serverInfo.getServerName();
                            case 1: // IP
                                return serverInfo.getHostAddress();
                            case 2: // Port
                                return Integer.toString(serverInfo.getHostMonPort());
                            case 3: // Data Type
                                return type.getMessage();
                            case 4: // Time Type
                                return timeType;
                            case 5: // Database Name
                                return dataItem.getDbName();
                            case 6: // Database Volume Name
                                return dataItem.getVolName();
                            case 7: // Broker Name
                                return dataItem.getbName();
                            case 8: // Metric
                                return metricType != null ? metricType.getMessage() : VALUE_DEFAULT;
                            case 9: // max value
                                return Double.toString(chart.getMaxValueMap().get(dataItem));
                            case 10: // min value
                                return Double.toString(chart.getMinValueMap().get(dataItem));
                        }
                    } else {
                        switch (columnIndex) {
                            case 0: // Data Type
                                return type.getMessage();
                            case 1: // Time Type
                                return timeType;
                            case 2: // Database Name
                                return dataItem.getDbName();
                            case 3: // Database Volume Name
                                return dataItem.getVolName();
                            case 4: // Broker Name
                                return dataItem.getbName();
                            case 5: // Metric
                                return metricType != null ? metricType.getMessage() : VALUE_DEFAULT;
                            case 6: // max value
                                return Double.toString(chart.getMaxValueMap().get(dataItem));
                            case 7: // min value
                                return Double.toString(chart.getMinValueMap().get(dataItem));
                        }
                    }
                }
            }

            return null;
        }
    }

    /**
     * Host table content provider. Used for multi-host.
     *
     * @author Santiago Wang
     */
    public class HostTableViewerContentProvider implements IStructuredContentProvider {
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<StatisticData> list = (List<StatisticData>) inputElement;
                StatisticData[] nodeArr = new StatisticData[list.size()];
                return list.toArray(nodeArr);
            }

            return new Object[] {};
        }

        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    }
}
