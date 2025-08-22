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
package com.cubrid.common.ui.spi.model;

import com.cubrid.cubridmanager.core.monstatistic.model.StatisticChartItem;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.IEditorInput;

/**
 * CUBRID Monitor Statistic Node
 *
 * @author Santiago Wang
 * @version 1.0 - 2013-07-29 created by Santiago Wang
 */
public class MonitorStatistic extends DefaultCubridNode implements IEditorInput {

    private List<StatisticChartItem> statisticItemList = new ArrayList<StatisticChartItem>();
    private boolean isMultiHost = false;
    // for single host, IP and port is need, to specify node belongs to which CUBRID Host.
    private String ip;
    private int port;

    public MonitorStatistic(String id, String label, String iconPath) {
        super(id, label, iconPath);
    }

    public List<StatisticChartItem> getStatisticItemList() {
        return statisticItemList;
    }

    public void addStatisticItem(StatisticChartItem item) {
        if (item == null) {
            return;
        }
        statisticItemList.add(item);
    }

    public boolean isMultiHost() {
        return isMultiHost;
    }

    public void setMultiHost(boolean isMultiHost) {
        this.isMultiHost = isMultiHost;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getToolTipText() {
        return this.getId();
    }
}
