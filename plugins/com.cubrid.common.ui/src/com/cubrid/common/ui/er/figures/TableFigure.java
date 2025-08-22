/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.figures;

import com.cubrid.common.ui.spi.ResourceManager;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * Figure used to represent a table in the schema
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-10 created by Yu Guojia
 */
public class TableFigure extends Figure {
    public static String HEADER_FONT_KEY = "Verdana";
    private final ColumnsFigure columnsFigure = new ColumnsFigure();
    private final EditableLabel nameLabel;
    private boolean isSelected = false;

    public static Color defaultBackgroundColor = new Color(null, 0, 165, 81);
    public static Color selectedBackgroundColor = new Color(null, 3, 156, 77);
    public static Color disableBackgroundColor = new Color(null, 121, 121, 121);
    public static Color hoverBackgroundColor = new Color(null, 40, 157, 201);
    public static Color defaultBorderColor = new Color(null, 204, 204, 204);
    public static Color selectedBorderColor = new Color(null, 0, 161, 88);
    public static Color disableBorderColor = disableBackgroundColor;
    public static Color hoverBorderColor = hoverBackgroundColor;

    public TableFigure(EditableLabel name) {
        this(name, null);
    }

    public TableFigure(EditableLabel name, List colums) {
        nameLabel = name;
        nameLabel.setHeader(true);
        nameLabel.setFont(ResourceManager.getFont(HEADER_FONT_KEY, 11, SWT.NONE));
        nameLabel.setLabelAlignment(PositionConstants.CENTER);
        nameLabel.setForegroundColor(ColorConstants.white);

        ToolbarLayout layout = new ToolbarLayout();
        layout.setVertical(true);
        layout.setStretchMinorAxis(true);
        setLayoutManager(layout);
        setBorder(new LineBorder(defaultBorderColor, 1));
        setBackgroundColor(defaultBackgroundColor);
        setOpaque(true);
        add(name);
        add(columnsFigure);
    }

    public void setHoverEnterState() {
        if (isDisabled()) {
            // do nothing
        } else {
            LineBorder lineBorder = (LineBorder) this.getBorder();
            this.setBackgroundColor(hoverBackgroundColor);
            lineBorder.setColor(hoverBorderColor);
        }
    }

    public void setSelectedState() {
        LineBorder lineBorder = (LineBorder) getBorder();
        this.setBackgroundColor(selectedBackgroundColor);
        lineBorder.setColor(selectedBorderColor);
    }

    public void setNormalState() {
        LineBorder lineBorder = (LineBorder) this.getBorder();
        this.setBackgroundColor(defaultBackgroundColor);
        lineBorder.setColor(defaultBorderColor);
        if (lineBorder.getWidth() != 1 && !isSelected()) {
            lineBorder.setWidth(1);
        }
    }

    public void setDisableState() {
        LineBorder lineBorder = (LineBorder) this.getBorder();
        this.setBackgroundColor(disableBackgroundColor);
        lineBorder.setColor(disableBorderColor);
    }

    public void setHoverExistState() {
        if (isSelected()) {
            setSelectedState();
        } else if (isDisabled()) {
            // do nothing
        } else { // nomal
            setNormalState();
        }
    }

    public Color getBorderColor() {
        LineBorder lineBorder = (LineBorder) this.getBorder();
        return lineBorder.getColor();
    }

    public boolean isNormalState() {
        return defaultBackgroundColor.equals(this.getBackgroundColor());
    }

    public boolean isDisabled() {
        if (disableBackgroundColor.equals(this.getBackgroundColor())) {
            return true;
        }
        return false;
    }

    /**
     * Set hover state to the figure
     *
     * @param isHovered
     */
    public void setHovered(boolean isHovered) {
        LineBorder lineBorder = (LineBorder) getBorder();
        if (isHovered) {
            this.setBackgroundColor(hoverBackgroundColor);
            lineBorder.setColor(hoverBorderColor);
        } else {
            this.setBackgroundColor(defaultBackgroundColor);
            lineBorder.setColor(defaultBorderColor);
        }
    }

    public void setDisabled(boolean isDisable) {
        LineBorder lineBorder = (LineBorder) getBorder();
        if (isDisable) {
            this.setBackgroundColor(disableBackgroundColor);
            lineBorder.setColor(disableBorderColor);
        } else {
            setSelected(isSelected);
        }
    }

    public void setSelected(boolean isSelected) {
        LineBorder lineBorder = (LineBorder) getBorder();
        if (isSelected) {
            this.setBackgroundColor(selectedBackgroundColor);
            lineBorder.setColor(selectedBorderColor);
        } else {
            this.setBackgroundColor(defaultBackgroundColor);
            lineBorder.setColor(defaultBorderColor);
        }
        if (lineBorder.getWidth() != 1 && !isSelected) {
            lineBorder.setWidth(1);
        }
        this.isSelected = isSelected;
    }

    public EditableLabel getNameLabel() {
        return nameLabel;
    }

    public ColumnsFigure getColumnsFigure() {
        return columnsFigure;
    }

    public void addFocusListener(FocusListener listener) {
        super.addFocusListener(listener);
    }

    public boolean isSelected() {
        return isSelected;
    }
}
