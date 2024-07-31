/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.tool.editor.dialog;

import com.cubrid.tool.editor.CUBRIDTextEditorPlugin;
import com.cubrid.tool.editor.Messages;
import com.cubrid.tool.editor.TextEditorFindReplaceMediator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Find Replace Dialog.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-2-12 created by Kevin Cao
 */
public class FindReplaceDialog extends Dialog {
    private static int MAX_HISTORY_COUNT = 8;
    // input
    private Combo findText;
    private Combo replaceText;
    // buttons
    private Button btnReplaceAll;
    private Button btnFind;
    private Button btnReplaceAndFind;
    private Button btnReplace;
    private Label labelResult;
    // options
    private Button btnForward;
    private Button btnScopeAll;
    private Button btnCaseSenitive;
    private Button btnWrapSearch;
    private Button btnWholeWord;
    private Button btnIncremental;
    private Button btnRegularExpressions;

    /**
     * Constructor of FindReplaceDialog. It is strong recommended that use
     * TextEditorFindReplaceDialogMediator.getFindReplaceDialog to get a dialog instance.
     *
     * @param parentShell Shell of parent.
     */
    public FindReplaceDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(
                SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.MODELESS | getDefaultOrientation());
    }

    /**
     * Write configurations before close.
     *
     * @return is closing success.
     */
    public boolean close() {
        writeConfiguration();
        createFindReplaceOption();
        return super.close();
    }

    /**
     * Initializes itself from the dialog settings with the same state as at the previous
     * invocation.
     */
    private void readConfiguration() {
        IDialogSettings s = getDialogSettings();

        btnWrapSearch.setSelection(s.getBoolean("wrap")); // $NON-NLS-1$
        btnCaseSenitive.setSelection(s.getBoolean("casesensitive")); // $NON-NLS-1$
        btnWholeWord.setSelection(s.getBoolean("wholeword")); // $NON-NLS-1$
        btnIncremental.setSelection(s.getBoolean("incremental")); // $NON-NLS-1$
        btnRegularExpressions.setSelection(s.getBoolean("isRegEx")); // $NON-NLS-1$

        String[] findHistory = s.getArray("findhistory"); // $NON-NLS-1$
        if (findHistory != null) {
            for (int i = 0; i < findHistory.length; i++) {
                if (findHistory[i].trim().length() > 0) {
                    findText.add(findHistory[i]);
                }
            }
            if (findHistory.length > 0) {
                findText.setText(findText.getItem(0));
            }
        }

        String[] replaceHistory = s.getArray("replacehistory"); // $NON-NLS-1$
        if (replaceHistory != null) {
            for (int i = 0; i < replaceHistory.length; i++) {
                if (replaceHistory[i].trim().length() > 0) {
                    replaceText.add(replaceHistory[i]);
                }
            }
        }
    }

    /** Stores its current configuration in the dialog store. */
    private void writeConfiguration() {
        IDialogSettings s = getDialogSettings();

        s.put("wrap", isWrapSearch()); // $NON-NLS-1$
        s.put("casesensitive", isCaseSensitive()); // $NON-NLS-1$
        s.put("wholeword", isWholeWord()); // $NON-NLS-1$
        s.put("incremental", isIncremental()); // $NON-NLS-1$
        s.put("isRegEx", isRegularExpressions()); // $NON-NLS-1$

        List<String> history = getFindHistory();
        writeHistory(history, s, "findhistory"); // $NON-NLS-1$

        history = getReplaceHistory();
        writeHistory(history, s, "replacehistory"); // $NON-NLS-1$
    }

    /**
     * Get replace history.
     *
     * @return List of replace history.
     */
    private List<String> getReplaceHistory() {
        return Arrays.asList(replaceText.getItems());
    }

    /**
     * Get find history.
     *
     * @return List of find history.
     */
    private List<String> getFindHistory() {
        return Arrays.asList(findText.getItems());
    }

    /**
     * Retrieves the dialog settings.
     *
     * @return Dialog Settings
     */
    private IDialogSettings getDialogSettings() {
        IDialogSettings dialogSettings;
        IDialogSettings settings = CUBRIDTextEditorPlugin.getDefault().getDialogSettings();
        dialogSettings = settings.getSection(getClass().getName());
        if (dialogSettings == null) {
            dialogSettings = settings.addNewSection(getClass().getName());
        }
        // set default values.
        if (dialogSettings.get("wrap") == null) {
            dialogSettings.put("wrap", isWrapSearch()); // $NON-NLS-1$
            dialogSettings.put("casesensitive", isCaseSensitive()); // $NON-NLS-1$
            dialogSettings.put("wholeword", isWholeWord()); // $NON-NLS-1$
            dialogSettings.put("incremental", isIncremental()); // $NON-NLS-1$
            dialogSettings.put("isRegEx", isRegularExpressions()); // $NON-NLS-1$
        }
        return dialogSettings;
    }

    /**
     * Writes the given history into the given dialog store.
     *
     * @param history the history
     * @param settings the dialog settings
     * @param sectionName the section name
     */
    private void writeHistory(List<?> history, IDialogSettings settings, String sectionName) {
        int itemCount = history.size();
        List<String> newHistoryList = new ArrayList<String>();
        for (int i = 0; i < itemCount && i < MAX_HISTORY_COUNT; i++) {
            String item = (String) history.get(i);
            if (newHistoryList.contains(item)) {
                continue;
            }
            newHistoryList.add(item);
        }

        String[] names = new String[newHistoryList.size()];
        newHistoryList.toArray(names);
        settings.put(sectionName, names);
    }

    /**
     * Auto add history and remove the old.
     *
     * @param text combo to be updated
     */
    private void updateTextCombo(Combo text) {
        String searchStr = text.getText();
        int strHis = text.indexOf(searchStr);
        if (strHis < 0) {
            text.add(searchStr, 0);
            if (text.getItemCount() > MAX_HISTORY_COUNT) {
                text.remove(MAX_HISTORY_COUNT);
            }
        } else if (strHis > 0) {
            text.remove(strHis);
            text.add(searchStr, 0);
        }
        text.setText(searchStr);
    }

    /**
     * Create the option of FindReplace
     *
     * @return FindReplaceOption
     */
    protected FindReplaceOption createFindReplaceOption() {
        FindReplaceOption findReplaceOption = new FindReplaceOption();
        findReplaceOption.setForward(isForward());
        findReplaceOption.setCaseSensitive(this.isCaseSensitive());
        findReplaceOption.setWholeWord(isWholeWord());
        findReplaceOption.setRegularExpressions(isRegularExpressions());
        findReplaceOption.setSearchedStr(findText.getText());
        findReplaceOption.setWrapSearch(isWrapSearch());
        findReplaceOption.setReplacedStr(replaceText.getText());
        return findReplaceOption;
    }

    /**
     * Select the text in text viewer.It is supposed that getCurrentTextComposite()'s return is not
     * null.
     *
     * @param start start position.
     * @return the location of string.
     */
    protected int findAndSelect(int start, FindReplaceOption option) {
        int result = TextEditorFindReplaceMediator.findAndSelect(start, option.isForward(), option);
        updateTextCombo(findText);
        return result;
    }

    /**
     * Search text in editor from current position and use the isWrapSearch() option.
     *
     * @return position.
     */
    protected int defaultLocateText() {
        TextViewer st = TextEditorFindReplaceMediator.getCurrentTextEditor();
        if (st == null) {
            return -1;
        }
        return findAndSelect(st.getTextWidget().getCaretOffset(), createFindReplaceOption());
    }

    /** Replace current selected text with input. */
    protected void replaceSelected() {
        TextViewer st = TextEditorFindReplaceMediator.getCurrentTextEditor();
        if (st == null) {
            return;
        }

        IFindReplaceTarget target = TextEditorFindReplaceMediator.getFindAndReplaceInterface();
        if (target == null) {
            return;
        }

        if (isTextSelected()) {
            try {
                target.replaceSelection(replaceText.getText());
            } catch (Exception ignored) {
            }

            updateTextCombo(replaceText);
        }
    }

    /**
     * Retrieve is the text selected.
     *
     * @return true or false.
     */
    protected boolean isTextSelected() {
        TextViewer st = TextEditorFindReplaceMediator.getCurrentTextEditor();
        if (st == null) {
            return false;
        }
        return (st.getTextWidget().getSelectionCount() > 0);
    }

    /**
     * Set find result message
     *
     * @param message to be find.
     */
    protected void setFindResultMessage(String message) {
        labelResult.setText(message);
    }

    /** Search the text in document. */
    protected void findClicked() {
        if (defaultLocateText() < 0) {
            setFindResultMessage(Messages.messageStringNotFound);
        } else {
            setFindResultMessage("");
        }
        updateButtons();
    }

    /** Replace current and find next. */
    protected void replaceAndFindClicked() {
        replaceSelected();
        if (defaultLocateText() < 0) {
            setFindResultMessage(Messages.messageStringNotFound);
        } else {
            setFindResultMessage("");
        }
        updateButtons();
    }

    /** Replace current only. */
    protected void replaceClicked() {
        replaceSelected();
        updateButtons();
    }

    /** Replace all matched. */
    protected void replaceAllClicked() {
        FindReplaceOption option = createFindReplaceOption();
        int position = findAndSelect(0, option);
        int total = 0;
        option.setWrapSearch(false);
        while (position >= 0) {
            total++;
            replaceSelected();
            position = findAndSelect(-1, option);
        }
        if (total > 0) {
            setFindResultMessage(total + " " + Messages.messageTotalReplaced);
        } else {
            setFindResultMessage(Messages.messageStringNotFound);
        }
        updateButtons();
    }

    /** Update the buttons status. */
    public void updateButtons() {

        if (findText == null || findText.isDisposed()) {
            return;
        }
        TextViewer textViewer = TextEditorFindReplaceMediator.getCurrentTextEditor();
        boolean isCanFind = textViewer != null && findText.getText().length() > 0;
        boolean isCanReplace =
                isCanFind && textViewer.isEditable() && textViewer.getTextWidget().isEnabled();
        btnFind.setEnabled(isCanFind);
        btnReplaceAndFind.setEnabled(isCanReplace && isTextSelected());
        btnReplace.setEnabled(isCanReplace && isTextSelected());
        btnReplaceAll.setEnabled(isCanReplace);
        btnWholeWord.setEnabled(!btnRegularExpressions.getSelection());
        btnIncremental.setEnabled(!btnRegularExpressions.getSelection());
    }

    /**
     * Search option: Wrap Search
     *
     * @return wrap search option
     */
    protected boolean isWrapSearch() {
        return btnWrapSearch.getSelection();
    }

    /**
     * Search option: Case sensitive.
     *
     * @return case sensitive option
     */
    protected boolean isCaseSensitive() {
        return btnCaseSenitive.getSelection();
    }

    /**
     * Search option: Whole word
     *
     * @return whole word option
     */
    protected boolean isWholeWord() {
        return btnWholeWord.isEnabled() && btnWholeWord.getSelection();
    }

    /**
     * Search option: Regular expressions
     *
     * @return regular expressions option
     */
    protected boolean isRegularExpressions() {
        return btnRegularExpressions.getSelection();
    }

    /**
     * Search option: Incremental.
     *
     * @return incremental option
     */
    protected boolean isIncremental() {
        return btnIncremental.isEnabled() && btnIncremental.getSelection();
    }

    /**
     * Search option: search forward or search backward .
     *
     * @return forward or backward option
     */
    protected boolean isForward() {
        return btnForward.getSelection();
    }

    /**
     * Search option: search all or search in selected text.
     *
     * @return search all or selected text.
     */
    protected boolean isSearchAll() {
        return btnScopeAll.getSelection();
    }

    /**
     * Create contents.
     *
     * @param parent of Composite.
     * @return the contents of dialog.
     */
    protected Control createContents(Composite parent) {
        Control result = super.createContents(parent);
        readConfiguration();
        this.getShell()
                .addShellListener(
                        new ShellAdapter() {
                            public void shellDeactivated(ShellEvent event) {
                                FindReplaceDialog.this.updateButtons();
                            }

                            public void shellActivated(ShellEvent event) {
                                FindReplaceDialog.this.updateButtons();
                            }
                        });
        getShell().setText(Messages.findReplaceDialogTitle);
        setSelectedText();
        return result;
    }

    /**
     * Create dialog area.
     *
     * @param parent composite.
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        createSearchArea(dialogArea);
        createSearchOption1(dialogArea);
        createSearchOption2(dialogArea);
        return dialogArea;
    }

    /**
     * Adds buttons to this dialog's button bar.
     *
     * @param parent the button bar composite
     */
    protected void createButtonsForButtonBar(Composite parent) {

        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        parent.setLayout(layout);

        Composite topComp = new Composite(parent, SWT.None);
        topComp.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
        topComp.setLayout(new GridLayout(2, false));

        Composite bottomComp = new Composite(parent, SWT.None);
        bottomComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        bottomComp.setLayout(new GridLayout(2, false));

        btnFind = createButton(topComp, Messages.btnFind);
        btnFind.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FindReplaceDialog.this.findClicked();
                    }
                });

        btnReplaceAndFind = createButton(topComp, Messages.btnRepalceAndFind);
        btnReplaceAndFind.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FindReplaceDialog.this.replaceAndFindClicked();
                    }
                });

        btnReplace = createButton(topComp, Messages.btnReplace);
        btnReplace.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FindReplaceDialog.this.replaceClicked();
                    }
                });

        btnReplaceAll = createButton(topComp, Messages.btnReplaceAll);
        btnReplaceAll.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FindReplaceDialog.this.replaceAllClicked();
                    }
                });
        labelResult = new Label(bottomComp, SWT.LEFT);
        GridData labelData = new GridData(SWT.FILL, SWT.FILL, true, true);
        labelResult.setLayoutData(labelData);
        labelResult.setText("");

        Button btnClose = createButton(bottomComp, Messages.btnClose);
        btnClose.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        FindReplaceDialog.this.close();
                    }
                });

        getShell().setDefaultButton(btnFind);
        updateButtons();
    }

    private Button createButton(Composite container, String message) {
        Composite composite = new Composite(container, SWT.None);
        composite.setLayout(new FillLayout());

        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        /*For the MAC platform*/
        data.widthHint = Math.max(widthHint, minSize.x) + 3;
        composite.setLayoutData(data);

        Button button = new Button(composite, SWT.None);
        button.setText(message);

        return button;
    }

    /**
     * Create Search options area.
     *
     * @param dialogArea parent Composite
     */
    private void createSearchOption2(Composite dialogArea) {
        final Group groupOp = new Group(dialogArea, SWT.NONE);
        groupOp.setText(Messages.grpOptions);
        groupOp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
        groupOp.setLayout(new GridLayout(2, false));

        btnCaseSenitive = new Button(groupOp, SWT.CHECK);
        btnCaseSenitive.setText(Messages.btnCaseSenitive);

        btnWrapSearch = new Button(groupOp, SWT.CHECK);
        btnWrapSearch.setText(Messages.btnWrapSearch);

        btnWholeWord = new Button(groupOp, SWT.CHECK);
        btnWholeWord.setText(Messages.btnWholeWord);

        btnIncremental = new Button(groupOp, SWT.CHECK);
        btnIncremental.setText(Messages.btnIncremental);
        btnIncremental.setVisible(false);

        btnRegularExpressions = new Button(groupOp, SWT.CHECK);
        btnRegularExpressions.setText(Messages.btnRegularExpressions);
        btnRegularExpressions.addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent event) {
                        updateButtons();
                    }
                });
    }

    /**
     * Create search direction and scope area.
     *
     * @param dialogArea parent Composite
     */
    private void createSearchOption1(Composite dialogArea) {
        final Composite soPanel = new Composite(dialogArea, SWT.NONE);
        soPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
        final GridLayout gdPanel = new GridLayout(2, true);
        gdPanel.horizontalSpacing = 6;
        gdPanel.marginWidth = 2;
        soPanel.setLayout(gdPanel);

        final Group groupDir = new Group(soPanel, SWT.NONE);
        groupDir.setText(Messages.grpDirection);
        groupDir.setLayout(new GridLayout());
        groupDir.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        btnForward = new Button(groupDir, SWT.RADIO);
        btnForward.setText(Messages.btnForward);
        btnForward.setSelection(true);

        Button btnBackward = new Button(groupDir, SWT.RADIO);
        btnBackward.setText(Messages.btnBackward);
        //
        final Group groupScope = new Group(soPanel, SWT.NONE);
        groupScope.setText(Messages.grpScope);
        groupScope.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        groupScope.setLayout(new GridLayout());

        btnScopeAll = new Button(groupScope, SWT.RADIO);
        btnScopeAll.setText(Messages.btnScopeAll);
        btnScopeAll.setSelection(true);

        Button btnScopeSelectedLines = new Button(groupScope, SWT.RADIO);
        btnScopeSelectedLines.setText(Messages.btnScopeSelectedLines);
    }

    /**
     * Create text input area.
     *
     * @param dialogArea parent Composite.
     */
    private void createSearchArea(Composite dialogArea) {
        final Composite textPanel = new Composite(dialogArea, SWT.NONE);
        textPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        final GridLayout gdGroup = new GridLayout(2, false);
        textPanel.setLayout(gdGroup);

        final Label findWhatLabel = new Label(textPanel, SWT.NONE);
        findWhatLabel.setText(Messages.labelFind);

        findText = new Combo(textPanel, SWT.DROP_DOWN | SWT.V_SCROLL | SWT.H_SCROLL);
        final GridData gdFind = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdFind.widthHint = 200;
        findText.setLayoutData(gdFind);
        findText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        updateButtons();
                    }
                });
        final Label replaceWithLabel = new Label(textPanel, SWT.NONE);
        replaceWithLabel.setText(Messages.labelRepalceWith);

        replaceText = new Combo(textPanel, SWT.DROP_DOWN);
        final GridData gdReplace = new GridData(SWT.FILL, SWT.CENTER, true, false);
        replaceText.setLayoutData(gdReplace);
    }

    /** Set the selected text from focus text editor */
    public void setSelectedText() {
        TextViewer textViewer = TextEditorFindReplaceMediator.getCurrentTextEditor();
        if (textViewer == null
                || textViewer.getTextWidget() == null
                || textViewer.getTextWidget().isDisposed()) {
            return;
        }
        if (findText == null || findText.isDisposed()) {
            return;
        }
        String selectedText = textViewer.getTextWidget().getSelectionText();
        if (selectedText == null || selectedText.trim().length() == 0) {
            return;
        }
        if (findText.getItems() != null
                && Arrays.asList(findText.getItems()).contains(selectedText)) {
            findText.setText(selectedText);
        } else {
            findText.add(selectedText);
            findText.setText(selectedText);
        }
        writeConfiguration();
    }
}
