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

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * BrokerHostMonitorFigure Description
 *
 * @author cyl
 * @version 1.0 - 2010-6-3 created by cyl
 */
public class BrokerDBListFigure extends AbstractMonitorFigure {

    private static final Image PNG_ICON =
            CubridManagerUIPlugin.getImage("icons/navigator/database_group.png");
    private static final Image PNG_DATABASE =
            CubridManagerUIPlugin.getImage("icons/monitor/database.png");

    private static final int TOTAL_WIDTH = 157;
    private static final int LABEL_HEIGHT = 16;
    private static final int SPACING_1 = 4;
    private static final int TITLE_HEIGHT = LABEL_HEIGHT + SPACING_1 * 2;
    private static final int FIRST_CONTENT_Y = TITLE_HEIGHT + SPACING_1;
    private static final int TOTAL_HEIGHT = FIRST_CONTENT_Y + LABEL_HEIGHT + SPACING_1;

    private final Point lastContentLocation = new Point(SPACING_1, FIRST_CONTENT_Y);
    private final Label nameLabel;
    private final List<Label> databases = new ArrayList<Label>();

    /** default constructor of BrokerHostMonitorFigure. Initialize figures. */
    public BrokerDBListFigure() {
        setSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        // db name & connection status
        nameLabel = new Label();
        nameLabel.setLabelAlignment(PositionConstants.LEFT);
        nameLabel.setIcon(PNG_ICON);
        nameLabel.setSize(TOTAL_WIDTH - SPACING_1, LABEL_HEIGHT);
        nameLabel.setLocation(new Point(SPACING_1, SPACING_1));
        nameLabel.setForegroundColor(ColorConstants.black);
        add(nameLabel);
    }

    /**
     * set host name to figure
     *
     * @param name String
     */
    public void setName(String name) {
        nameLabel.setText(name);
        setHint(name);
    }

    /**
     * If the name in list is exists.
     *
     * @param name String.
     * @return true or false
     */
    private boolean nameExists(String name) {
        for (Label label : this.databases) {
            if (label.getText().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the database list to display
     *
     * @param list List<String>
     */
    public void setDBList(List<String> list) {
        // in case of concurrent modfiy exception
        List<Label> copyedDatabases = new ArrayList<Label>();
        copyedDatabases.addAll(this.databases);

        for (Label label : copyedDatabases) {
            if (!list.contains(label.getText())) {
                remove(label);
                databases.remove(label);
            }
        }
        for (String name : list) {
            if (nameExists(name)) {
                continue;
            }
            Label label = new Label();
            label.setLabelAlignment(PositionConstants.LEFT);
            label.setText(name);
            label.setToolTip(new Label());
            setLabelHint(label, name);
            label.setForegroundColor(ColorConstants.black);
            label.setIcon(PNG_DATABASE);
            label.setSize(getSize().width - SPACING_1, LABEL_HEIGHT);
            add(label);
            databases.add(label);
        }
        lastContentLocation.y = FIRST_CONTENT_Y;
        for (Label lable : databases) {
            lable.setLocation(
                    new Point(
                            getLocation().x + lastContentLocation.x,
                            getLocation().y + lastContentLocation.y));
            lastContentLocation.y = lastContentLocation.y + SPACING_1 + LABEL_HEIGHT;
        }
        // Auto change size with the client count.
        if (lastContentLocation.y >= TOTAL_HEIGHT) {
            setSize(TOTAL_WIDTH, lastContentLocation.y);
        }
    }

    /**
     * Set host's connection status
     *
     * @param connected boolean
     */
    public void setHostConnected(boolean connected) {
        Color color = DEFAULT_BORDER_COLOR;
        if (!connected) {
            color = DISABLED_COLOR;
        }
        setForegroundColor(color);
    }

    /**
     * fill shapge
     *
     * @param graphics Graphics
     */
    protected void fillShape(Graphics graphics) {
        super.fillShape(graphics);
        Rectangle r = getBounds();
        graphics.drawLine(r.x, r.y + TITLE_HEIGHT, r.x + r.width, r.y + TITLE_HEIGHT);
    }
}
