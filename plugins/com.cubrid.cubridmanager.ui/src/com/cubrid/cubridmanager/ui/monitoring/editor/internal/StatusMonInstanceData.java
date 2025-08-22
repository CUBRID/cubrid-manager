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

import java.io.Serializable;
import java.util.TreeMap;

/**
 * A plain java bean that contains the information of a status monitor template
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-4-2 created by lizhiqiang
 */
public class StatusMonInstanceData implements Serializable {

    private static final long serialVersionUID = -3478536477257524720L;
    private MonitorType monitorType;
    private String titleName;
    private String titleBgColor;
    private String titleFontName;
    private int titleFontSize;
    private String titleFontColor;
    // plot
    private String plotBgColor;
    private String plotDomainGridColor;
    private String plotRangGridColor;
    private String plotDateAxisColor;
    private String plotNumberAxisColor;
    // series
    private TreeMap<String, ShowSetting> settingMap;
    // history record
    private String historyPath;

    /**
     * Get the title name
     *
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /** @param titleName the titleName to set */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * Get the title background color
     *
     * @return the titleBgColor
     */
    public String getTitleBgColor() {
        return titleBgColor;
    }

    /** @param titleBgColor the titleBgColor to set */
    public void setTitleBgColor(String titleBgColor) {
        this.titleBgColor = titleBgColor;
    }

    /**
     * Get title font name
     *
     * @return the titleFontName
     */
    public String getTitleFontName() {
        return titleFontName;
    }

    /** @param titleFontName the titleFontName to set */
    public void setTitleFontName(String titleFontName) {
        this.titleFontName = titleFontName;
    }

    /**
     * Get the title font size
     *
     * @return the titleFontSize
     */
    public int getTitleFontSize() {
        return titleFontSize;
    }

    /** @param titleFontSize the titleFontSize to set */
    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     * Get the title font color
     *
     * @return the titleFontColor
     */
    public String getTitleFontColor() {
        return titleFontColor;
    }

    /** @param titleFontColor the titleFontColor to set */
    public void setTitleFontColor(String titleFontColor) {
        this.titleFontColor = titleFontColor;
    }

    /**
     * Get the plot background color
     *
     * @return the plotBgColor
     */
    public String getPlotBgColor() {
        return plotBgColor;
    }

    /** @param plotBgColor the plotBgColor to set */
    public void setPlotBgColor(String plotBgColor) {
        this.plotBgColor = plotBgColor;
    }

    /**
     * Get the plot domain grid color
     *
     * @return the plotDomainGridColor
     */
    public String getPlotDomainGridColor() {
        return plotDomainGridColor;
    }

    /** @param plotDomainGridColor the plotDomainGridColor to set */
    public void setPlotDomainGridColor(String plotDomainGridColor) {
        this.plotDomainGridColor = plotDomainGridColor;
    }

    /**
     * Get the plot range grid color
     *
     * @return the plotRangGridColor
     */
    public String getPlotRangGridColor() {
        return plotRangGridColor;
    }

    /** @param plotRangGridColor the plotRangGridColor to set */
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

    /** @param plotDateAxisColor the plotDateAxisColor to set */
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

    /** @param plotNumberAxisColor the plotNumberAxisColor to set */
    public void setPlotNumberAxisColor(String plotNumberAxisColor) {
        this.plotNumberAxisColor = plotNumberAxisColor;
    }

    /**
     * Get the settingMap
     *
     * @return the settingMap
     */
    public TreeMap<String, ShowSetting> getSettingMap() {
        return settingMap;
    }

    /** @param settingMap the settingMap to set */
    public void setSettingMap(TreeMap<String, ShowSetting> settingMap) {
        this.settingMap = settingMap;
    }

    /**
     * Get the monitorType
     *
     * @return the monitorType
     */
    public MonitorType getMonitorType() {
        return monitorType;
    }

    /** @param monitorType the monitorType to set */
    public void setMonitorType(MonitorType monitorType) {
        this.monitorType = monitorType;
    }

    /**
     * Get the path of history record
     *
     * @return the historyPath
     */
    public String getHistoryPath() {
        return historyPath;
    }

    /** @param historyPath the historyPath to set */
    public void setHistoryPath(String historyPath) {
        this.historyPath = historyPath;
    }
}
