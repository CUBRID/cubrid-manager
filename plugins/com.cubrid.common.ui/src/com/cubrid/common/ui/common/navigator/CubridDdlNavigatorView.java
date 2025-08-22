/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.util.CommonUITool;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class CubridDdlNavigatorView extends ViewPart {
    public static final String ID = "com.cubrid.common.navigator.ddl";
    private StyledText sqlText;

    public void createPartControl(Composite parent) {
        sqlText =
                new StyledText(
                        parent,
                        SWT.V_SCROLL
                                | SWT.READ_ONLY
                                | SWT.H_SCROLL
                                | SWT.BORDER
                                | SWT.WRAP
                                | SWT.MULTI);
        {
            sqlText.setToolTipText(com.cubrid.common.ui.common.Messages.miniSchemaCopyDdlTooltip);
            final MenuManager menuManager = new MenuManager();
            menuManager.setRemoveAllWhenShown(true);
            final Menu contextMenu = menuManager.createContextMenu(sqlText);
            sqlText.setMenu(contextMenu);
            final Menu copyMenu =
                    new Menu(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            SWT.POP_UP);
            sqlText.setMenu(copyMenu);
            sqlText.addKeyListener(
                    new org.eclipse.swt.events.KeyAdapter() {
                        public void keyPressed(org.eclipse.swt.events.KeyEvent event) {
                            if ((event.stateMask & SWT.CTRL) != 0
                                    && (event.stateMask & SWT.SHIFT) == 0
                                    && event.keyCode == 'c') {
                                String text = sqlText.getSelectionText();
                                if (text == null || text.length() == 0) {
                                    text = sqlText.getText().trim();
                                }
                                CommonUITool.copyContentToClipboard(text);
                            }
                        }
                    });

            final MenuItem copyMenuItem = new MenuItem(copyMenu, SWT.PUSH);
            copyMenuItem.setText(com.cubrid.common.ui.common.Messages.miniSchemaCopyDdl);
            copyMenuItem.addSelectionListener(
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            String text = sqlText.getSelectionText();
                            if (text == null || text.length() == 0) {
                                text = sqlText.getText().trim();
                            }
                            CommonUITool.copyContentToClipboard(text);
                        }
                    });
        }
    }

    public void setFocus() {
        CubridNavigatorView mainNav = CubridNavigatorView.findNavigationView();
        if (mainNav != null) {
            String ddl = mainNav.getCurrentSchemaDDL();
            updateView(StringUtil.nvl(ddl));
        }
    }

    public void updateView(String ddl) {
        sqlText.setText(ddl);
    }

    public void cleanView() {
        sqlText.setText("");
    }

    public static CubridDdlNavigatorView getInstance() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return null;
        }

        IWorkbenchPage page = window.getActivePage();
        if (page == null) {
            return null;
        }

        IViewReference viewReference = page.findViewReference(ID);
        if (viewReference != null) {
            IViewPart viewPart = viewReference.getView(false);
            return viewPart instanceof CubridDdlNavigatorView
                    ? (CubridDdlNavigatorView) viewPart
                    : null;
        }

        return null;
    }
}
