/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search
 * Solution.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  - Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
package com.cubrid.tool.editor;

import com.cubrid.tool.editor.action.FindReplaceAction;
import com.cubrid.tool.editor.action.TextEditorAction;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;

/**
 * Text Editor which is used to edit text documents.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-1-21 created by Kevin Cao
 */
public class CUBRIDTextEditor extends EditorPart {

    public static final String ID = CUBRIDTextEditor.class.getName();

    protected ColorManager colorManager;

    protected SourceViewerConfiguration svConfiguration;

    protected DocumentProvider documentProvider;

    protected SourceViewer textViewer;

    protected TextViewerUndoManager undoManager;

    /**
     * Retrieves the undo manager of text editor.
     *
     * @return the undoManager
     */
    public TextViewerUndoManager getUndoManager() {
        return undoManager;
    }

    protected Map<String, IAction> actions = new HashMap<String, IAction>();

    protected TextInputListener textInputListener;

    protected TextEditorFindReplaceMediator editorDialogMediator;

    /**
     * Text Input Listener.
     *
     * @author Kevin Cao
     * @version 1.0 - 2011-2-24 created by Kevin Cao
     */
    protected static class TextInputListener implements ITextListener {
        /** Indicates whether the editor input changed during the process of state validation. */
        boolean inputChanged;

        /**
         * Text changed.
         *
         * @param event TextEvent.
         */
        public void textChanged(TextEvent event) {
            inputChanged = true;
        }
    }

    public CUBRIDTextEditor() {
        super();
        colorManager = new ColorManager();
        undoManager = new TextViewerUndoManager(50);
    }

    /**
     * Set document provider.
     *
     * @param documentProvider DocumentProvider
     */
    protected void setDocumentProvider(DocumentProvider documentProvider) {
        this.documentProvider = documentProvider;
    }

    /**
     * Set text viewer configuration.
     *
     * @param configuration SourceViewerConfiguration
     */
    protected void setSourceViewerConfiguration(SourceViewerConfiguration configuration) {
        svConfiguration = configuration;
    }

    /** Dispose composites. */
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    /**
     * Save.
     *
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        textInputListener.inputChanged = false;
    }

    /** Save as. */
    public void doSaveAs() {
        textInputListener.inputChanged = false;
    }

