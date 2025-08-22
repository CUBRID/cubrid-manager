/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.er;

import com.cubrid.common.ui.er.action.AbstractSelectionAction;
import com.cubrid.common.ui.er.action.AddColumnAction;
import com.cubrid.common.ui.er.action.DeleteAction;
import com.cubrid.common.ui.er.action.EditTableAction;
import com.cubrid.common.ui.er.action.ImportERwinDataAction;
import com.cubrid.common.ui.er.action.ModifyTableNameAction;
import com.cubrid.common.ui.er.part.ColumnPart;
import com.cubrid.common.ui.er.part.RelationshipPart;
import com.cubrid.common.ui.er.part.TablePart;
import java.util.List;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

/**
 * Provides a context menu for the schema diagram editor. A virtual cut and paste from the flow
 * example
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-6 created by Yu Guojia
 */
public class SchemaContextMenuProvider extends ContextMenuProvider {
    public static String ID = SchemaContextMenuProvider.class.getName();

    private ActionRegistry actionRegistry;

    /**
     * Creates a new FlowContextMenuProvider assoicated with the given viewer and action registry.
     *
     * @param viewer the viewer
     * @param registry the action registry
     */
    public SchemaContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
        super(viewer);
        setActionRegistry(registry);
    }

    @Override
    public void buildContextMenu(IMenuManager menu) {
        if (!(menu instanceof SchemaContextMenuProvider)) {
            return;
        }
        SchemaContextMenuProvider menuProvider = (SchemaContextMenuProvider) menu;
        EditPartViewer viewer = menuProvider.getViewer();
        if (viewer == null) {
            return;
        }

        buildPublicMenuItems(menu);
        List selectParts = viewer.getSelectedEditParts();
        // blank right-click
        if (selectParts == null || selectParts.size() == 0) {
            return;
        }

        // multi-objects right-click
        if (selectParts.size() > 1) {
            buildMultiFiguresMenuItems(menu);
            return;
        }

        // one object right-click
        Object object = selectParts.get(0);
        if (object instanceof TablePart) {
            buildTableMenuItems(menu);
        } else if (object instanceof ColumnPart) {
            buildColumnMenuItems(menu);
        } else if (object instanceof RelationshipPart) {
            buildRelationshipLineMenuItems(menu);
        }
    }

    /**
     * Add actions to multi-parts
     *
     * @param menu
     */
    private void buildMultiFiguresMenuItems(IMenuManager menu) {
        IAction action = getActionRegistry().getAction(DeleteAction.ID);
        action.setText(Messages.actionDelete);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
    }

    /**
     * Add public menu items
     *
     * @param menu
     */
    private void buildPublicMenuItems(IMenuManager menu) {
        IAction action;
        GEFActionConstants.addStandardActionGroups(menu);
        menu.add(new Separator(AbstractSelectionAction.MANAGE_GROUP_ID));
        menu.add(new Separator(AbstractSelectionAction.GLOBAL_GROUP_ID));

        action = getActionRegistry().getAction(ImportERwinDataAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
    }

    /**
     * Add menu items for table
     *
     * @param menu
     */
    private void buildTableMenuItems(IMenuManager menu) {
        IAction action;

        action = getActionRegistry().getAction(EditTableAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
        action = getActionRegistry().getAction(AddColumnAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
        action = getActionRegistry().getAction(ModifyTableNameAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);

        action = getActionRegistry().getAction(DeleteAction.ID);
        action.setText(Messages.actionDeleteTable);
        // action.setImageDescriptor(CommonUIPlugin.getImageDescriptor("icons/action/schema_table_delete.png"));
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
    }

    /**
     * Add menu items for column
     *
     * @param menu
     */
    private void buildColumnMenuItems(IMenuManager menu) {
        IAction action;
        action = getActionRegistry().getAction(EditTableAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
        action = getActionRegistry().getAction(AddColumnAction.ID);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);

        action = getActionRegistry().getAction(DeleteAction.ID);
        action.setText(Messages.actionDeleteColumnName);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
    }

    /**
     * Add menu items for Relationship connection line.
     *
     * @param menu
     */
    private void buildRelationshipLineMenuItems(IMenuManager menu) {
        IAction action;
        action = getActionRegistry().getAction(DeleteAction.ID);
        action.setText(Messages.actionDeleteRelationship);
        menu.appendToGroup(AbstractSelectionAction.MANAGE_GROUP_ID, action);
    }

    private ActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    /**
     * Sets the action registry
     *
     * @param registry the action registry
     */
    public void setActionRegistry(ActionRegistry registry) {
        actionRegistry = registry;
    }
}
