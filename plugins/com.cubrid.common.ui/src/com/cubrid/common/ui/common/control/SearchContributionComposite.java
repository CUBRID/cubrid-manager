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
package com.cubrid.common.ui.common.control;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

/**
 * The Search Contribution
 *
 * @author Kevin.Wang
 * @version 1.0 - Apr 23, 2012 created by Kevin.Wang
 */
public class SearchContributionComposite extends Composite {
    private static final Logger LOGGER = LogUtil.getLogger(SearchContributionComposite.class);
    private Text text;
    private Button searchButton;

    /**
     * SearchContributionComposite constructor
     *
     * @param parent
     * @param style
     */
    public SearchContributionComposite(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());

        boolean isMac = Util.isMac();
        final Composite group = isMac ? new Composite(this, SWT.None) : new Group(this, SWT.None);
        group.setLayout(new GridLayout(5, false));

        text = new Text(group, SWT.FILL | SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
        text.setToolTipText(Messages.lblSearchDesc);
        final GridData gridData = new GridData();
        gridData.widthHint = 190;
        text.setLayoutData(gridData);
        text.setMessage(Messages.msgSearchKeyword);

        text.addKeyListener(
                new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        // Handle the press of the Enter key in the locationText.
                        // This will browse to the entered text.
                        if (e.character == SWT.LF || e.character == SWT.CR) {
                            e.doit = false;
                            processSearch();
                        }
                    }
                });

        if (!isMac) {
            searchButton = new Button(group, SWT.None);
            searchButton.setImage(CommonUIPlugin.getImage("icons/control/search.png"));
            searchButton.setToolTipText(Messages.btnSearchTooltip);
            searchButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent e) {
                            processSearch();
                        }
                    });
        }
    }

    public void processSearch() {
        if (text.getText().length() == 0) {
            return;
        }

        String key = StringUtil.urlencode(text.getText(), "UTF-8");
        if (key == null) {
            LOGGER.error("Encode key word error");
            return;
        }

        String url = getUrl(key);
        BrowserEditorPart browserViewPart = null;

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
        for (IEditorReference reference : editorReferences) {
            if (reference.getId().equals(BrowserEditorPart.ID)) {
                browserViewPart = (BrowserEditorPart) reference.getEditor(true);
            }
        }

        if (browserViewPart == null) {
            try {
                browserViewPart =
                        (BrowserEditorPart)
                                PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow()
                                        .getActivePage()
                                        .openEditor(
                                                new BrowserEditorPartInput(), BrowserEditorPart.ID);
            } catch (PartInitException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (browserViewPart != null) {
            browserViewPart.go(url);
            // For bug TOOLS-1014
            window.getActivePage().activate(browserViewPart);
        }
    }

    /**
     * Get the url
     *
     * @param keyword
     * @return
     */
    private String getUrl(String keyword) {
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.urlSearch);
        sb.append(keyword);
        return sb.toString();
    }
}
