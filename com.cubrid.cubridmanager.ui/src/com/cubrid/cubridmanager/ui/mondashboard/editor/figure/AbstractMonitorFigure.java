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

import com.cubrid.common.ui.spi.ResourceManager;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.mondashboard.preference.MonitorDashboardPreference;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * base class of monitor figure classes.
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class AbstractMonitorFigure extends RoundedRectangle {
    protected static final Color DEFAULT_BORDER_COLOR = ResourceManager.getColor(153, 186, 243);
    protected static final Color MINIMIZED_BORDER_COLOR = ResourceManager.getColor(0, 128, 128);
    protected static final Font FONT_MINIMIZED =
            ResourceManager.getFont("WINDOWS", 10, 1, false, false);
    protected static final Font FONT_NORMAL =
            ResourceManager.getFont("WINDOWS", 10, 0, false, false);

    public static final String STATUS_CONNECTED = "Connected";
    public static final String STATUS_DISCONNECTED = "Disconnected";
    public static final String STATUS_HOST_DISCONNECTED = "Host disconnected";
    public static final String STATUS_DB_DISCONNECTED = "DB disconnected";
    public static final String STATUS_UNKNOWN = "Unknown";

    public static final Color DISABLED_COLOR = ResourceManager.getColor(128, 128, 128);

    protected static final Image ERROR_PNG =
            CubridManagerUIPlugin.getImage("icons/monitor/error.gif");
    protected static final Image PNG_RED =
            CubridManagerUIPlugin.getImage("icons/monitor/circle_red.png");
    protected static final Image PNG_GREEN =
            CubridManagerUIPlugin.getImage("icons/monitor/circle_green.png");
    protected static final Image PNG_YELLOW =
            CubridManagerUIPlugin.getImage("icons/monitor/circle_yellow.png");

    protected static final MonitorDashboardPreference PREFER = new MonitorDashboardPreference();

    private boolean minimized = false;

    /** default constructor. */
    public AbstractMonitorFigure() {
        // ToolTip
        Label tt = new Label();
        this.setToolTip(tt);
        setForegroundColor(DEFAULT_BORDER_COLOR);
    }

    /**
     * set the hint figures to pop
     *
     * @param hint String
     */
    public void setHint(String hint) {
        ((Label) getToolTip()).setText(hint);
    }

    /**
     * set the hint of a figure
     *
     * @param figure the figure
     * @param hint String
     */
    public static void setLabelHint(Figure figure, String hint) {
        if (figure.getToolTip() instanceof Label) {
            ((Label) figure.getToolTip()).setText(hint);
        }
    }

    /**
     * Get the hint of a figure
     *
     * @param figure the figure
     * @return hint String
     */
    public static String getLabelHint(Figure figure) {
        if (figure.getToolTip() instanceof Label) {
            return ((Label) figure.getToolTip()).getText();
        }
        return "";
    }

    /**
     * Get figure minimize status.
     *
     * @return the minimized
     */
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * Set figure minimized status.
     *
     * @param minimized the minimized to set
     */
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    /** when figure's edit part activated. */
    public void activate() {
        // activate.
    }

    /** when figure's edit part deactivated. */
    public void deactivate() {
        // deactivate
    }
}
