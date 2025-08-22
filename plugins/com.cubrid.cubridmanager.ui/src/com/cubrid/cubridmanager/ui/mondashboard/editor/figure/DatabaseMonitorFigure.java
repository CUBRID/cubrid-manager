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
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToggleButton;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * figure of database monitor
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class DatabaseMonitorFigure extends AbstractMonitorFigure
        implements ActionListener, FlashSupport {
    private static final int ICON_SIZE = 16;
    private static final int TOTAL_WIDTH = 157;
    private static final int LABEL_HEIGHT = 16;
    private static final int SPACING_1 = 4;
    private static final int SPACING_2 = 2;
    private static final int SPACING_3 = 6;

    private static final int TITLE_HEIGHT = LABEL_HEIGHT + SPACING_1 * 2;
    private static final int STATUS_HEIGHT = LABEL_HEIGHT + SPACING_2 * 2;;

    private static final int MONITOR_CONTAINER_WIDTH = TOTAL_WIDTH - SPACING_3 * 2;
    private static final int SPACING_4 =
            (MONITOR_CONTAINER_WIDTH - MonitorChartFigure.FIGURE_WIDTH * 3) / 4;
    private static final int MONITOR_CONTAINER_HEIGHT =
            MonitorChartFigure.FIGURE_HEIGHT + SPACING_4 * 2;
    private static final int TOTAL_HEIGHT =
            TITLE_HEIGHT + STATUS_HEIGHT + MONITOR_CONTAINER_HEIGHT + SPACING_3 + TITLE_HEIGHT;

    private static final Image DB_PNG =
            CubridManagerUIPlugin.getImage("icons/monitor/database.png");

    private static final Dimension MONITOR_CHART_SIZE = new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT);

    private final Label title;
    private final ImageFigure statusFigure;
    private final Label statusLabel;
    private final Label hostName;
    private final MonitorChartFigure cpuFigure;
    private final MonitorChartFigure memFigure;
    private final MonitorChartFigure delayFigure;

    private final Label cpuLabel;
    private final Label memLabel;
    private final Label delayLabel;
    private final RoundedRectangle monitorContainer;

    private boolean isShowHost = false;

    private boolean isHostConnected = false;

    private boolean isNeedFlash = true;

    /** default constructor of BrokerHostMonitorFigure. Initialize figures. */
    public DatabaseMonitorFigure() {
        setSize(MONITOR_CHART_SIZE);
        // db name & connection status
        title = new Label();
        title.setLabelAlignment(PositionConstants.LEFT);
        title.setIcon(DB_PNG);
        title.setSize(TOTAL_WIDTH - SPACING_1 - ICON_SIZE, LABEL_HEIGHT);
        title.setLocation(new Point(SPACING_1, SPACING_1));
        title.setForegroundColor(ColorConstants.black);
        add(title);

        statusFigure = new ImageFigure();
        statusFigure.setImage(PNG_RED);
        statusFigure.setSize(ICON_SIZE, ICON_SIZE);
        statusFigure.setLocation(new Point(TOTAL_WIDTH - SPACING_1 - ICON_SIZE, SPACING_1));
        statusFigure.setToolTip(new Label());
        add(statusFigure);
        // db status
        statusLabel = new Label();
        statusLabel.setSize(TOTAL_WIDTH, LABEL_HEIGHT);
        statusLabel.setLocation(new Point(0, TITLE_HEIGHT + SPACING_2));
        statusLabel.setForegroundColor(ColorConstants.black);
        statusLabel.setToolTip(new Label());
        add(statusLabel);

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
        cpuFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_RED);
        cpuFigure.setLocation(new Point(offsetx1, offsety1));
        cpuFigure.setPreFix(" CPU: ");
        add(cpuFigure);
        memFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_YELLOW);
        memFigure.setLocation(new Point(offsetx2, offsety1));
        memFigure.setPreFix(" MEM: ");
        add(memFigure);
        delayFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_PINK);
        delayFigure.setLocation(new Point(offsetx3, offsety1));
        delayFigure.setPostFix(" ms");
        delayFigure.setPreFix(" Delay: ");
        add(delayFigure);
        // database status labels
        cpuLabel = new Label();
        cpuLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        cpuLabel.setLocation(new Point(offsetx1, offsety2));
        cpuLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_RED);
        cpuLabel.setText("0");
        add(cpuLabel);
        memLabel = new Label();
        memLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        memLabel.setLocation(new Point(offsetx2, offsety2));
        memLabel.setText("0");
        memLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_YELLOW);
        add(memLabel);
        delayLabel = new Label();
        delayLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        delayLabel.setLocation(new Point(offsetx3, offsety2));
        delayLabel.setText("0");
        delayLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_PINK);
        add(delayLabel);
        // host name
        hostName = new Label();
        hostName.setLocation(
                new Point(SPACING_1, mcOffsetY + MONITOR_CONTAINER_HEIGHT + SPACING_3 + SPACING_1));
        hostName.setSize(TOTAL_WIDTH, LABEL_HEIGHT);
        hostName.setForegroundColor(ColorConstants.black);
        add(hostName);
        // showHost toggle button;deprecated
        ToggleButton showHost = new ToggleButton();
        showHost.setSize(ICON_SIZE, ICON_SIZE);
        showHost.setLocation(new Point(100, 99));
        showHost.addActionListener(this);
        // hide toggle button.
        showHost.setVisible(false);
        add(showHost);
    }

    /**
     * executed when toggle button be clicked.
     *
     * @param event ActionEvent
     */
    public void actionPerformed(ActionEvent event) {
        isShowHost = !isShowHost;
        if (isShowHost) {
            setSize(122, 178);
        } else {
            setSize(MONITOR_CHART_SIZE);
        }
        getParent().setConstraint(this, getBounds());
        getParent().repaint();
    }

    /**
     * set database name to label
     *
     * @param dbName String
     */
    public void setTitle(String dbName) {
        title.setText(dbName);
    }

    /**
     * Set figures' status.
     *
     * @param isNeedFlash boolean
     * @param hostConnected boolean
     * @param dbLogon boolean
     * @param dbStatus String
     * @param errorMsg String
     * @param hasNewErrorMsg boolean
     */
    public void setStatus(
            boolean isNeedFlash,
            boolean hostConnected,
            boolean dbLogon,
            String dbStatus,
            String errorMsg,
            boolean hasNewErrorMsg) {
        this.isNeedFlash = isNeedFlash;
        isHostConnected = hostConnected;
        if (isHostConnected) {
            if (dbLogon) {
                setLabelHint(statusFigure, STATUS_CONNECTED);
                if (StringUtil.isEmpty(errorMsg)) {
                    statusLabel.setText(dbStatus);
                    statusLabel.setIcon(null);
                    setLabelHint(statusLabel, getLabelHint(this));
                } else {
                    statusLabel.setText(dbStatus);
                    statusLabel.setIcon(ERROR_PNG);
                    setLabelHint(statusLabel, errorMsg);
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
                setLabelHint(statusFigure, STATUS_DB_DISCONNECTED);
                statusLabel.setText(STATUS_DB_DISCONNECTED);
                if (StringUtil.isEmpty(errorMsg)) {
                    statusLabel.setIcon(null);
                    setLabelHint(statusLabel, getLabelHint(this));
                } else {
                    statusLabel.setIcon(ERROR_PNG);
                    setLabelHint(statusLabel, errorMsg);
                }
                startFlash();
            }
        } else {
            statusFigure.setImage(PNG_RED);
            setLabelHint(statusFigure, STATUS_HOST_DISCONNECTED);
            statusLabel.setText(STATUS_HOST_DISCONNECTED);
            if (StringUtil.isEmpty(errorMsg)) {
                statusLabel.setIcon(null);
                setLabelHint(statusLabel, getLabelHint(this));
            } else {
                statusLabel.setIcon(ERROR_PNG);
                setLabelHint(statusLabel, errorMsg);
            }
            stopFlash();
        }
    }

    /**
     * set host name to figure
     *
     * @param hostName String
     */
    public void setHostName(String hostName) {
        this.hostName.setText(hostName);
    }

    /**
     * fill shapge
     *
     * @param graphics Graphics
     */
    protected void fillShape(Graphics graphics) {
        super.fillShape(graphics);

        Rectangle r = getBounds();

        Color color = null;
        if (isHostConnected) {
            color = PREFER.getColor(statusLabel.getText());
        } else {
            color = DISABLED_COLOR;
        }
        if (isMinimized()) {
            if (color != null) {
                graphics.setBackgroundColor(color);
                graphics.fillRectangle(r.x + 1, r.y + TITLE_HEIGHT, r.width - 2, STATUS_HEIGHT + 3);
            }
        } else {
            if (color != null) {
                graphics.setBackgroundColor(color);
                graphics.fillRectangle(
                        r.x,
                        r.y + TITLE_HEIGHT + 1,
                        r.width,
                        r.height - TITLE_HEIGHT - TITLE_HEIGHT - 1);
            }

            graphics.drawLine(
                    r.x,
                    r.y + r.height - TITLE_HEIGHT,
                    r.x + r.width - 1,
                    r.y + r.height - TITLE_HEIGHT);
            if (isShowHost) {
                graphics.drawLine(r.x, r.y + 117, r.x + r.width, r.y + 117);
                graphics.drawRectangle(r.x + 6, r.y + 123, 109, 48);
            }
        }
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
            monitorContainer.setSize(MONITOR_CONTAINER_WIDTH, 0);
            setSize(TOTAL_WIDTH, TITLE_HEIGHT + STATUS_HEIGHT + SPACING_1);
        } else {
            lineWidth = 1;
            title.setFont(FONT_NORMAL);
            setForegroundColor(DEFAULT_BORDER_COLOR);
            monitorContainer.setSize(MONITOR_CONTAINER_WIDTH, MONITOR_CONTAINER_HEIGHT);
            setSize(TOTAL_WIDTH, TOTAL_HEIGHT);
        }
    }

    /**
     * set figure's cpu usage value
     *
     * @param cpuUsage 0 to 100
     */
    public void setCpuUsage(int cpuUsage) {
        cpuFigure.setValue(cpuUsage);
        cpuLabel.setText(String.valueOf(cpuUsage) + cpuFigure.getPostFix());
    }

    /**
     * set figures's delay value
     *
     * @param delay delay>0
     */
    public void setDelay(int delay) {
        delayFigure.setValue(delay);
        delayLabel.setText(String.valueOf(delay));
    }

    /**
     * set figure's memory usage value.
     *
     * @param memUsage 0 to 100
     */
    public void setMemUsage(int memUsage) {
        memFigure.setValue(memUsage);
        memLabel.setText(String.valueOf(memUsage) + memFigure.getPostFix());
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
