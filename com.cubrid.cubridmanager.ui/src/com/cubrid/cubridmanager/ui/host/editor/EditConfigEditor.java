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
package com.cubrid.cubridmanager.ui.host.editor;

import static com.cubrid.common.core.util.NoOp.noOp;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.part.CubridEditorPart;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.action.ConfEditInput;
import com.cubrid.cubridmanager.ui.host.dialog.ConfigType;
import com.cubrid.cubridmanager.ui.host.dialog.ExportConfigDialog;
import com.cubrid.cubridmanager.ui.host.dialog.ImportConfigDialog;
import com.cubrid.cubridmanager.ui.spi.util.ConfigParaHelp;
import com.cubrid.tool.editor.property.PropEditor;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;

/**
 * This editor is responsible for edit property
 *
 * @author lizhiqiang
 * @version 1.0 - 2011-3-25 created by lizhiqiang
 */
public class EditConfigEditor extends CubridEditorPart {
    public static final Logger LOGGER = LogUtil.getLogger(EditConfigEditor.class);
    boolean isDirty;
    protected PropEditor propEditor;
    private String defaultExportFilePath;
    private String defaultExportFileName;
    private String defaultExportFileExtName;
    private String defaultExportFileCharset;
    private String defaultImportFileName;
    private String defaultImportFileCharset;

    protected List<String> contents;

