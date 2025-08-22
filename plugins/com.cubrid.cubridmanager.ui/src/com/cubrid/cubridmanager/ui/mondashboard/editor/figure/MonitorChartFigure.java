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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * a Figure class to display current status of a server or database
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class MonitorChartFigure extends ImageFigure {

    private static final Image BACK_IMAGE =
            CubridManagerUIPlugin.getImage("icons/monitor/monitor_figure_0.png");
    public static final int FIGURE_HEIGHT = 52;
    public static final int FIGURE_WIDTH = 33;

    public static final Color COLUMN_COLOR_YELLOW = ResourceManager.getColor(255, 255, 0);
    public static final Color COLUMN_COLOR_RED = ResourceManager.getColor(255, 0, 0);
    public static final Color COLUMN_COLOR_PINK = ResourceManager.getColor(255, 0, 255);
    public static final Color COLUMN_COLOR_BLUE = ResourceManager.getColor(0, 255, 255);

    private Color columnColor = COLUMN_COLOR_YELLOW;

    private int value;

    private int maxValue = 100;

    private String postFix = "%";

    private String preFix = "";

    private static List<Point[]> drawPoints = new ArrayList<Point[]>();

    static {
        for (int i = 29; i >= 1; i = i - 2) {
            Point[] ps = new Point[4];
            ps[0] = new Point(1, i);
            ps[1] = new Point(10, i);
            ps[2] = new Point(12, i);
            ps[3] = new Point(21, i);
            drawPoints.add(ps);
        }
    }

    /** default constructor of MonitorChartFigure */
    public MonitorChartFigure() {
        this.setImage(BACK_IMAGE);
        initFigure();
    }

    /** Initialize figure. */
    private void initFigure() {
        this.setSize(FIGURE_WIDTH, FIGURE_HEIGHT);
        Label tt = new Label();
        this.setToolTip(tt);
    }

    /** default constructor of MonitorChartFigure */
    public MonitorChartFigure(Color columnColor) {
        this.columnColor = columnColor;
        this.setImage(BACK_IMAGE);
        initFigure();
    }

    /**
     * get column color
     *
     * @return the columnColor
     */
    public Color getColumnColor() {
        return columnColor;
    }

    /** @param columnColor the columnColor to set */
    public void setColumnColor(Color columnColor) {
        this.columnColor = columnColor;
    }

    /**
     * get the value figure to show.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /** @param value the value to set */
    public void setValue(int value) {
        this.value = value;
        if (value > this.maxValue) {
            maxValue = value;
        }
    }

    /**
     * get persent of value/maxValue. for example if this.value=13 return 20 if this.value=80 return
     * 80
     *
     * @return value to display
     */
    private int getValueToDisplay() {
        int percent = (value * 100 * 3) / (maxValue * 2);
        return percent / 10 + (percent % 10 == 0 ? 0 : 1);
    }

    /**
     * draw figure
     *
     * @see org.eclipse.draw2d.ImageFigure#paintFigure(org.eclipse.draw2d.Graphics)
     * @param graphics Graphics
     */
    @Override
    protected void paintFigure(Graphics graphics) {
        AbstractMonitorFigure.setLabelHint(this, preFix + String.valueOf(value) + postFix);
        super.paintFigure(graphics);
        if (value > 0) {
            Color color = graphics.getForegroundColor();
            graphics.setForegroundColor(columnColor);
            int value2display = getValueToDisplay();
            for (int i = 0; i < value2display; i++) {
                graphics.drawLine(
                        new Point(
                                getLocation().x + 5 + drawPoints.get(i)[0].x,
                                getLocation().y + 5 + drawPoints.get(i)[0].y),
                        new Point(
                                getLocation().x + 5 + drawPoints.get(i)[1].x,
                                getLocation().y + 5 + drawPoints.get(i)[1].y));
                graphics.drawLine(
                        new Point(
                                getLocation().x + 5 + drawPoints.get(i)[2].x,
                                getLocation().y + 5 + drawPoints.get(i)[2].y),
                        new Point(
                                getLocation().x + 5 + drawPoints.get(i)[3].x,
                                getLocation().y + 5 + drawPoints.get(i)[3].y));
            }
            graphics.setForegroundColor(color);
        }
    }

    /**
     * set postfix displayed in tooltip default is %
     *
     * @param postFix the postFix to set
     */
    public void setPostFix(String postFix) {
        this.postFix = postFix;
    }

    /**
     * set prefix
     *
     * @return the preFix
     */
    public String getPreFix() {
        return preFix;
    }

    /** @param preFix the preFix to set */
    public void setPreFix(String preFix) {
        this.preFix = preFix;
    }

    /**
     * get text post fix.
     *
     * @return the postFix
     */
    public String getPostFix() {
        return postFix;
    }
}
