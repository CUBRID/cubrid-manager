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
package com.cubrid.cubridmanager.ui.mondashboard.editor.figure;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * BrokerHostMonitorFigure Description
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class HostMonitorFigure extends AbstractMonitorFigure implements FlashSupport {

    private static final int ICON_SIZE = 16;

    private static final Image HOST_PNG = CubridManagerUIPlugin.getImage("icons/monitor/host.png");

    private static final int TOTAL_WIDTH = 157;
    private static final int LABEL_HEIGHT = 16;
    private static final int SPACING_1 = 4;
    private static final int SPACING_2 = 2;
    private static final int SPACING_3 = 6;

    private static final int TITLE_HEIGHT = LABEL_HEIGHT + SPACING_1 * 2;
    private static final int STATUS_HEIGHT = LABEL_HEIGHT + SPACING_2 * 2;
    // SPACING_3;

    private static final int MONITOR_CONTAINER_WIDTH = TOTAL_WIDTH - SPACING_3 * 2;
    private static final int SPACING_4 =
            (MONITOR_CONTAINER_WIDTH - MonitorChartFigure.FIGURE_WIDTH * 3) / 4;
    private static final int MONITOR_CONTAINER_HEIGHT =
            MonitorChartFigure.FIGURE_HEIGHT + SPACING_4 * 2;
    private static final int TOTAL_HEIGHT =
            TITLE_HEIGHT + STATUS_HEIGHT + MONITOR_CONTAINER_HEIGHT + SPACING_3;

    private final Label title;

    private final Label hostStatus;
    private final ImageFigure statusFigure;

    private final RoundedRectangle monitorContainer;
    private final MonitorChartFigure hostCpuFigure;
    private final MonitorChartFigure hostMemFigure;
    private final MonitorChartFigure hostIOFigure;

    private final Label hostCpuLabel;
    private final Label hostMemLabel;
    private final Label hostIOLabel;

    private boolean isNeedFlash = true;

    /** default constructor of BrokerHostMonitorFigure. Initialize figures. */
    public HostMonitorFigure() {
        setSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        // db name & connection status
        title = new Label();
        title.setLabelAlignment(PositionConstants.LEFT);
        title.setIcon(HOST_PNG);
        title.setSize(TOTAL_WIDTH - SPACING_1 - ICON_SIZE, LABEL_HEIGHT);
        title.setLocation(new Point(SPACING_1, SPACING_1));
        title.setForegroundColor(ColorConstants.black);
        add(title);
        statusFigure = new ImageFigure();
        statusFigure.setSize(ICON_SIZE, ICON_SIZE);
        statusFigure.setLocation(new Point(TOTAL_WIDTH - SPACING_1 - ICON_SIZE, SPACING_1));
        statusFigure.setToolTip(new Label());
        add(statusFigure);
        // host status
        hostStatus = new Label();
        hostStatus.setSize(TOTAL_WIDTH, LABEL_HEIGHT);
        hostStatus.setLocation(new Point(0, TITLE_HEIGHT + SPACING_2));
        hostStatus.setForegroundColor(ColorConstants.black);
        hostStatus.setText(STATUS_DISCONNECTED);
        hostStatus.setToolTip(new Label());
        // hostStatus.setVisible(false);
        add(hostStatus);

        // calulate monitor figures positions.
        int offsetx1 = SPACING_4 + SPACING_3 + 1;
        int offsetx2 = offsetx1 + MonitorChartFigure.FIGURE_WIDTH + SPACING_4;
        int offsetx3 = offsetx2 + MonitorChartFigure.FIGURE_WIDTH + SPACING_4;
        int mcOffsetY = TITLE_HEIGHT + STATUS_HEIGHT;
        int offsety1 = mcOffsetY + SPACING_4;
        int offsety2 = offsety1 + MonitorChartFigure.FIGURE_HEIGHT - LABEL_HEIGHT;
        // monitor border's rect
        monitorContainer = new RoundedRectangle();
        monitorContainer.setLocation(new Point(SPACING_3, mcOffsetY));
        monitorContainer.setSize(MONITOR_CONTAINER_WIDTH, MONITOR_CONTAINER_HEIGHT);
        add(monitorContainer);
        // monitor figures

        hostCpuFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_RED);
        hostCpuFigure.setLocation(new Point(offsetx1, offsety1));
        hostCpuFigure.setPreFix(" CPU:");
        add(hostCpuFigure);
        hostMemFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_YELLOW);

        hostMemFigure.setLocation(new Point(offsetx2, offsety1));
        hostMemFigure.setPreFix(" MEM: ");
        add(hostMemFigure);
        hostIOFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_BLUE);

        hostIOFigure.setLocation(new Point(offsetx3, offsety1));
        hostIOFigure.setPreFix(" IO Wait: ");
        add(hostIOFigure);
        // monitors' labels
        hostCpuLabel = new Label();
        hostCpuLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        hostCpuLabel.setLocation(new Point(offsetx1, offsety2));
        //		hostCpuLabel.setBackgroundColor(SWTResourceManager.getColor(0, 0, 0));
        hostCpuLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_RED);
        hostCpuLabel.setText("0");
        add(hostCpuLabel);
        hostMemLabel = new Label();
        hostMemLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        hostMemLabel.setLocation(new Point(offsetx2, offsety2));
        hostMemLabel.setText("0");
        hostMemLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_YELLOW);
        add(hostMemLabel);
        hostIOLabel = new Label();
        hostIOLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        hostIOLabel.setLocation(new Point(offsetx3, offsety2));
        hostIOLabel.setText("0");
        hostIOLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_BLUE);
        add(hostIOLabel);
    }

    /**
     * Set figures' status.
     *
     * @param needFlash boolean
     * @param connected boolean
     * @param errorMsg String
     * @param hasNewErrorMsg boolean
     */
    public void setStatus(
            boolean needFlash, boolean connected, String errorMsg, boolean hasNewErrorMsg) {
        this.isNeedFlash = needFlash;
        if (connected) {
            hostStatus.setText(STATUS_CONNECTED);
            setLabelHint(statusFigure, STATUS_CONNECTED);
            if (StringUtil.isEmpty(errorMsg)) {
                hostStatus.setIcon(null);
                setLabelHint(hostStatus, getLabelHint(this));
            } else {
                hostStatus.setIcon(ERROR_PNG);
                setLabelHint(hostStatus, errorMsg);
            }
            if (hasNewErrorMsg) {
                statusFigure.setImage(PNG_YELLOW);
                startFlash();
            } else {
                statusFigure.setImage(PNG_GREEN);
                stopFlash();
            }
        } else {
            statusFigure.setImage(PNG_RED);
            hostStatus.setText(STATUS_DISCONNECTED);
            setLabelHint(statusFigure, STATUS_DISCONNECTED);
            if (StringUtil.isEmpty(errorMsg)) {
                hostStatus.setIcon(null);
                setLabelHint(hostStatus, getLabelHint(this));
            } else {
                hostStatus.setIcon(ERROR_PNG);
                setLabelHint(hostStatus, errorMsg);
            }
            stopFlash();
        }
    }

    /**
     * set host name to figure
     *
     * @param title String
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * set figure's host cpu usage value
     *
     * @param cpuUsage 0 to 100
     */
    public void setHostCpuUsage(int cpuUsage) {
        hostCpuFigure.setValue(cpuUsage);
        hostCpuLabel.setText(String.valueOf(cpuUsage) + hostCpuFigure.getPostFix());
    }

    /**
     * set figures's host io value
     *
     * @param delay wait io wait>0
     */
    public void setHostIO(int delay) {
        hostIOFigure.setValue(delay);
        hostIOLabel.setText(String.valueOf(delay) + hostIOFigure.getPostFix());
    }

    /**
     * set figure's host memory usage value.
     *
     * @param memUsage 0 to 100
     */
    public void setHostMemUsage(int memUsage) {
        hostMemFigure.setValue(memUsage);
        hostMemLabel.setText(String.valueOf(memUsage) + hostMemFigure.getPostFix());
    }

    /**
     * fill shapge
     *
     * @param graphics Graphics
     */
    protected void fillShape(Graphics graphics) {
        super.fillShape(graphics);
        Rectangle r = getBounds();
        graphics.drawLine(r.x + 1, r.y + TITLE_HEIGHT, r.x + r.width - 1, r.y + TITLE_HEIGHT);
    }

    /**
     * Set minimized
     *
     * @param minimize boolean
     */
    public void setMinimized(boolean minimize) {
        super.setMinimized(minimize);
        if (isMinimized()) {
            lineWidth = 2;
            title.setFont(FONT_MINIMIZED);
            setForegroundColor(MINIMIZED_BORDER_COLOR);
            monitorContainer.setVisible(false);
            setSize(TOTAL_WIDTH, TITLE_HEIGHT + STATUS_HEIGHT + SPACING_1);
        } else {
            lineWidth = 1;
            title.setFont(FONT_NORMAL);
            setForegroundColor(DEFAULT_BORDER_COLOR);
            monitorContainer.setVisible(true);
            setSize(TOTAL_WIDTH, TOTAL_HEIGHT);
        }
    }

    /** Start filcker */
    public void startFlash() {
        if (isNeedFlash) {
            FlashSupportManager.getInstance().add(this);
        }
    }

    /** Stop Flicker */
    public void stopFlash() {
        FlashSupportManager.getInstance().remove(this);
        statusFigure.setVisible(true);
    }

    /** Flicker. */
    public void flash() {
        statusFigure.setVisible(!statusFigure.isVisible());
    }
}
