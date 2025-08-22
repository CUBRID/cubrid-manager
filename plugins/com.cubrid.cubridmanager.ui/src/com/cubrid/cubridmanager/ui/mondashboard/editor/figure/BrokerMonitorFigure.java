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
import com.cubrid.cubridmanager.core.common.model.OnOffType;
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
 * Figure of broker monitor
 *
 * @author cyl
 * @version 1.0 - 2010-8-18 created by cyl
 */
public class BrokerMonitorFigure extends AbstractMonitorFigure implements FlashSupport {
    private static final int ICON_SIZE = 16;
    private static final int TOTAL_WIDTH = 157;
    private static final int LABEL_HEIGHT = 16;
    private static final int SPACING_1 = 4;
    private static final int SPACING_2 = 2;
    private static final int SPACING_3 = 6;

    private static final int TITLE_HEIGHT = LABEL_HEIGHT + SPACING_1 * 2;
    private static final int STATUS_HEIGHT = LABEL_HEIGHT + SPACING_2 * 2;

    private static final int MONITOR_CONTAINER_WIDTH = TOTAL_WIDTH - SPACING_3 * 2;
    private static final int SPACING_4 =
            (MONITOR_CONTAINER_WIDTH - MonitorChartFigure.FIGURE_WIDTH * 3) / 4;
    private static final int MONITOR_CONTAINER_HEIGHT =
            MonitorChartFigure.FIGURE_HEIGHT + SPACING_4 * 2;
    private static final int TOTAL_HEIGHT =
            TITLE_HEIGHT + STATUS_HEIGHT + MONITOR_CONTAINER_HEIGHT + SPACING_3 + TITLE_HEIGHT;

    private static final Image PNG_ICON =
            CubridManagerUIPlugin.getImage("icons/navigator/broker.png");