    /**
     * Initialization of editor.
     *
     * @param site IEditorSite
     * @param input IEditorInput
     * @throws PartInitException when error raised.
     */
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.setInput(input);
        this.setSite(site);
        setPartName(input.getName());
        setTitleToolTip(input.getToolTipText());
    }

    /** Create actions of editor. */
    protected void createActions() {
        // Do nothing.
        UndoActionHandler undo =
                new UndoActionHandler(this.getSite(), undoManager.getUndoContext());
        undo.setAccelerator(SWT.CTRL | 'z');
        actions.put(ActionFactory.UNDO.getId(), undo);
        RedoActionHandler redo =
                new RedoActionHandler(this.getSite(), undoManager.getUndoContext());
        redo.setAccelerator(SWT.CTRL | 'y');
        actions.put(ActionFactory.REDO.getId(), redo);

        actions.put(
                ActionFactory.CUT.getId(),
                new TextEditorAction(Messages.menuCut, this, ITextOperationTarget.CUT));
        actions.put(
                ActionFactory.COPY.getId(),
                new TextEditorAction(Messages.menuCopy, this, ITextOperationTarget.COPY));
        actions.put(
                ActionFactory.PASTE.getId(),
                new TextEditorAction(Messages.menuPast, this, ITextOperationTarget.PASTE));

        actions.put(ActionFactory.FIND.getId(), new FindReplaceAction(Messages.menuFindReplace));
    }

    /** Hook the bar actions */
    protected void hookRetargetActions() {
        IActionBars bar = this.getEditorSite().getActionBars();
        bar.setGlobalActionHandler(
                ActionFactory.UNDO.getId(), actions.get(ActionFactory.UNDO.getId()));
        bar.setGlobalActionHandler(
                ActionFactory.REDO.getId(), actions.get(ActionFactory.REDO.getId()));
        bar.setGlobalActionHandler(
                ActionFactory.CUT.getId(), actions.get(ActionFactory.CUT.getId()));
        bar.setGlobalActionHandler(
                ActionFactory.COPY.getId(), actions.get(ActionFactory.COPY.getId()));
        bar.setGlobalActionHandler(
                ActionFactory.PASTE.getId(), actions.get(ActionFactory.PASTE.getId()));
        bar.setGlobalActionHandler(
                ActionFactory.FIND.getId(), actions.get(ActionFactory.FIND.getId()));
        bar.updateActionBars();
    }

    /** Unhook retartet actions */
    protected void unHookRetargetActions() {
        IActionBars bar = this.getEditorSite().getActionBars();
        bar.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
        bar.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
        bar.setGlobalActionHandler(ActionFactory.CUT.getId(), null);
        bar.setGlobalActionHandler(ActionFactory.COPY.getId(), null);
        bar.setGlobalActionHandler(ActionFactory.PASTE.getId(), null);
        bar.setGlobalActionHandler(ActionFactory.FIND.getId(), null);
        bar.updateActionBars();
    }

    /**
     * Create the context menu
     *
     * @param manager IMenuManager
     */
    protected void createContextMenu(IMenuManager manager) {

        updateActions();
        IAction undoAction = actions.get(ActionFactory.UNDO.getId());
        if (undoAction != null) {
            manager.add(undoAction);
        }
        IAction redoAction = actions.get(ActionFactory.REDO.getId());
        if (redoAction != null) {
            manager.add(redoAction);
        }
        manager.add(new Separator());
        IAction cutAction = actions.get(ActionFactory.CUT.getId());
        if (cutAction != null) {
            manager.add(cutAction);
        }
        IAction copyAction = actions.get(ActionFactory.COPY.getId());
        if (copyAction != null) {
            manager.add(copyAction);
        }
        IAction pasteAction = actions.get(ActionFactory.PASTE.getId());
        if (pasteAction != null) {
            manager.add(pasteAction);
        }
        manager.add(new Separator());
        IAction findAction = actions.get(ActionFactory.FIND.getId());
        if (findAction != null) {
            manager.add(findAction);
        }
    }

    /**
     * The document is modified.
     *
     * @return true:modified;false: not modified.
     */
    public boolean isDirty() {
        return textInputListener.inputChanged;
    }

    /** @return can be save as. */
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * Create Part Controls.
     *
     * @param parent composite.
     */
    public void createPartControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        initTextViewer(composite);
        undoManager.connect(textViewer);
        createActions();
        updateActions();

        // create context menu
        MenuManager contextMenuManager =
                new MenuManager("#PopupMenu", "CUBRIDTExtEditorContextMenu");
        contextMenuManager.setRemoveAllWhenShown(true);
        contextMenuManager.addMenuListener(
                new IMenuListener() {
                    public void menuAboutToShow(IMenuManager manager) {
                        createContextMenu(manager);
                    }
                });
        Menu contextMenu = contextMenuManager.createContextMenu(textViewer.getTextWidget());
        textViewer.getTextWidget().setMenu(contextMenu);
        IWorkbenchPartSite site = getSite();
        site.registerContextMenu(contextMenuManager, textViewer);
        site.setSelectionProvider(textViewer);
        textViewer
                .getTextWidget()
                .addFocusListener(
                        new FocusListener() {

                            public void focusGained(FocusEvent event) {
                                hookRetargetActions();
                            }

                            public void focusLost(FocusEvent event) {
                                unHookRetargetActions();
                            }
                        });
    }

    /**
     * Initialize the Text Viewer.
     *
     * @param composite which is text viewer's parent.
     */
    protected void initTextViewer(final Composite composite) {
        textViewer =
                new SourceViewer(composite, getRuler(), SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        textViewer
                .getTextWidget()
                .setFont(new Font(Display.getCurrent(), "Default", 12, SWT.NORMAL));
        textViewer.configure(svConfiguration);
        textViewer.setDocument(documentProvider.getDocument(getEditorInput()));
        textViewer.setUndoManager(undoManager);
        textInputListener = new TextInputListener();
        textViewer.addTextListener(textInputListener);
        textViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {

                    public void selectionChanged(SelectionChangedEvent event) {
                        updateActions();
                    }
                });
        textViewer
                .getTextWidget()
                .setData(TextEditorFindReplaceMediator.SQL_EDITOR_FLAG, textViewer);

        editorDialogMediator = new TextEditorFindReplaceMediator();
        textViewer.getTextWidget().addFocusListener(editorDialogMediator);
    }

    /** @return CompositeRuler of SourceViewer. */
    protected CompositeRuler getRuler() {
        CompositeRuler ruler = new CompositeRuler();
        LineNumberRulerColumn lineCol = new LineNumberRulerColumn();
        lineCol.setBackground(colorManager.getColor(new RGB(236, 233, 216)));
        ruler.addDecorator(0, lineCol);
        return ruler;
    }

    /** Set text viewer focus. */
    public void setFocus() {
        if (textViewer != null && textViewer.getTextWidget() != null) {
            textViewer.getTextWidget().setFocus();
            hookRetargetActions();
        }
    }

    /**
     * Get the document which is being edit.
     *
     * @return document being edit.
     */
    public IDocument getDocument() {
        return textViewer.getDocument();
    }

    /**
     * It can retrieve document and document content.
     *
     * @param adapter if it is String, the document's content will be retrieved.
     * @return If adapter is string,return the document content.If is document,return the document.
     */
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (IDocument.class.equals(adapter)) {
            return getDocument();
        } else if (String.class.equals(adapter)) {
            if (getDocument() == null) {
                return "";
            } else {
                return getDocument().get();
            }
        } else if (IAction.class.equals(adapter)) {
            Map<String, IAction> actAda = new HashMap<String, IAction>();
            actAda.putAll(actions);
            return actAda;
        }
        return super.getAdapter(adapter);
    }

    /**
     * Set the document content
     *
     * @param content String content.
     */
    public void setDocumentString(String content) {
        if (getDocument() != null) {
            getDocument().set(content);
        }
    }

    /** Update actions' status. */
    protected void updateActions() {
        IAction iAction = actions.get(ActionFactory.UNDO.getId());
        if (iAction != null) {
            iAction.setEnabled(undoManager.undoable());
        }

        iAction = actions.get(ActionFactory.REDO.getId());
        if (iAction != null) {
            iAction.setEnabled(undoManager.redoable());
        }

        iAction = actions.get(ActionFactory.CUT.getId());
        if (iAction != null) {
            iAction.setEnabled(textViewer.canDoOperation(ITextOperationTarget.CUT));
        }

        iAction = actions.get(ActionFactory.COPY.getId());
        if (iAction != null) {
            iAction.setEnabled(textViewer.canDoOperation(ITextOperationTarget.COPY));
        }

        iAction = actions.get(ActionFactory.PASTE.getId());
        if (iAction != null) {
            iAction.setEnabled(textViewer.canDoOperation(ITextOperationTarget.PASTE));
        }
    }

    /**
     * Text's operation.
     *
     * @param operation ITextOperationTarget.CUT/COPY/PASTE and etc.
     */
    public void doOperation(int operation) {
        textViewer.doOperation(operation);
    }

    public SourceViewer getTextViewer() {
        return textViewer;
    }
}
