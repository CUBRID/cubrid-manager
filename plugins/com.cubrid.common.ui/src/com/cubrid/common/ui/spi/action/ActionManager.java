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
package com.cubrid.common.ui.spi.action;

import com.cubrid.common.ui.spi.model.ICubridNode;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * A action manager is responsible for managing all actions in menu bar and tool bar.
 *
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public final class ActionManager implements ISelectionChangedListener {

    private static final ActionManager MANAGER = new ActionManager();
    private final Map<String, ISelectionAction> selectionActions =
            new HashMap<String, ISelectionAction>();
    private final Map<String, IFocusAction> foucsActions = new HashMap<String, IFocusAction>();
    private final Map<String, IAction> actions = new HashMap<String, IAction>();
    private ISelectionProvider selectionProvider = null;
    private Control focusProvider = null;
    private IMenuProvider menuProvider = null;

    /** The constructor */
    private ActionManager() {}

    /**
     * Return the only action manager instance
     *
     * @return the ActionManager instance
     */
    public static ActionManager getInstance() {
        return MANAGER;
    }

    public IMenuProvider getMenuProvider() {
        return menuProvider;
    }

    public void setMenuProvider(IMenuProvider menuProvider) {
        this.menuProvider = menuProvider;
    }

    /**
     * Register action
     *
     * @param action the action object
     */
    public void registerAction(IAction action) {
        synchronized (this) {
            if (action != null && action.getId() != null && action.getId().trim().length() > 0) {
                if (action instanceof ISelectionAction) {
                    selectionActions.put(action.getId(), (ISelectionAction) action);
                    ((ISelectionAction) action).setSelectionProvider(selectionProvider);
                } else if (action instanceof IFocusAction) {
                    foucsActions.put(action.getId(), (IFocusAction) action);
                    ((IFocusAction) action).setFocusProvider(this.focusProvider);
                } else {
                    actions.put(action.getId(), action);
                }
            }
        }
    }

    /**
     * Get SelectionProvider
     *
     * @return the selection provider
     */
    public ISelectionProvider getSelectionProvider() {
        return this.selectionProvider;
    }

    /**
     * Get FocusProvider
     *
     * @return the focus provider
     */
    public Control getFocusProvider() {
        return this.focusProvider;
    }

    /**
     * Get registered action by action ID
     *
     * @param id the action id
     * @return the action object
     */
    public IAction getAction(String id) {
        if (id != null && id.trim().length() > 0) {
            IAction action = selectionActions.get(id);
            if (action != null) {
                return action;
            }
            action = foucsActions.get(id);
            if (action != null) {
                return action;
            }
            return actions.get(id);
        }
        return null;
    }

    /**
     * Change focus provider for IFocusAction and add action to focus provider to listen to focus
     * changed event
     *
     * @param control the focus provider
     */
    public void changeFocusProvider(Control control) {
        for (IFocusAction action : foucsActions.values()) {
            action.setFocusProvider(control);
        }
        focusProvider = control;
    }

    /**
     * Change selection provider for ISelectionAction and add action to selection provider to listen
     * to selection changed event
     *
     * @param provider the selection provider
     */
    public void changeSelectionProvider(ISelectionProvider provider) {
        for (ISelectionAction action : selectionActions.values()) {
            action.setSelectionProvider(provider);
        }
        if (provider != null) {
            if (this.selectionProvider != null) {
                this.selectionProvider.removeSelectionChangedListener(this);
            }
            this.selectionProvider = provider;
            this.selectionProvider.addSelectionChangedListener(this);
            changeActionMenu();
        }
    }

    /**
     * Fire selection change event
     *
     * @param selection the selection object
     */
    public void fireSelectionChanged(ISelection selection) {
        SelectionChangedEvent event = new SelectionChangedEvent(getSelectionProvider(), selection);
        for (ISelectionAction action : selectionActions.values()) {
            action.selectionChanged(event);
        }
        selectionChanged(event);
    }

    /**
     * Fill in action menu,build action menubar and navigator context menu shared action
     *
     * @param manager the IMenuManager object
     */
    public void setActionsMenu(IMenuManager manager) {
        if (this.selectionProvider == null) {
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
        if (selection == null || selection.isEmpty()) {
            return;
        }
        ICubridNode node = null;
        Object obj = selection.getFirstElement();
        if (obj instanceof ICubridNode) {
            node = (ICubridNode) obj;
        } else {
            return;
        }
        menuProvider.buildMenu(manager, node);
    }

    /**
     * Add action to menu manager
     *
     * @param manager the menu manager object
     * @param action the action object
     */
    public static void addActionToManager(IMenuManager manager, IAction action) {
        if (action != null) {
            manager.add(action);
        }
    }

    /**
     * Add action to menu manager
     *
     * @param manager the menu manager object
     * @param actionId String
     */
    public static void addActionToManager(IMenuManager manager, String actionId) {
        IAction action = ActionManager.getInstance().getAction(actionId);
        if (action != null) {
            manager.add(action);
        }
    }

    /**
     * Notifies that the selection has changed.
     *
     * @param event the selection changed event
     */
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof IStructuredSelection)) {
            return;
        }
        changeActionMenu();
    }

    /** Change action menu */
    private void changeActionMenu() {
        WorkbenchWindow window =
                (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IMenuManager menuBarManager = window.getMenuBarManager();
        if (menuBarManager == null) {
            return;
        }
        IMenuManager actionMenuManager =
                menuBarManager.findMenuUsingPath(IActionConstants.MENU_ACTION);
        if (actionMenuManager != null) {
            actionMenuManager.removeAll();
            setActionsMenu(actionMenuManager);
            menuBarManager.update(true);
        }
    }
}
