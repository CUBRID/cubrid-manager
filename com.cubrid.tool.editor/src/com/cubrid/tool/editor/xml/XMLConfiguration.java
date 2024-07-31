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
package com.cubrid.tool.editor.xml;

import com.cubrid.tool.editor.ColorManager;
import com.cubrid.tool.editor.DefaultDoubleClickStrategy;
import com.cubrid.tool.editor.NonRuleBasedDamagerRepairer;
import com.cubrid.tool.editor.xml.scanner.XMLPartitionScanner;
import com.cubrid.tool.editor.xml.scanner.XMLTagDamageRepairScanner;
import com.cubrid.tool.editor.xml.scanner.XMLTitleScanner;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * XML Configuration for XML editor.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-1-21 created by Kevin Cao
 */
public class XMLConfiguration extends SourceViewerConfiguration {
    private DefaultDoubleClickStrategy doubleClickStrategy;
    private RuleBasedScanner tagScanner;
    private XMLTitleScanner scanner;
    private final ColorManager colorManager;

    /**
     * Constructor.
     *
     * @param colorManager of XML editor.
     */
    public XMLConfiguration(ColorManager colorManager) {
        this.colorManager = colorManager;
    }

    /**
     * Retrieves the Configured Content Types
     *
     * @param sourceViewer ISourceViewer
     * @return String[]
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
            IDocument.DEFAULT_CONTENT_TYPE,
            XMLPartitionScanner.XML_COMMENT,
            XMLPartitionScanner.XML_TAG
        };
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
     * Retrieves the XML Scanner.
     *
     * @return scanner
     */
    protected XMLTitleScanner getXMLScanner() {
        if (scanner == null) {
            scanner = new XMLTitleScanner(colorManager);
            scanner.setDefaultReturnToken(
                    new Token(
                            new TextAttribute(colorManager.getColor(IXMLColorConstants.DEFAULT))));
        }
        return scanner;
    }

    /**
     * Retrieves the XML Tag Scanner.
     *
     * @return tagScanner
     */
    protected RuleBasedScanner getXMLTagScanner() {
        if (tagScanner == null) {
            tagScanner = new XMLTagDamageRepairScanner(colorManager);
            tagScanner.setDefaultReturnToken(
                    new Token(new TextAttribute(colorManager.getColor(IXMLColorConstants.ATTR))));
        }
        return tagScanner;
    }

    /**
     * Retrieves the Presentation Reconciler
     *
     * @param sourceViewer ISourceViewer
     * @return object of IPresentationReconciler
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getXMLTagScanner());
        reconciler.setDamager(dr, XMLPartitionScanner.XML_TAG);
        reconciler.setRepairer(dr, XMLPartitionScanner.XML_TAG);

        dr = new DefaultDamagerRepairer(getXMLScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        NonRuleBasedDamagerRepairer ndr =
                new NonRuleBasedDamagerRepairer(
                        new TextAttribute(colorManager.getColor(IXMLColorConstants.XML_COMMENT)));
        reconciler.setDamager(ndr, XMLPartitionScanner.XML_COMMENT);
        reconciler.setRepairer(ndr, XMLPartitionScanner.XML_COMMENT);

        return reconciler;
    }
}
