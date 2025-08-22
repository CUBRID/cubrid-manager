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
package com.cubrid.common.ui.common.control;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.net.URL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;

/**
 * CUBRID notice information editor part
 *
 * @author fulei
 * @version 1.0 - 2012-12-04 created by fulei
 */
public class CubridNoticeInfoEditorPart extends EditorPart {
    private static final Logger LOGGER = LogUtil.getLogger(CubridNoticeInfoEditorPart.class);
    public static final String ID = CubridNoticeInfoEditorPart.class.getName();
    private String index = "";
    private String noticeURL = "";
    private CubridNoticeInfoEditorPart editor = this;

    /**
     * Saves the contents of this editor.
     *
     * @param monitor the progress monitor
     */
    public void doSave(IProgressMonitor monitor) {
        noOp();
    }

    /**
     * Saves the contents of this editor to another object.
     *
     * @see IEditorPart
     */
    public void doSaveAs() {
        noOp();
    }

    /**
     * Initializes this editor with the given editor site and input.
     *
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setTitleToolTip(input.getToolTipText());

        String noticeContents = (String) input.getAdapter(String.class);
        String[] content = noticeContents.split("\\|");
        index = content[0];
        noticeURL = content[3];
    }

    public void dispose() {
        noOp();
    }

    /**
     * Return whether the editor is dirty
     *
     * @return <code>true</code> if it is dirty;<code>false</code> otherwise
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * Return whether the save as operation is allowed
     *
     * @return <code>true</code> if it is allowed;<code>false</code> otherwise
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Create the editor content
     *
     * @param parent the parent composite
     */
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        try {
            Button ignoreButton = new Button(composite, SWT.CHECK);
            ignoreButton.setText(Messages.cubridNoticeIgnoreButtonLbl);
            ignoreButton.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            String ignore =
                                    PersistUtils.getPreferenceValue(
                                            CommonUIPlugin.PLUGIN_ID,
                                            CubridNoticeUtil.IGNORE_NOTICE);
                            if (StringUtil.isEmpty(ignore)) {
                                ignore = index;
                            } else {
                                ignore = ignore + "," + index;
                            }
                            PersistUtils.setPreferenceValue(
                                    CommonUIPlugin.PLUGIN_ID,
                                    CubridNoticeUtil.IGNORE_NOTICE,
                                    ignore);
                            getSite()
                                    .getWorkbenchWindow()
                                    .getActivePage()
                                    .closeEditor(editor, false);
                        }
                    });

            Browser browser = new Browser(composite, SWT.NONE);
            browser.setUrl(noticeURL);
            browser.setLayoutData(new GridData(GridData.FILL_BOTH));
        } catch (Exception e) {
            Label label = new Label(parent, SWT.NONE);
            IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
            try {
                IWebBrowser browser = support.getExternalBrowser();
                browser.openURL(new URL(CommonUITool.urlEncodeForSpaces(noticeURL.toCharArray())));
            } catch (Exception browserEx) {
                LOGGER.error(browserEx.getMessage(), browserEx);
                label.setText(Messages.errCannotOpenExternalBrowser);
                return;
            }
            label.setText(Messages.errCannotOpenInternalBrowser);
        }
    }

    public void setFocus() {
        noOp();
    }
}
