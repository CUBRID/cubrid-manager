/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.action;

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.ISelectionAction;
import com.cubrid.common.ui.spi.action.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Proxy action for the selection action, by this action you can change the selected action
 * style(For example: text, icon, disabled icon), but the behavior is the same
 *
 * @author pangqiren
 * @version 1.0 - 2010-11-19 created by pangqiren
 * @version 1.1 - 2012-09-05 created by Isaiah Choe
 */
public class ProxyAction extends SelectionAction {

    public static final String POSTFIX_ID = ".proxy";
    private final String proxyActionId;

    public ProxyAction(
            String proxyActionId, String text, ImageDescriptor icon, ImageDescriptor disabledIcon) {
        super(null, null, text, icon);
        if (disabledIcon != null) {
            setDisabledImageDescriptor(disabledIcon);
        }

        this.proxyActionId = proxyActionId;
        setId(proxyActionId + POSTFIX_ID);
        setToolTipText(text);
    }

    public ProxyAction(
            String proxyActionId,
            String text,
            int style,
            ImageDescriptor icon,
            ImageDescriptor disabledIcon) {
        super(null, null, text, style, icon);
        if (disabledIcon != null) {
            setDisabledImageDescriptor(disabledIcon);
        }

        this.proxyActionId = proxyActionId;
        setId(proxyActionId + POSTFIX_ID);
        setToolTipText(text);
    }

    public boolean allowMultiSelections() {
        IAction action = ActionManager.getInstance().getAction(proxyActionId);
        if (action instanceof ISelectionAction) {
            return ((ISelectionAction) action).allowMultiSelections();
        }
        return true;
    }

    public boolean isSupported(Object obj) {
        IAction action = ActionManager.getInstance().getAction(proxyActionId);
        if (action instanceof ISelectionAction) {
            return ((ISelectionAction) action).isSupported(obj);
        }
        return true;
    }

    public void runWithEvent(Event event) {
        if (event.detail == SWT.ARROW) {
            Widget widget = event.widget;
            if (widget instanceof ToolItem) {
                ToolItem toolItem = (ToolItem) widget;
                Composite parent = toolItem.getParent();
                Rectangle rect = toolItem.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = parent.toDisplay(pt);
                MenuManager menuManager = new MenuManager();
                ActionManager.getInstance().setActionsMenu(menuManager);
                Menu contextMenu = menuManager.createContextMenu(parent);
                contextMenu.setLocation(pt.x, pt.y);
                contextMenu.setVisible(true);
            }
        } else {
            run();
        }
    }

    public void run() {
        IAction action = ActionManager.getInstance().getAction(proxyActionId);
        if (action instanceof ISelectionAction) {
            action.run();
        }
    }
}
