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
package com.cubrid.common.ui.common.navigator;

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Text contribution for select the tree object
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-30 created by pangqiren
 */
public class SelectTreeObjContrItem extends ControlContribution {

    public static final String ID = SelectTreeObjContrItem.class.getName();
    private final TreeViewer tv;
    private CCombo combo;

    public SelectTreeObjContrItem(TreeViewer tv) {
        super(ID);
        this.tv = tv;
    }

    public String getPattern() {
        return combo.getText();
    }

    /**
     * Create the content
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createControl(Composite parent) {
        // Fill in the vertical span for tool bar
        final ToolItem emptyToolItem = new ToolItem((ToolBar) parent, SWT.NONE);
        emptyToolItem.setEnabled(false);
        emptyToolItem.setImage(CommonUIPlugin.getImage("icons/empty.gif"));

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        composite.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        composite.setLayoutData(gridData);

        combo = new CCombo(composite, SWT.BORDER | SWT.LEFT);
        combo.setToolTipText(Messages.tipFind);
        combo.setVisibleItemCount(10);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.widthHint = 95;
        combo.setLayoutData(gridData);
        combo.addKeyListener(
                new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        String str = combo.getText();
                        if (str == null || str.trim().length() == 0) {
                            NodeFilterManager.getInstance().setMatchFilter(null);
                            tv.collapseAll();
                        } else {
                            NodeFilterManager.getInstance().setMatchFilter("*" + str + "*");
                            tv.expandToLevel(2);
                        }
                        tv.refresh();
                        super.keyReleased(e);
                    }
                });

        return composite;
    }
}
