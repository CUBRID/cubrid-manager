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
package com.cubrid.tool.editor;

import com.cubrid.tool.editor.action.ActionConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * Default Text Editor Contributor.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-2-10 created by Kevin Cao
 */
public class TextEditorContributor extends EditorActionBarContributor {
    /** Current active text editor. */
    protected IEditorPart textEditor;

    /** Retarget actions. */
    protected Map<String, RetargetAction> actions = new HashMap<String, RetargetAction>();

    /** TextEditorContributor constructor. */
    public TextEditorContributor() {
        RetargetAction redoAction = new RetargetAction(ActionFactory.REDO.getId(), "&Redo@Ctrl+Y");
        RetargetAction undoAction = new RetargetAction(ActionFactory.UNDO.getId(), "&Undo@Ctrl+Z");
        undoAction.setAccelerator(SWT.CTRL | 'z');
        redoAction.setAccelerator(SWT.CTRL | 'y');
        actions.put(ActionFactory.UNDO.getId(), undoAction);
        actions.put(ActionFactory.REDO.getId(), redoAction);

        actions.put(
                ActionFactory.CUT.getId(),
                new RetargetAction(ActionFactory.CUT.getId(), "&Cut@Ctrl+X"));
        actions.put(
                ActionFactory.COPY.getId(),
                new RetargetAction(ActionFactory.COPY.getId(), "C&opy@Ctrl+C"));
        actions.put(
                ActionFactory.PASTE.getId(),
                new RetargetAction(ActionFactory.PASTE.getId(), "&Paste@Ctrl+V"));

        actions.put(
                ActionFactory.FIND.getId(),
                new RetargetAction(ActionFactory.FIND.getId(), "Find/Replace@Ctrl+F"));
        actions.put(
                ActionConstants.ACTION_FORMAT,
                new RetargetAction(ActionConstants.ACTION_FORMAT, "Format@Ctrl+Shift+F"));
    }

    /**
     * Contributes to the given menu.
     *
     * @param menuManager the manager that controls the menu
     */
    public void contributeToMenu(IMenuManager menuManager) {
        super.contributeToMenu(menuManager);
        MenuManager drawMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        menuManager.add(drawMenu);
        drawMenu.add(getAction(ActionFactory.UNDO.getId()));
        drawMenu.add(getAction(ActionFactory.REDO.getId()));
        drawMenu.add(new Separator());
        drawMenu.add(getAction(ActionFactory.COPY.getId()));
        drawMenu.add(getAction(ActionFactory.CUT.getId()));
        drawMenu.add(getAction(ActionFactory.PASTE.getId()));
        drawMenu.add(new Separator());
        drawMenu.add(getAction(ActionFactory.FIND.getId()));

        IAction action = getAction(ActionConstants.ACTION_FORMAT);
        if (action != null) {
            drawMenu.add(new Separator());
            drawMenu.add(getAction(ActionConstants.ACTION_FORMAT));
        }
    }

    /**
     * Retrieves the action registered by action id.
     *
     * @param id Action ID.
     * @return Registered action.
     */
    protected IAction getAction(String id) {
        return actions.get(id);
    }

    /**
     * Sets the active editor for the contributor.
     *
     * @param targetEditor the new target editor
     */
    public void setActiveEditor(IEditorPart targetEditor) {
        super.setActiveEditor(targetEditor);
        if (targetEditor == null && textEditor != null) {
            for (RetargetAction action : actions.values()) {
                action.partClosed(textEditor);
            }
            textEditor = null;
        } else if (targetEditor != null) {
            textEditor = targetEditor;
            Object adapter = textEditor.getAdapter(IAction.class);
            if (adapter instanceof Map<?, ?>) {
                registerActiveEditorActions((Map<?, ?>) adapter);
            }
            for (RetargetAction action : actions.values()) {
                action.partActivated(textEditor);
            }
        }
    }

    /**
     * Register Active Editor Actions to GlobalActionHandler of ActionBars.
     *
     * @param editorActions Map<String, IAction>
     */
    protected void registerActiveEditorActions(Map<?, ?> editorActions) {
        for (Entry<?, ?> key : editorActions.entrySet()) {
            getActionBars()
                    .setGlobalActionHandler(key.getKey().toString(), (IAction) key.getValue());
        }
    }

    /** Dispose. */
    public void dispose() {
        setActiveEditor(null);
        super.dispose();
    }
}
