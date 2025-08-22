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

import static com.cubrid.common.core.util.NoOp.noOp;
import static com.cubrid.common.ui.CommonUIPlugin.getImage;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridData;
import static com.cubrid.common.ui.spi.util.CommonUITool.createGridLayout;
import static org.eclipse.swt.layout.GridData.FILL_BOTH;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END;

import com.cubrid.common.ui.common.Messages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * This is web browser editor part
 *
 * @author Kevin.Wang
 * @version 1.0 - Apr 23, 2012 created by Kevin.Wang
 */
public class BrowserEditorPart extends EditorPart {
    public static final String ID = "com.cubrid.common.ui.common.control.BrowserEditorPart";
    private Browser browser;
    private Text location;
    private ToolItem goItem;
    private ToolItem refreshItem;
    private ToolItem stopItem;
    private ToolItem backItem;
    private ToolItem forwardItem;

    public void createPartControl(Composite parent) {
        parent.setLayout(createGridLayout(3, 0, 0, 0, 0));
        initUrlBar(parent);
        initToolbar(parent);
        initBrowser(parent);
    }

    private void initUrlBar(Composite parent) {
        Label urlLabel = new Label(parent, SWT.None);
        urlLabel.setLayoutData(createGridData(1, 1, -1, -1));
        urlLabel.setText(Messages.lblLocation);

        location = new Text(parent, SWT.BORDER);
        location.setLayoutData(createGridData(FILL_HORIZONTAL, 1, 1, -1, -1));
        location.addKeyListener(
                new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.character == SWT.LF || e.character == SWT.CR) {
                            e.doit = false;
                            go(location.getText());
                        }
                    }
                });
    }

    private void initBrowser(Composite parent) {
        browser = new Browser(parent, SWT.None);
        browser.setLayoutData(createGridData(FILL_BOTH, 3, 1, -1, -1));

        // Add location change listener
        browser.addLocationListener(
                new LocationListener() {
                    public void changing(LocationEvent e) {
                        location.setText(e.location);
                    }

                    public void changed(LocationEvent e) {
                        noOp();
                    }
                });

        // Add loading listener
        browser.addProgressListener(
                new ProgressListener() {
                    // Set stopItem and progress bar status
                    public void changed(ProgressEvent e) {
                        if (!stopItem.isEnabled() && e.total != e.current) {
                            stopItem.setEnabled(true);
                        }
                    }

                    // Set stopItem,backItem,forwardItem and progress bar status
                    public void completed(ProgressEvent e) {
                        stopItem.setEnabled(false);
                        backItem.setEnabled(browser.isBackEnabled());
                        forwardItem.setEnabled(browser.isForwardEnabled());
                    }
                });
    }

    private void initToolbar(Composite parent) {
        Composite toolbarCom = new Composite(parent, SWT.None);
        toolbarCom.setLayoutData(createGridData(HORIZONTAL_ALIGN_END, 1, 1, -1, -1));
        toolbarCom.setLayout(new FillLayout());

        ToolBar toolBar = new ToolBar(toolbarCom, SWT.FLAT);

        goItem = new ToolItem(toolBar, SWT.CHECK);
        goItem.setImage(getImage("icons/browsereditor/go.gif"));
        goItem.setToolTipText(Messages.tooltipGo);
        goItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        go(location.getText());
                    }
                });

        refreshItem = new ToolItem(toolBar, SWT.CHECK);
        refreshItem.setImage(getImage("icons/browsereditor/refresh.gif"));
        refreshItem.setToolTipText(Messages.tooltipRefresh);
        refreshItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        browser.refresh();
                    }
                });

        stopItem = new ToolItem(toolBar, SWT.CHECK);
        stopItem.setImage(getImage("icons/browsereditor/stop.gif"));
        stopItem.setToolTipText(Messages.tooltipStop);
        stopItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        browser.stop();
                    }
                });

        backItem = new ToolItem(toolBar, SWT.CHECK);
        backItem.setImage(getImage("icons/browsereditor/back.gif"));
        backItem.setToolTipText(Messages.tooltipBack);
        backItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        browser.back();
                    }
                });

        forwardItem = new ToolItem(toolBar, SWT.CHECK);
        forwardItem.setImage(getImage("icons/browsereditor/forward.gif"));
        forwardItem.setToolTipText(Messages.tooltipForward);
        forwardItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        browser.forward();
                    }
                });
    }

    public void go(String url) {
        browser.setUrl(url);
    }

    public void setFocus() {
        noOp();
    }

    public void doSave(IProgressMonitor monitor) {
        noOp();
    }

    public void doSaveAs() {
        noOp();
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setTitleToolTip(input.getToolTipText());
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }
}
