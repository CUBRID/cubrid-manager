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
package com.cubrid.cubridmanager.ui.broker.editor.internal;

import com.cubrid.common.core.util.CompatibleUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.broker.model.ApplyServerInfo;
import com.cubrid.cubridmanager.core.broker.model.JobInfo;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.ui.broker.Messages;
import com.cubrid.cubridmanager.ui.broker.editor.ApplyServerContentProvider;
import com.cubrid.cubridmanager.ui.broker.editor.ApplyServerLabelProvider;
import com.cubrid.cubridmanager.ui.broker.editor.JobContentProvider;
import com.cubrid.cubridmanager.ui.broker.editor.JobLabelProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.BrokerBasicInfoContentProvider;
import com.cubrid.cubridmanager.ui.mondashboard.editor.BrokerBasicInfoLabelProvider;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This type is responsible for generate tableView for the BrokerStatusView or other view part
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-8-21 created by lizhiqiang
 */
public class BrokerSatusTablePart {
    private ServerInfo serverInfo;
    private List<ApplyServerInfo> asinfoLst;
    private List<JobInfo> jobinfoLst;
    private boolean isAppendDiag;

    /**
     * Create basic info table
     *
     * @param comp the parent composite
     * @return TableViewer
     */
    public TableViewer createBasicTable(Composite comp) {
        final Composite basicComposite = new Composite(comp, SWT.NONE);
        GridData gdBasic = new GridData(SWT.FILL, SWT.NONE, false, false);
        basicComposite.setLayoutData(gdBasic);
        basicComposite.setLayout(new GridLayout());

        TableViewer basicTableViewer = new TableViewer(basicComposite, SWT.NO_SCROLL | SWT.BORDER);
        Table basicTable = basicTableViewer.getTable();
        basicTable.setHeaderVisible(true);
        basicTable.setLinesVisible(true);
        GridData tblBasic = new GridData(SWT.FILL, SWT.TOP, true, false);

        tblBasic.heightHint = CommonUITool.getHeightHintOfTable(basicTable);
        basicTable.setLayoutData(tblBasic);

        TableLayout basicLayout = new TableLayout();
        setBasicLayout(basicLayout);
        basicTable.setLayout(basicLayout);
        basicTable.setBackground(basicComposite.getBackground());

        TableColumn tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscPid);
        tblColumn.setToolTipText(Messages.tblBscPid);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscPort);
        tblColumn.setToolTipText(Messages.tblBscPort);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscJobQueue);
        tblColumn.setToolTipText(Messages.tblBscJobQueue);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscAutoAddAs);
        tblColumn.setToolTipText(Messages.tblBscAutoAddAs);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscSqlLogMode);
        tblColumn.setToolTipText(Messages.tblBscSqlLogMode);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscLongTranTime);
        tblColumn.setToolTipText(Messages.tblBscLongTranTime);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscLongQueryTime);
        tblColumn.setToolTipText(Messages.tblBscLongQueryTime);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscSessionTimeout);
        tblColumn.setToolTipText(Messages.tblBscSessionTimeout);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscKeepConn);
        tblColumn.setToolTipText(Messages.tblBscKeepConn);
        tblColumn = new TableColumn(basicTable, SWT.CENTER);
        tblColumn.setText(Messages.tblBscAccessMode);
        tblColumn.setToolTipText(Messages.tblBscAccessMode);

        if (isAppendDiag) {
            tblColumn = new TableColumn(basicTable, SWT.CENTER);
            tblColumn.setText(Messages.tblBscActiveSession);
            tblColumn.setToolTipText(Messages.tblBscActiveSession);
            tblColumn = new TableColumn(basicTable, SWT.CENTER);
            tblColumn.setText(Messages.tblBscSession);
            tblColumn.setToolTipText(Messages.tblBscSession);
            tblColumn = new TableColumn(basicTable, SWT.CENTER);
            tblColumn.setText(Messages.tblBscTps);
            tblColumn.setToolTipText(Messages.tblBscTps);
        }

        basicTableViewer.setContentProvider(new BrokerBasicInfoContentProvider());
        BrokerBasicInfoLabelProvider basicInfoLabelProvider = new BrokerBasicInfoLabelProvider();
        basicTableViewer.setLabelProvider(basicInfoLabelProvider);
        List<String> basicInfoLst = new ArrayList<String>();
        basicTableViewer.setInput(basicInfoLst);
        return basicTableViewer;
    }

    /**
     * Set the basic info table layout based on the different column.
     *
     * @param layout the table layout
     */
    public void setBasicLayout(TableLayout layout) {
        for (BrokerStatusBasicColumn column : BrokerStatusBasicColumn.values()) {
            if (column.getValue() == -1) {
                layout.addColumnData(new ColumnWeightData(0, 0, false));
            } else {
                switch (column) {
                    case PID:
                        layout.addColumnData(new ColumnWeightData(7, 50, true));
                        break;
                    case PORT:
                        layout.addColumnData(new ColumnWeightData(7, 50, true));
                        break;
                    default:
                        layout.addColumnData(new ColumnWeightData(10, 50, true));
                }
            }
        }
    }

    /**
     * Create app server table
     *
     * @param comp the parent composite
     * @return TableViewer
     */
    public TableViewer createAsTable(Composite comp) {
        Composite asComposite = new Composite(comp, SWT.NONE);
        GridData gdAs = new GridData(SWT.FILL, SWT.FILL, true, true);
        asComposite.setLayoutData(gdAs);
        asComposite.setLayout(new GridLayout());

        TableViewer asTableViewer =
                new TableViewer(
                        asComposite,
                        SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        Table asTable = asTableViewer.getTable();
        asTable.setHeaderVisible(true);
        asTable.setLinesVisible(true);
        asTable.setLayoutData(gdAs);

        TableLayout asLayout = new TableLayout();
        setAsLayout(asLayout);
        asTable.setLayout(asLayout);

        TableColumn tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsId);
        tblColumn.setToolTipText(Messages.tblAsId);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsProcess);
        tblColumn.setToolTipText(Messages.tblAsProcess);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsQps);
        tblColumn.setToolTipText(Messages.tblAsQps);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsLqs);
        tblColumn.setToolTipText(Messages.tblAsLqs);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsPort);
        tblColumn.setToolTipText(Messages.tblAsPort);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsSize);
        tblColumn.setToolTipText(Messages.tblAsSize);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsStatus);
        tblColumn.setToolTipText(Messages.tblAsStatus);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsDb);
        tblColumn.setToolTipText(Messages.tblAsDb);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsHost);
        tblColumn.setToolTipText(Messages.tblAsHost);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsLastAccess);
        tblColumn.setToolTipText(Messages.tblAsLastAccess);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsLct);
        tblColumn.setToolTipText(Messages.tblAsLct);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsClientIp);
        tblColumn.setToolTipText(Messages.tblAsClientIp);
        tblColumn = new TableColumn(asTable, SWT.CENTER);
        tblColumn.setText(Messages.tblAsCur);
        tblColumn.setToolTipText(Messages.tblAsCur);

        asTableViewer.setContentProvider(new ApplyServerContentProvider());
        asTableViewer.setLabelProvider(new ApplyServerLabelProvider());
        asTableViewer.setInput(asinfoLst);
        return asTableViewer;
    }

    /**
     * Set the apply server table layout based on the different column.
     *
     * @param layout the table layout
     */
    public void setAsLayout(TableLayout layout) {
        for (BrokerStatusAsColumn column : BrokerStatusAsColumn.values()) {
            if (column.getValue() == -1) {
                layout.addColumnData(new ColumnWeightData(0, 0, false));
            } else {
                switch (column) {
                    case PSIZE:
                    case STATUS:
                    case CLIENT_IP:
                        layout.addColumnData(new ColumnWeightData(25, 25, true));
                        break;
                    case PORT:
                        if (CompatibleUtil.isSupportBrokerPort(serverInfo)) {
                            layout.addColumnData(new ColumnWeightData(20, 20, true));
                        } else {
                            layout.addColumnData(new ColumnWeightData(0, 0, false));
                        }
                        break;
                    case DB:
                    case HOST:
                        layout.addColumnData(new ColumnWeightData(30, 30, true));
                        break;
                    case LAST_ACCESS_TIME:
                    case LAST_CONNECT_TIME:
                        layout.addColumnData(new ColumnWeightData(70, 70, true));
                        break;
                    case SQL:
                        layout.addColumnData(new ColumnWeightData(150, 150, true));
                        break;
                    default:
                        layout.addColumnData(new ColumnWeightData(20, 20, true));
                }
            }
        }
    }

    /**
     * Create job table composite
     *
     * @param comp the composite
     * @return TableViewer
     */
    public TableViewer createJobTable(Composite comp) {
        Composite jobComposite = new Composite(comp, SWT.NONE);
        GridData gdJob = new GridData(SWT.FILL, SWT.FILL, true, true);
        jobComposite.setLayoutData(gdJob);
        jobComposite.setLayout(new GridLayout());

        final Label label = new Label(jobComposite, SWT.CENTER);
        label.setText(Messages.jobTblTitle);
        TableViewer jqTableViewer =
                new TableViewer(
                        jobComposite,
                        SWT.FULL_SELECTION | SWT.NO_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        jqTableViewer.getTable().setHeaderVisible(true);
        jqTableViewer.getTable().setLinesVisible(true);

        TableLayout jqLayout = new TableLayout();
        setJqLayout(jqLayout);
        jqTableViewer.getTable().setLayout(jqLayout);
        jqTableViewer.getTable().setLayoutData(gdJob);

        TableColumn tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
        tblColumn.setText(Messages.tblJobId);
        tblColumn.setToolTipText(Messages.tblJobId);
        tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
        tblColumn.setText(Messages.tblJobPriority);
        tblColumn.setToolTipText(Messages.tblJobPriority);
        tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
        tblColumn.setText(Messages.tblJobAddress);
        tblColumn.setToolTipText(Messages.tblJobAddress);
        tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
        tblColumn.setText(Messages.tblJobTime);
        tblColumn.setToolTipText(Messages.tblJobTime);
        tblColumn = new TableColumn(jqTableViewer.getTable(), SWT.CENTER);
        tblColumn.setText(Messages.tblJobRequest);
        tblColumn.setToolTipText(Messages.tblJobRequest);

        jqTableViewer.setContentProvider(new JobContentProvider());
        jqTableViewer.setLabelProvider(new JobLabelProvider());
        jqTableViewer.setInput(jobinfoLst);
        return jqTableViewer;
    }

    /**
     * Set the Job queue table layout based on the different column.
     *
     * @param layout the table layout
     */
    public void setJqLayout(TableLayout layout) {
        for (BrokerStatusJqColumn column : BrokerStatusJqColumn.values()) {
            if (column.getValue() == -1) {
                layout.addColumnData(new ColumnWeightData(0, 0, false));
            } else {
                layout.addColumnData(new ColumnWeightData(10, 50, true));
            }
        }
    }

    /** @return the serverInfo */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /** @param serverInfo the serverInfo to set */
    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * Get the asinfoLst
     *
     * @return the asinfoLst
     */
    public List<ApplyServerInfo> getAsinfoLst() {
        return asinfoLst;
    }

    /** @param asinfoLst the asinfoLst to set */
    public void setAsinfoLst(List<ApplyServerInfo> asinfoLst) {
        this.asinfoLst = asinfoLst;
    }

    /**
     * Get the jobinfoLst
     *
     * @return the jobinfoLst
     */
    public List<JobInfo> getJobinfoLst() {
        return jobinfoLst;
    }

    /** @param jobinfoLst the jobinfoLst to set */
    public void setJobinfoLst(List<JobInfo> jobinfoLst) {
        this.jobinfoLst = jobinfoLst;
    }

    /**
     * Get the isAppendDiag
     *
     * @return the isAppendDiag
     */
    public boolean isAppendDiag() {
        return isAppendDiag;
    }

    /** @param isAppendDiag the isAppendDiag to set */
    public void setAppendDiag(boolean isAppendDiag) {
        this.isAppendDiag = isAppendDiag;
    }
}
