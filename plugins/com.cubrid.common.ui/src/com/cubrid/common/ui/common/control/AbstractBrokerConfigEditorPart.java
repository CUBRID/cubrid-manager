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
package com.cubrid.common.ui.common.control;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.ui.spi.model.BrokerConfig;
import com.cubrid.common.ui.spi.persist.BrokerConfPersistUtil;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * Broker config base editor part
 *
 * @author fulei
 * @version 1.0 - 2012-11-8 created by fulei
 */
public abstract class AbstractBrokerConfigEditorPart extends EditorPart {
    public final BrokerConfPersistUtil brokerConfPersistUtil = new BrokerConfPersistUtil();
    public BrokerConfigEditComposite editorComp;
    public ToolItem addPropItem;
    public ToolItem deletePropItem;
    public String defaultExportFilePath;
    public String defaultExportFileName;
    public String defaultExportFileExtName;
    public String defaultExportFileCharset;
    public boolean isDirty = false;

    /**
     * Create part controls
     *
     * @param parent of the controls
     */
    public abstract void createPartControl(Composite parent);

    public abstract void createToolBarComp(Composite parent);

    public abstract BrokerConfig parseCommonTableValueToBrokerConfig(
            List<Map<String, String>> dataList, int tableColumnCount);

    public abstract BrokerConfig parseStringLineToBrokerConfig(String content);

    /**
     * Parse a CubridBrokerConfig model to a document string
     *
     * @param config CubridBrokerConfig
     * @return String
     */
    public abstract String parseBrokerConfigToDocumnetString(BrokerConfig config);

    /**
     * Parse CubridBrokerConfig model to common table value
     *
     * @param config
     * @return
     */
    public abstract List<Map<String, String>> parseBrokerConfigToCommonTableValue(
            BrokerConfig config);

    public abstract void init(IEditorSite site, IEditorInput input) throws PartInitException;

    public void doSaveAs() {
        noOp();
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /**
     * Set edit table item enable
     *
     * @param enabled
     */
    public void setEditTableItemEnabled(boolean enabled) {
        addPropItem.setEnabled(enabled);
        deletePropItem.setEnabled(enabled);
    }

    public void saveBrokerConfAsFile() {
        noOp();
    }

    public void doSave(IProgressMonitor monitor) {
        firePropertyChange(PROP_DIRTY);
    }

    public void fireChanged() {
        firePropertyChange(PROP_DIRTY);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public boolean isSaveAsAllowed() {
        return true;
    }

    public void setFocus() {
        noOp();
    }

    public BrokerConfPersistUtil getBrokerConfPersistUtil() {
        return brokerConfPersistUtil;
    }
}
