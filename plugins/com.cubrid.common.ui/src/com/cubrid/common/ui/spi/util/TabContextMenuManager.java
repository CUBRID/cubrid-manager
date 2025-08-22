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
package com.cubrid.common.ui.spi.util;

import com.cubrid.common.ui.query.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

/**
 * Tab context menu manager
 *
 * @author pangqiren
 * @version 1.0 - 2011-4-21 created by pangqiren
 */
public class TabContextMenuManager {
    private final CTabFolder tabFolder;

    public TabContextMenuManager(CTabFolder tabFolder) {
        this.tabFolder = tabFolder;
    }

    public void createContextMenu() {
        tabFolder.addMouseListener(
                new MouseAdapter() {
                    public void mouseDown(MouseEvent event) {
                        int x = event.x;
                        int y = event.y;
                        final CTabItem item = tabFolder.getItem(new Point(x, y));
                        if (item == null) {
                            return;
                        }
                        Widget widget = event.widget;
                        if (!tabFolder.equals(widget)) {
                            return;
                        }
                        tabFolder.setSelection(item);
                        if (event.button == 3) {
                            Point pt = new Point(x, y - 65);
                            pt = tabFolder.toDisplay(pt);
                            MenuManager menuManager = new MenuManager();
                            CloseAction closeAction = new CloseAction(item);
                            CloseOthersAction closeOthersAction = new CloseOthersAction(item);
                            CloseAllAction closeAllAction = new CloseAllAction();
                            menuManager.add(closeAction);
                            menuManager.add(closeOthersAction);
                            menuManager.add(closeAllAction);
                            Menu contextMenu = menuManager.createContextMenu(tabFolder);
                            contextMenu.setLocation(pt.x, pt.y);
                            contextMenu.setVisible(true);
                        }
                    }
                });
    }

    /**
     * Close the tab action
     *
     * @author pangqiren
     * @version 1.0 - 2011-4-21 created by pangqiren
     */
    private class CloseAction extends Action {
        private final CTabItem item;

        public CloseAction(CTabItem item) {
            this.setText(Messages.close);
            this.setId(CloseAction.class.getName());
            this.item = item;
        }

        public void run() {
            BusyIndicator.showWhile(
                    Display.getDefault(),
                    new Runnable() {
                        public void run() {
                            if (item != null) {
                                item.dispose();
                            }
                        }
                    });
        }
    }

    /**
     * Close others tab action
     *
     * @author pangqiren
     * @version 1.0 - 2011-4-21 created by pangqiren
     */
    private class CloseOthersAction extends Action {
        private final CTabItem item;

        public CloseOthersAction(CTabItem item) {
            this.setText(Messages.closeOthers);
            this.setId(CloseOthersAction.class.getName());
            this.item = item;
        }

        public void run() {
            BusyIndicator.showWhile(
                    Display.getDefault(),
                    new Runnable() {
                        public void run() {
                            CTabItem[] items = tabFolder.getItems();
                            for (int i = 0; items != null && i < items.length; i++) {
                                if (!items[i].equals(item)) {
                                    items[i].dispose();
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Close all tabs action
     *
     * @author pangqiren
     * @version 1.0 - 2011-4-21 created by pangqiren
     */
    private class CloseAllAction extends Action {
        public CloseAllAction() {
            this.setText(Messages.closeAll);
            this.setId(CloseAllAction.class.getName());
        }

        public void run() {
            BusyIndicator.showWhile(
                    Display.getDefault(),
                    new Runnable() {
                        public void run() {
                            CTabItem[] items = tabFolder.getItems();
                            for (int i = 0; items != null && i < items.length; i++) {
                                items[i].dispose();
                            }
                        }
                    });
        }
    }
}
