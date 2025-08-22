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
import org.eclipse.swt.graphics.RGB;

/**
 * The type represents for the series properties in the chart
 *
 * @author lizhiqiang
 * @version 1.0 - 2010-3-20 created by lizhiqiang
 */
public class ShowSetting implements Cloneable, Serializable {
    private static final long serialVersionUID = -4713505624807829133L;
    private boolean checked;
    private RGB seriesRgb;
    private float width = 1.0f;

    /**
     * Whether is checked
     *
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /** @param checked the checked to set */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * Get the series rgb
     *
     * @return the seriesRgb
     */
    public RGB getSeriesRgb() {
        return seriesRgb;
    }

    /** @param seriesRgb the seriesRgb to set */
    public void setSeriesRgb(RGB seriesRgb) {
        this.seriesRgb = seriesRgb;
    }

    /**
     * Duplicate an instance of this type
     *
     * @return a new instance of this type
     */
    public ShowSetting clone() {
        try {
            return (ShowSetting) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    /**
     * Get the width of series
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /** @param width the width to set */
    public void setWidth(float width) {
        this.width = width;
    }
}
