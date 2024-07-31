/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.ui.er.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Navigator menu for converting between logical model and physical model.
 *
 * @author Yu Guojia
 * @version 1.0 - 2014-5-15 created by Yu Guojia
 */
public class PhysicalLogicalNavigatorMenu {

    protected Menu modelSelectionMenu;

    protected Listener listener;
    protected MenuItem physicalMenuItem;
    protected MenuItem logicalMenuItem;
    /*Current selected menu item*/
    protected MenuItem selectedMenuItem;
    protected Composite parent = null;
    private ERSchemaEditor editor;
    protected CLabel modelLabel;

    public PhysicalLogicalNavigatorMenu(
            Composite parent, ERSchemaEditor editor, CLabel modelLabel) {
        this.editor = editor;
        modelSelectionMenu = new Menu(parent.getShell(), SWT.POP_UP);
        this.modelLabel = modelLabel;

        physicalMenuItem = new MenuItem(modelSelectionMenu, SWT.RADIO);
        physicalMenuItem.setText(Messages.physicalModeltItemName);
        physicalMenuItem.setSelection(true);
        selectedMenuItem = physicalMenuItem; // default item
        this.modelLabel.setText(physicalMenuItem.getText());

        logicalMenuItem = new MenuItem(modelSelectionMenu, SWT.RADIO);
        logicalMenuItem.setText(Messages.logicalModeltItemName);

        addSelectionListener();
    }

    public boolean isSelectedPhysical() {
        return selectedMenuItem.equals(physicalMenuItem);
    }

    protected MenuItem[] getAllMenuNodes() {
        List<MenuItem> allNodes = new ArrayList<MenuItem>();
        allNodes.add(physicalMenuItem);
        allNodes.add(logicalMenuItem);
        return allNodes.toArray(new MenuItem[0]);
    }

    /** Add selection listener */
    protected void addSelectionListener() {
        MenuItem[] allNodes = getAllMenuNodes();
        for (final MenuItem item : allNodes) {
            item.addSelectionListener(
                    new SelectionListener() {
                        public void widgetSelected(SelectionEvent event) {
                            widgetDefaultSelected(event);
                        }

                        public void widgetDefaultSelected(SelectionEvent e) {
                            if (selectedMenuItem.equals(item)) {
                                return;
                            }

                            if (isSelectedPhysical()) { // now select to logical model
                                editor.getERSchema().setPhysicModel(false);
                            } else {
                                editor.getERSchema().setPhysicModel(true);
                            }
                            editor.getERSchema().FireModelViewChanged();

                            selectedMenuItem = item;
                            setText();
                        }
                    });
        }
    }

    /**
     * Set the label text.
     *
     * @param item MenuItem
     */
    public void setText() {
        String text = selectedMenuItem.getText();
        modelLabel.setText(text);
        modelLabel.setToolTipText(text);
    }

    public void setSelectLabel(CLabel label) {
        this.modelLabel = label;
    }

    public Menu getModelSelectionMenu() {
        return modelSelectionMenu;
    }

    public void setModelSelectionMenu(Menu dbSelectionMenu) {
        this.modelSelectionMenu = dbSelectionMenu;
    }
}
