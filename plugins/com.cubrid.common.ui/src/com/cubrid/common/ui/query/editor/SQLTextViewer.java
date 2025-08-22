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
package com.cubrid.common.ui.query.editor;

import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * SQL text viewer
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-25 created by pangqiren
 */
public class SQLTextViewer extends SourceViewer implements ITextListener {

    private ITextListener textListener;
    IVerticalRuler ruler = null;

    public SQLTextViewer(
            Composite parent, IVerticalRuler ruler, int styles, ITextListener textListener) {
        this(parent, ruler, null, false, styles, textListener);
        this.ruler = ruler;
        this.textListener = textListener;
    }

    public SQLTextViewer(
            Composite parent,
            IVerticalRuler verticalRuler,
            IOverviewRuler overviewRuler,
            boolean showAnnotationsOverview,
            int styles,
            ITextListener textListener) {
        super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
        this.textListener = textListener;
        this.ruler = verticalRuler;
        addTextListener(textListener);
    }

    /**
     * Configure the source viewer
     *
     * @param configuration SourceViewerConfiguration
     */
    public void configure(SourceViewerConfiguration configuration) {
        super.configure(configuration);
    }

    public void textChanged(TextEvent event) {
        if (getTextWidget() != null) {
            textListener.textChanged(event);
        }
    }

    /**
     * Set the background color
     *
     * @param color
     */
    public void setBackground(Color color) {
        getControl().setBackground(color);
    }

    public void updateRuler() {
        ruler.update();
    }
}