    /**
     * @see com.cubrid.common.ui.spi.part.CubridEditorPart#init(org.eclipse.ui.IEditorSite,
     *     org.eclipse.ui.IEditorInput)
     * @param site the editor site
     * @param input the editor input
     * @exception PartInitException if this editor was not initialized successfully
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        if (input instanceof ConfEditInput) {
            setSite(site);
            setInput(input);
            ConfEditInput editInput = (ConfEditInput) input;
            String title = this.getPartName();
            String partName =
                    title
                            + " - "
                            + input.getName()
                            + "@"
                            + editInput.getServerName()
                            + ":"
                            + editInput.getServerPort();
            this.setTitleToolTip(partName);
            this.setPartName(partName);
        }
    }

    /**
     * @param monitor the IProgressMonitor
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {
        isDirty = false;
        firePropertyChange(PROP_DIRTY);
    }

    public boolean isSaveAllowed() {
        return CommonUITool.openConfirmBox(Messages.msgConfirmImport);
    }

    /** @see org.eclipse.ui.part.EditorPart#doSaveAs() */
    public void doSaveAs() {
        // empty
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     * @return boolean
     */
    public boolean isDirty() {
        return isDirty;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     * @return boolean
     */
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Create the part Control
     *
     * @param parent the Composite
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        Composite topComp = new Composite(parent, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            gridLayout.verticalSpacing = 0;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            topComp.setLayout(gridLayout);
            topComp.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        }
        createToolBar(topComp);

        createPropEditor(topComp);
        createContent();
        propEditor.getDocument().addDocumentListener(new DocumentAdpater());
    }

    /**
     * Create the property editor
     *
     * @param parent the Composite
     */
    private void createPropEditor(Composite parent) {
        final Composite editorComp = new Composite(parent, SWT.NONE);
        {
            editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            gridLayout.marginWidth = 0;
            editorComp.setLayout(gridLayout);
        }
        propEditor = new PropEditor();
        try {
            propEditor.init(this.getEditorSite(), this.getEditorInput());
        } catch (PartInitException ex) {
            LOGGER.error(ex.getMessage());
        }
        propEditor.createPartControl(editorComp);
    }

    /** Create the content */
    protected void createContent() {
        StringBuffer sb = new StringBuffer();
        for (String line : contents) {
            sb.append(line);
            sb.append(StringUtil.NEWLINE);
        }
        propEditor.setDocumentString(sb.toString());
    }

    /**
     * Create the tool bar
     *
     * @param parent the Composite
     */
    private void createToolBar(Composite parent) {
        final Composite toolBarComp = new Composite(parent, SWT.NONE);
        {
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            toolBarComp.setLayoutData(gd);
            GridLayout gridLayout = new GridLayout();
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            gridLayout.marginWidth = 0;
            toolBarComp.setLayout(gridLayout);
        }
        ToolBar toolBar = new ToolBar(toolBarComp, SWT.FLAT);
        ToolItem saveItem = new ToolItem(toolBar, SWT.PUSH);
        saveItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_save.png"));
        saveItem.setToolTipText(Messages.msgTipSaveAction);
        saveItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        if (isDirty) {
                            doSave(new NullProgressMonitor());
                        }
                    }
                });

        new ToolItem(toolBar, SWT.SEPARATOR);

        ToolItem importItem = new ToolItem(toolBar, SWT.PUSH);
        importItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_open.png"));
        importItem.setToolTipText(Messages.msgTipOpenAction);
        importItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        doImport();
                    }
                });
        ToolItem exportItem = new ToolItem(toolBar, SWT.PUSH);
        exportItem.setImage(CubridManagerUIPlugin.getImage("icons/queryeditor/file_saveas.png"));
        exportItem.setToolTipText(Messages.msgTipSaveasAction);
        exportItem.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        doExport();
                    }
                });
    }

    /** Empty method.The subclass maybe extend this method. */
    protected void doImport() {
        // empty
    }

    /**
     * Perform the import based upon the given ConfigType.
     *
     * @param configType the ConfigType
     */
    protected void doImport(ConfigType configType) {
        IEditorInput input = this.getEditorInput();
        if (!(input instanceof ConfEditInput)) {
            return;
        }
        ImportConfigDialog dialog =
                new ImportConfigDialog(this.getSite().getShell(), configType, true);
        if (defaultImportFileName != null && !"".equals(defaultImportFileName)) {
            dialog.setDefaultFileName(defaultImportFileName);
        }
        if (defaultImportFileCharset != null && !"".equals(defaultImportFileCharset)) {
            dialog.setDefaultCharset(defaultImportFileCharset);
        }

        if (dialog.open() == Dialog.OK) {
            defaultImportFileName = dialog.getDefaultFileName();
            defaultImportFileCharset = dialog.getDefaultCharset();
            contents = dialog.getImportFileContent();
            createContent();
        }
    }

    /** Empty method.The subclass maybe extend this method. */
    protected void doExport() {
        // empty
    }

    /**
     * Perform the export based upon the given ConfigType.
     *
     * @param configType the ConfigType
     */
    protected void doExport(ConfigType configType) {
        IEditorInput input = this.getEditorInput();
        if (!(input instanceof ConfEditInput)) {
            return;
        }
        ExportConfigDialog dialog =
                new ExportConfigDialog(this.getSite().getShell(), configType, true);
        if (defaultExportFilePath != null && !"".equals(defaultExportFilePath)) {
            dialog.setDefaultFilePath(defaultExportFilePath);
        }
        if (defaultExportFileName != null && !"".equals(defaultExportFileName)) {
            dialog.setDefaultFileName(defaultExportFileName);
        }
        if (defaultExportFileExtName != null && !"".equals(defaultExportFileExtName)) {
            dialog.setDefaultFileExtName(defaultExportFileExtName);
        }
        if (defaultExportFileCharset != null && !"".equals(defaultExportFileCharset)) {
            dialog.setOutputFileCharset(defaultExportFileCharset);
        }
        if (dialog.open() == Dialog.OK) {
            defaultExportFilePath = dialog.getDefaultFilePath();
            defaultExportFileName = dialog.getDefaultFileName();
            defaultExportFileExtName = dialog.getDefaultFileExtName();
            String fileFullName = dialog.getOutputFileFullName();
            defaultExportFileCharset = dialog.getOutputFileCharset();
            ConfigParaHelp.exportConf(contents, fileFullName, defaultExportFileCharset);
        }
    }

    /**
     * @param event the CubridNodeChangedEvent
     * @see
     *     com.cubrid.common.ui.spi.event.ICubridNodeChangedListener#nodeChanged(com.cubrid.common.ui.spi.event.CubridNodeChangedEvent)
     */
    public void nodeChanged(CubridNodeChangedEvent event) {
        noOp();
    }

    /**
     * Document change listener
     *
     * @author pangqiren
     * @version 1.0 - 2011-2-17 created by pangqiren
     */
    private class DocumentAdpater implements IDocumentListener {
        /**
         * Listen to document about to be changed event
         *
         * @param event DocumentEvent
         */
        public void documentAboutToBeChanged(DocumentEvent event) {
            noOp();
        }

        /**
         * Listen to document changed event
         *
         * @param event DocumentEvent
         */
        public void documentChanged(DocumentEvent event) {
            isDirty = true;
            firePropertyChange(PROP_DIRTY);
        }
    }
}