    private static final Dimension MONITOR_CHART_SIZE = new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT);

    private final Label title;
    private final ImageFigure statusFigure;
    private final Label statusLabel;
    private final Label hostName;

    private final RoundedRectangle monitorContainer;
    private final MonitorChartFigure sessionFigure;
    private final MonitorChartFigure activeSessionFigure;
    private final MonitorChartFigure tpsFigure;

    private final Label sessionLabel;
    private final Label aciveSessionLabel;
    private final Label tpsLabel;
    private boolean isNeedFlash = true;

    // private boolean isConnected = false;

    /** default constructor of BrokerMonitorFigure. Initialize figures. */
    public BrokerMonitorFigure() {
        setSize(MONITOR_CHART_SIZE);
        // broker name & connection status
        title = new Label();
        title.setLabelAlignment(PositionConstants.LEFT);
        title.setIcon(PNG_ICON);
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
        // status
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
        sessionFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_RED);
        sessionFigure.setLocation(new Point(offsetx1, offsety1));
        sessionFigure.setPreFix(" Sessions: ");
        sessionFigure.setPostFix(" ");
        add(sessionFigure);
        activeSessionFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_YELLOW);
        activeSessionFigure.setLocation(new Point(offsetx2, offsety1));
        activeSessionFigure.setPreFix(" Active Sessions: ");
        activeSessionFigure.setPostFix(" ");
        add(activeSessionFigure);
        tpsFigure = new MonitorChartFigure(MonitorChartFigure.COLUMN_COLOR_PINK);
        tpsFigure.setLocation(new Point(offsetx3, offsety1));
        tpsFigure.setPostFix(" ");
        tpsFigure.setPreFix(" TPS: ");
        add(tpsFigure);
        // database status labels
        sessionLabel = new Label();
        sessionLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        sessionLabel.setLocation(new Point(offsetx1, offsety2));
        sessionLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_RED);
        sessionLabel.setText("0");
        add(sessionLabel);
        aciveSessionLabel = new Label();
        aciveSessionLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        aciveSessionLabel.setLocation(new Point(offsetx2, offsety2));
        aciveSessionLabel.setText("0");
        aciveSessionLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_YELLOW);
        add(aciveSessionLabel);
        tpsLabel = new Label();
        tpsLabel.setSize(MonitorChartFigure.FIGURE_WIDTH, LABEL_HEIGHT);
        tpsLabel.setLocation(new Point(offsetx3, offsety2));
        tpsLabel.setText("0");
        tpsLabel.setForegroundColor(MonitorChartFigure.COLUMN_COLOR_PINK);
        add(tpsLabel);
        // host name
        hostName = new Label();
        hostName.setLocation(
                new Point(SPACING_1, mcOffsetY + MONITOR_CONTAINER_HEIGHT + SPACING_3 + SPACING_1));
        hostName.setSize(TOTAL_WIDTH, LABEL_HEIGHT);
        hostName.setForegroundColor(ColorConstants.black);
        add(hostName);
    }

    /**
     * set broker name to label
     *
     * @param name String
     */
    public void setTitle(String name) {
        title.setText(name);
    }

    /**
     * Set broker's status.
     *
     * @param needFlash boolean
     * @param connected boolean
     * @param brokerStatus String
     * @param accessMode mode
     * @param errorMsg errorMsg
     * @param hasNewErrorMsg boolean
     */
    public void setStatus(
            boolean needFlash,
            boolean connected,
            String brokerStatus,
            String accessMode,
            String errorMsg,
            boolean hasNewErrorMsg) {
        isNeedFlash = needFlash;
        if (connected) {
            setLabelHint(statusFigure, STATUS_CONNECTED);
            if (OnOffType.ON.getText().equals(brokerStatus)) {
                if (StringUtil.isNotEmpty(accessMode)) {
                    statusLabel.setText(accessMode);
                } else {
                    statusLabel.setText(brokerStatus);
                }
                if (StringUtil.isNotEmpty(errorMsg)) {
                    statusLabel.setIcon(ERROR_PNG);
                    setLabelHint(statusLabel, errorMsg);
                } else {
                    statusLabel.setIcon(null);
                    setLabelHint(statusLabel, getLabelHint(this));
                }
                if (hasNewErrorMsg) {
                    statusFigure.setImage(PNG_YELLOW);
                    startFlash();
                } else {
                    statusFigure.setImage(PNG_GREEN);
                    stopFlash();
                }
            } else {
                statusFigure.setImage(PNG_YELLOW);
                statusLabel.setText("Broker " + OnOffType.OFF.getText());
                if (StringUtil.isNotEmpty(errorMsg)) {
                    statusLabel.setIcon(ERROR_PNG);
                    setLabelHint(statusLabel, errorMsg);
                } else {
                    statusLabel.setIcon(null);
                    setLabelHint(statusLabel, getLabelHint(this));
                }
                startFlash();
            }
        } else {
            statusFigure.setImage(PNG_RED);
            setLabelHint(statusFigure, STATUS_HOST_DISCONNECTED);
            statusLabel.setText(STATUS_HOST_DISCONNECTED);
            if (StringUtil.isNotEmpty(errorMsg)) {
                statusLabel.setIcon(ERROR_PNG);
                setLabelHint(statusLabel, errorMsg);
            } else {
                statusLabel.setIcon(null);
                setLabelHint(statusLabel, getLabelHint(this));
            }
            stopFlash();
        }
    }

    /**
     * Set host name to figure
     *
     * @param hostName String
     */
    public void setHostName(String hostName) {
        this.hostName.setText(hostName);
    }

    /**
     * Fill shapge
     *
     * @param graphics Graphics
     */
    protected void fillShape(Graphics graphics) {
        super.fillShape(graphics);
        Rectangle r = getBounds();
        graphics.drawLine(r.x + 1, r.y + TITLE_HEIGHT, r.x + r.width - 1, r.y + TITLE_HEIGHT);

        if (!isMinimized()) {
            graphics.drawLine(
                    r.x + 1,
                    r.y + r.height - TITLE_HEIGHT,
                    r.x + r.width - 1,
                    r.y + r.height - TITLE_HEIGHT);
        }
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
     * Set figure's cpu sessions value
     *
     * @param sessions >0
     */
    public void setSessionCount(int sessions) {
        sessionFigure.setValue(sessions);
        sessionLabel.setText(String.valueOf(sessions) + sessionFigure.getPostFix());
    }

    /**
     * Set figures's tps value
     *
     * @param tps >0
     */
    public void setTps(int tps) {
        tpsFigure.setValue(tps);
        tpsLabel.setText(String.valueOf(tps));
    }

    /**
     * Set figure's active session value.
     *
     * @param as active session count
     */
    public void setActiveSessionCount(int as) {
        activeSessionFigure.setValue(as);
        aciveSessionLabel.setText(String.valueOf(as) + activeSessionFigure.getPostFix());
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
