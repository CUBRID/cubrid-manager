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
package com.cubrid.cubridmanager.ui.replication.editor;

import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.DistributorNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.SlaveNode;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

/**
 * This factory class is responsible to create replication component in palette
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public final class PaletteFactory {

    private PaletteFactory() {
        // empty
    }

    /**
     * Create palette component
     *
     * @return paletteRoot
     */
    public static PaletteRoot createPalette() {
        PaletteRoot paletteRoot = new PaletteRoot();
        paletteRoot.addAll(createCategories(paletteRoot));
        return paletteRoot;
    }

    /**
     * create categories
     *
     * @param root PaletteRoot
     * @return categories
     */
    private static List<PaletteContainer> createCategories(PaletteRoot root) {
        List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
        categories.add(createControlGroup(root));
        categories.add(createComponentsDrawer());
        return categories;
    }

    /**
     * create control group
     *
     * @param root PaletteRoot
     * @return componentGroup
     */
    private static PaletteContainer createControlGroup(PaletteRoot root) {
        PaletteGroup componentGroup = new PaletteGroup(Messages.lblComponentGrp);
        List<ToolEntry> toolEntries = new ArrayList<ToolEntry>();

        ToolEntry toolEntry = new SelectionToolEntry();
        toolEntry.setLabel(Messages.lblSelectTool);
        toolEntry.setDescription(Messages.descSelectTool);
        toolEntries.add(toolEntry);
        root.setDefaultEntry(toolEntry);

        toolEntry =
                new ConnectionCreationToolEntry(
                        Messages.lblConnectionTool,
                        Messages.descConnectionTool,
                        null,
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/arrow.gif"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/arrow.gif"));
        toolEntries.add(toolEntry);

        componentGroup.addAll(toolEntries);
        return componentGroup;
    }

    /**
     * create components drawer
     *
     * @return drawer
     */
    private static PaletteContainer createComponentsDrawer() {

        PaletteDrawer drawer = new PaletteDrawer(Messages.lblReplComponent);
        drawer.setLargeIcon(
                CubridManagerUIPlugin.getImageDescriptor("icons/replication/replication.gif"));
        drawer.setSmallIcon(
                CubridManagerUIPlugin.getImageDescriptor("icons/replication/replication.gif"));
        List<ToolEntry> toolEntries = new ArrayList<ToolEntry>();

        ToolEntry toolEntry =
                new CombinedTemplateCreationEntry(
                        Messages.lblHostTool,
                        Messages.descHostTool,
                        HostNode.class,
                        new SimpleFactory(HostNode.class),
                        CubridManagerUIPlugin.getImageDescriptor("icons/navigator/host.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/navigator/host.png"));
        toolEntries.add(toolEntry);

        toolEntry =
                new CombinedTemplateCreationEntry(
                        Messages.lblMasterTool,
                        Messages.descMasterTool,
                        MasterNode.class,
                        new SimpleFactory(MasterNode.class),
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/master.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/master.png"));
        toolEntries.add(toolEntry);

        toolEntry =
                new CombinedTemplateCreationEntry(
                        Messages.lblDistributorTool,
                        Messages.descDistributorTool,
                        DistributorNode.class,
                        new SimpleFactory(DistributorNode.class),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/replication/distributor.gif"),
                        CubridManagerUIPlugin.getImageDescriptor(
                                "icons/replication/distributor.gif"));
        toolEntries.add(toolEntry);

        toolEntry =
                new CombinedTemplateCreationEntry(
                        Messages.lblSlaveTool,
                        Messages.descSlaveTool,
                        SlaveNode.class,
                        new SimpleFactory(SlaveNode.class),
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/slave.png"),
                        CubridManagerUIPlugin.getImageDescriptor("icons/replication/slave.png"));
        toolEntries.add(toolEntry);
        drawer.addAll(toolEntries);
        return drawer;
    }
}
