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
package com.cubrid.cubridmanager.ui.cubrid.database.control;

import java.util.List;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

/**
 * pie renderer
 *
 * @author lizhiqing
 * @version 1.0 - 2009-12-28 created by lizhiqing
 */
public class PieRenderer {
    /*
     * Declaring an array of Color variables for storing a list of Colors
     */
    private final java.awt.Color[] color;

    /* Constructor to initialize PieRenderer class */
    public PieRenderer(java.awt.Color[] color) {
        this.color = color;
    }

    /**
     * * Set Method to set colors for pie sections based on our choice*
     *
     * @param plot PiePlot of PieChart*
     * @param dataset PieChart DataSet
     */
    @SuppressWarnings("rawtypes")
    public void setColor(PiePlot3D plot, DefaultPieDataset dataset) {
        if (plot == null || dataset == null) {
            return;
        }

        List keys = dataset.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            plot.setSectionPaint(dataset.getKey(i), this.color[i % this.color.length]);
        }
    }
}
