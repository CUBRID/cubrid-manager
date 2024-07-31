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

package com.cubrid.common.ui.query.editor;

import com.cubrid.common.ui.query.control.jface.text.contentassist.ContentAssistant;
import com.cubrid.common.ui.query.control.jface.text.contentassist.IContentAssistant;
import com.cubrid.common.ui.query.format.SqlFormattingStrategy;
import org.eclipse.jface.internal.text.html.HTMLTextPresenter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;

/**
 * This class provides the SQL source viewer configuration
 *
 * @author pangqiren 2009-3-2
 * @author 8.4.1 - 2012-04 modify by fulei
 */
@SuppressWarnings("restriction")
public class SQLViewerConfiguration extends SourceViewerConfiguration {

    private final IDatabaseProvider databaseProvider;
    private SQLContentAssistProcessor contentAssistProcessor;
    private RecentSQLContentAssistProcessor recentlyUsedSQLContentAssistProcessor;

    public SQLViewerConfiguration(IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    /**
     * Gets the presentation reconciler. This will color the code.
     *
     * @param sourceViewer ISourceViewer
     * @return reconciler IPresentationReconciler
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        // Create the presentation reconciler
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

        // Create the damager/repairer for comment partitions
        String[] types = SQLPartitionScanner.getAllTypes();
        for (String type : types) {
            if (IDocument.DEFAULT_CONTENT_TYPE.equals(type)) {
                DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new SQLKeyWordScanner());
                reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
                reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
            } else {
                DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new StringCommentScanner());
                reconciler.setDamager(dr, type);
                reconciler.setRepairer(dr, type);
            }
        }
        return reconciler;
    }

    /**
     * Gets the configured document partitioning
     *
     * @param sourceViewer ISourceViewer
     * @return String
     */
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return ISQLPartitions.SQL_PARTITIONING;
    }

    /**
     * Gets the configured partition types
     *
     * @param sourceViewer ISourceViewer
     * @return String[]
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
            IDocument.DEFAULT_CONTENT_TYPE,
            ISQLPartitions.SQL_MULTI_LINE_COMMENT,
            ISQLPartitions.SQL_STRING,
            ISQLPartitions.SQL_SINGLE_LINE_COMMENT
        };
    }

    /**
     * @see
     *     org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(org.eclipse.jface.text.source.ISourceViewer)
     * @param sourceViewer the source viewer to be configured by this configuration
     * @return a content formatter or <code>null</code> if formatting should not be supported
     */
    public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
        ContentFormatter formatter = new ContentFormatter();
        formatter.setFormattingStrategy(
                new SqlFormattingStrategy(databaseProvider), IDocument.DEFAULT_CONTENT_TYPE);
        return formatter;
    }

    /**
     * Get content assistant
     *
     * @param sourceViewer the source viewer to be configured by this configuration
     * @return a content assistant
     */
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
        //		assistant.setInformationControlCreator(new IInformationControlCreator() {
        //			public IInformationControl createInformationControl(Shell parent) {
        //				DefaultInformationControl control = new DefaultInformationControl(
        //						parent);
        //				return control;
        //			}
        //		});
        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

        final WordTracker tracker = WordTracker.getWordTracker();
        contentAssistProcessor = new SQLContentAssistProcessor(tracker, databaseProvider);

        assistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        return assistant;
    }

    /**
     * get recently used content assistant
     *
     * @param sourceViewer the source viewer to be configured by this configuration
     * @return a content assistant
     */
    public IContentAssistant getRecentlyUsedContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();
        assistant.setInformationControlCreator(
                new IInformationControlCreator() {
                    public IInformationControl createInformationControl(Shell parent) {
                        DefaultInformationControl control = new DefaultInformationControl(parent);
                        return control;
                    }
                });
        recentlyUsedSQLContentAssistProcessor =
                new RecentSQLContentAssistProcessor(databaseProvider);
        assistant.setContentAssistProcessor(
                recentlyUsedSQLContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        return assistant;
    }

    public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent, new HTMLTextPresenter(false));
            }
        };
    }
}
