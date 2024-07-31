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
package com.cubrid.common.ui.er.editor;

import com.cubrid.common.ui.er.Messages;
import com.cubrid.common.ui.er.SchemaEditorUIPlugin;
import com.cubrid.common.ui.er.dnd.DataElementFactory;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.er.model.ERTable;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Encapsulates functionality to create the PaletteViewer toolbox
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-19 created by Yu Guojia
 */
public class PaletteViewerCreator {
    private PaletteRoot paletteRoot;
    private static ConnectionCreationToolEntry connectionEntry;

    /**
     * Create palette for ER canvas
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public PaletteRoot createPaletteRoot(ERSchema erSchema) {
        paletteRoot = new PaletteRoot();
        paletteRoot.setLabel("");
        paletteRoot.setSmallIcon(null);
        paletteRoot.setLargeIcon(null);

        PaletteGroup controls = new PaletteGroup("Controls");
        paletteRoot.add(controls);

        ToolEntry tool = new SelectionToolEntry();
        controls.add(tool);
        paletteRoot.setDefaultEntry(tool);
        controls.add(new MarqueeToolEntry());

        PaletteDrawer drawer = new PaletteDrawer("New Component", null);

        List entries = new ArrayList();

        ConnectionCreationToolEntry connection = getConnectionEntry();
        CombinedTemplateCreationEntry tableEntry = getTableEntry(erSchema);

        entries.add(connection);
        entries.add(tableEntry);
        drawer.addAll(entries);
        paletteRoot.add(drawer);

        return paletteRoot;
    }

    public static ConnectionCreationToolEntry getConnectionEntry() {
        if (connectionEntry == null) {
            connectionEntry =
                    new ConnectionCreationToolEntry(
                            "Connection",
                            Messages.btnTipConnection,
                            null,
                            AbstractUIPlugin.imageDescriptorFromPlugin(
                                    SchemaEditorUIPlugin.PLUGIN_ID,
                                    "icons/er/new_relationship.png"),
                            AbstractUIPlugin.imageDescriptorFromPlugin(
                                    SchemaEditorUIPlugin.PLUGIN_ID,
                                    "icons/er/new_relationship.png"));
            connectionEntry.setToolClass(ERConnectionCreationTool.class);
        }
        return connectionEntry;
    }

    public static CombinedTemplateCreationEntry getTableEntry(ERSchema erSchema) {
        return new CombinedTemplateCreationEntry(
                "New Table",
                Messages.btnTipCreateTable,
                ERTable.class,
                new DataElementFactory(ERTable.class, erSchema),
                AbstractUIPlugin.imageDescriptorFromPlugin(
                        SchemaEditorUIPlugin.PLUGIN_ID, "icons/er/new_table.png"),
                AbstractUIPlugin.imageDescriptorFromPlugin(
                        SchemaEditorUIPlugin.PLUGIN_ID, "icons/er/new_table.png"));
    }

    public PaletteRoot getPaletteRoot() {
        return paletteRoot;
    }
}
