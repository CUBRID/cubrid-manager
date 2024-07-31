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

import com.cubrid.common.ui.CommonUIPlugin;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.query.editor.EditorConstance;
import com.cubrid.common.ui.spi.ResourceManager;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * The SelectColorCombox class
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-3-21 created by Kevin.Wang
 */
public class SelectColorCombo extends Composite {
    private CLabel serverTypeLabel;
    private Button dropDownButton;
    private Menu colorMenu;
    private RGB selectedColor;

    /**
     * Constructor
     *
     * @param parent
     * @param style
     * @param selectedColor
     */
    public SelectColorCombo(Composite parent, int style, RGB selectedColor) {
        super(parent, style);
        this.selectedColor = selectedColor;
        this.setLayout(new FormLayout());

        buildServerTypeLabels(selectedColor);
        buildDropDownButton();
        colorMenu = new Menu(getShell(), SWT.POP_UP);
        buildColorMenu(colorMenu);
    }

    /** Build the drop down button of the server type selection. */
    private void buildDropDownButton() {
        dropDownButton = new Button(this, SWT.FLAT | SWT.ARROW | SWT.DOWN);
        dropDownButton.setLayoutData(formDataForDropDown());
        dropDownButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        expandMenu();
                    }
                });
    }

    /**
     * Build the server type labels
     *
     * @param selectedColor
     */
    private void buildServerTypeLabels(RGB selectedColor) {
        serverTypeLabel = new CLabel(this, SWT.None);
        serverTypeLabel.setLayoutData(formDataForLabel());
        if (selectedColor != null) {
            serverTypeLabel.setBackground(ResourceManager.getColor(selectedColor));
        }
        serverTypeLabel.addMouseListener(
                new MouseListener() {
                    public void mouseUp(MouseEvent e) {
                        noOp();
                    }

                    public void mouseDown(MouseEvent e) {
                        expandMenu();
                    }

                    public void mouseDoubleClick(MouseEvent e) {
                        noOp();
                    }
                });

        if (selectedColor != null) {
            String[] labels = {
                Messages.lblColor0,
                Messages.lblColor1,
                Messages.lblColor2,
                Messages.lblColor3,
                Messages.lblColor4,
                Messages.lblColor5
            };
            List<RGB> rgbs = EditorConstance.getAvaliableBackground();
            for (int i = 0; i < labels.length; i++) {
                if (selectedColor.equals(rgbs.get(i))) {
                    serverTypeLabel.setText(labels[i]);
                }
            }
        }
    }

    /**
     * Build the FormData object which is used on the colored label.
     *
     * @return
     */
    private FormData formDataForDropDown() {
        final FormData dropDownButtonData = new FormData();
        dropDownButtonData.top = new FormAttachment(0, 0);
        dropDownButtonData.bottom = new FormAttachment(100, 0);
        dropDownButtonData.left = new FormAttachment(80, 0);
        dropDownButtonData.right = new FormAttachment(100, 0);
        return dropDownButtonData;
    }

    /**
     * Build the FormData object which is used on the colored label.
     *
     * @return
     */
    private FormData formDataForLabel() {
        FormData backgroundData = new FormData();
        backgroundData.top = new FormAttachment(0, 0);
        backgroundData.bottom = new FormAttachment(100, 0);
        backgroundData.left = new FormAttachment(0, 0);
        backgroundData.right = new FormAttachment(80, 0);
        return backgroundData;
    }

    /** Expand the menu */
    public void expandMenu() {
        if (colorMenu.isVisible()) {
            colorMenu.setVisible(false);
        }
        Rectangle rect = serverTypeLabel.getBounds();
        Point pt = serverTypeLabel.toDisplay(0, rect.height);
        colorMenu.setLocation(pt);
        colorMenu.setVisible(true);
    }

    /**
     * Build the color menu
     *
     * @param menu
     * @return Menu
     */
    private Menu buildColorMenu(Menu menu) {
        final String[] messages = {
            Messages.menuColor0,
            Messages.menuColor1,
            Messages.menuColor2,
            Messages.menuColor3,
            Messages.menuColor4,
            Messages.menuColor5
        };
        for (int i = 0; i < messages.length; i++) {
            buildColorMenuItem(menu, i, messages[i]);
        }

        return menu;
    }

    /**
     * Create the menuitem which would be used on the host type.
     *
     * @param menu
     * @param colorIndex
     * @param message
     * @return
     */
    private MenuItem buildColorMenuItem(
            final Menu menu, final int colorIndex, final String message) {
        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
        menuItem.setText(message);
        menuItem.setImage(CommonUIPlugin.getImage("icons/control/color_" + colorIndex + ".GIF"));
        menuItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        selectedColor = EditorConstance.getAvaliableBackground().get(colorIndex);
                        serverTypeLabel.setBackground(ResourceManager.getColor(selectedColor));
                        serverTypeLabel.setText(message);
                    }
                });

        return menuItem;
    }

    /**
     * Return the selected color.
     *
     * @return
     */
    public RGB getSelectedColor() {
        return selectedColor;
    }
}
