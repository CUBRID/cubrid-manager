/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.tool.editor.property;

import com.cubrid.tool.editor.ColorManager;
import com.cubrid.tool.editor.DefaultDoubleClickStrategy;
import com.cubrid.tool.editor.NonRuleBasedDamagerRepairer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * XML Configuration for XML editor.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-1-21 created by Kevin Cao
 */
public class PropConfiguration extends SourceViewerConfiguration {
    private DefaultDoubleClickStrategy doubleClickStrategy;
    private PropPartitionScanner commentScanner;
    private final ColorManager colorManager;

    /**
     * Constructor.
     *
     * @param colorManager of XML editor.
     */
    public PropConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    /**
     * Retrieves the Configured Content Types
     *
     * @param sourceViewer ISourceViewer
     * @return String[]
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return PropPartitionScanner.LEGAL_CONTENT_TYPES;
    }

    /**
     * Retrieves the ITextDoubleClickStrategy
     *
     * @param sourceViewer ISourceViewer
     * @param contentType String
     * @return ITextDoubleClickStrategy
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy(
            ISourceViewer sourceViewer, String contentType) {
        if (doubleClickStrategy == null) {
            doubleClickStrategy = new DefaultDoubleClickStrategy();
        }
        return doubleClickStrategy;
    }

    /**
     * Retrieves the XML Tag Scanner.
     *
     * @return tagScanner
     */
    protected PropPartitionScanner getPropertiesCommentScanner() {
        if (commentScanner == null) {
            commentScanner = new PropPartitionScanner();
            commentScanner.setDefaultReturnToken(
                    new Token(
                            new TextAttribute(
                                    colorManager.getColor(IPropColorConstants.PROP_COMMENT))));
        }
        return commentScanner;
    }

    /**
     * Retrieves the Presentation Reconciler
     *
     * @param sourceViewer ISourceViewer
     * @return object of IPresentationReconciler
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        NonRuleBasedDamagerRepairer ndr =
                new NonRuleBasedDamagerRepairer(
                        new TextAttribute(colorManager.getColor(IPropColorConstants.PROP_COMMENT)));
        reconciler.setDamager(ndr, PropPartitionScanner.PROPERTIES_COMMENT);
        reconciler.setRepairer(ndr, PropPartitionScanner.PROPERTIES_COMMENT);

        ndr =
                new NonRuleBasedDamagerRepairer(
                        new TextAttribute(colorManager.getColor(IPropColorConstants.DEFAULT)));
        reconciler.setDamager(ndr, PropPartitionScanner.PROPERTIES_CONTENT);
        reconciler.setRepairer(ndr, PropPartitionScanner.PROPERTIES_CONTENT);

        ndr =
                new NonRuleBasedDamagerRepairer(
                        new TextAttribute(colorManager.getColor(IPropColorConstants.TAG)));
        reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

        return reconciler;
    }
}
