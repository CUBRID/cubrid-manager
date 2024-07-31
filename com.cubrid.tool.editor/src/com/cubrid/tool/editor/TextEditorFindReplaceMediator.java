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
package com.cubrid.tool.editor;

import com.cubrid.tool.editor.dialog.FindReplaceDialog;
import com.cubrid.tool.editor.dialog.FindReplaceOption;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Manage the communication between TextEditor and FindReplaceDialog.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-2-16 created by Kevin Cao
 */
public class TextEditorFindReplaceMediator extends FocusAdapter {

    public static final String SQL_EDITOR_FLAG = "SQL_EDITOR_FLAG";

    private static TextViewer currentTextEditor = null;
    private static FindReplaceDialog dialog;

    /** Open the dialog of FindOrReplace */
    public static void openFindReplaceDialog() {
        if ((null == dialog || dialog.getShell() == null || dialog.getShell().isDisposed())) {
            Shell parrentShell = null;
            if (currentTextEditor == null) {
                parrentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            } else {
                parrentShell = currentTextEditor.getControl().getShell();
            }
            dialog = new FindReplaceDialog(parrentShell);
            dialog.open();
            dialog = null;
        } else {
            dialog.setSelectedText();
            dialog.getShell().forceFocus();
        }
    }

    /**
     * Get the current text editor
     *
     * @return TextViewer
     */
    public static TextViewer getCurrentTextEditor() {
        if (currentTextEditor == null
                || currentTextEditor.getControl() == null
                || currentTextEditor.getControl().isDisposed()) {
            return null;
        }
        return currentTextEditor;
    }

    /**
     * Set current text editor.
     *
     * @param currentTextEditor of current TextViewer
     */
    private static void setCurrentTextEditor(TextViewer currentTextEditor) {
        TextEditorFindReplaceMediator.currentTextEditor = currentTextEditor;
    }

    /**
     * When text editor focus gained, update the find dialog's buttons.
     *
     * @param event FocusEnent
     */
    public void focusGained(FocusEvent event) {
        if (event.widget instanceof StyledText) {
            StyledText text = (StyledText) event.widget;
            setCurrentTextEditor((TextViewer) text.getData(SQL_EDITOR_FLAG));
            if (null == dialog) {
                return;
            }
            dialog.updateButtons();
        }
    }

    /**
     * Find next searched string
     *
     * @return boolean
     */
    public static boolean findNext() {
        TextViewer st = getCurrentTextEditor();
        if (st == null) {
            return false;
        }

        String searchedStr = st.getTextWidget().getSelectionText();
        if (searchedStr == null || searchedStr.trim().length() == 0) {
            return false;
        }

        FindReplaceOption findReplaceOption = new FindReplaceOption();
        findReplaceOption.setSearchedStr(searchedStr);
        return findAndSelect(st.getTextWidget().getCaretOffset(), true, findReplaceOption) >= 0;
    }

    /**
     * Find previous searched string
     *
     * @return boolean
     */
    public static boolean findPrevious() {
        TextViewer st = getCurrentTextEditor();
        if (st == null) {
            return false;
        }

        String searchedStr = st.getTextWidget().getSelectionText();
        if (searchedStr == null || searchedStr.trim().length() == 0) {
            return false;
        }

        FindReplaceOption findReplaceOption = new FindReplaceOption();
        findReplaceOption.setSearchedStr(searchedStr);
        return findAndSelect(st.getTextWidget().getCaretOffset(), false, findReplaceOption) >= 0;
    }

    /**
     * Get the StyledText composite which to be searched in.
     *
     * @return IFindReplaceTarget of TextViewer.
     */
    public static IFindReplaceTarget getFindAndReplaceInterface() {
        TextViewer editor = getCurrentTextEditor();
        if (editor == null) {
            return null;
        }

        return editor.getFindReplaceTarget();
    }

    /**
     * Get the StyledText composite which to be searched in.
     *
     * @return TextView's IFindReplaceTargetExtension.
     */
    public static IFindReplaceTargetExtension getFindAndReplaceInterface1() {
        TextViewer editor = getCurrentTextEditor();
        if (editor == null) {
            return null;
        }

        return (IFindReplaceTargetExtension) editor.getFindReplaceTarget();
    }

    /**
     * Get the StyledText composite which to be searched in.
     *
     * @return TextView's IFindReplaceTargetExtension3.
     */
    public static IFindReplaceTargetExtension3 getFindAndReplaceInterface2() {
        TextViewer editor = getCurrentTextEditor();
        if (editor == null) {
            return null;
        }

        return (IFindReplaceTargetExtension3) editor.getFindReplaceTarget();
    }

    /**
     * Find and select the searched string
     *
     * @param start start position.
     * @param isWrap is warp search.
     * @param isForward is forward
     * @return the location of string.
     */
    public static int findAndSelect(int start, boolean isForward, FindReplaceOption option) {
        TextViewer st = getCurrentTextEditor();
        if (st == null) {
            return -1;
        }

        IFindReplaceTargetExtension findReplace = getFindAndReplaceInterface1();
        if (findReplace == null) {
            return -1;
        }

        if (option.isSearchAll()) {
            findReplace.setScope(null);
        } else {
            findReplace.setScope(new Region(st.getSelectedRange().x, st.getSelectedRange().y));
        }

        int widgetOffset = start >= 0 ? start : st.getTextWidget().getCaretOffset();
        widgetOffset =
                isForward ? widgetOffset : (widgetOffset - option.getSearchedStr().length() - 1);

        IFindReplaceTargetExtension3 target = getFindAndReplaceInterface2();
        if (target == null) {
            return -1;
        }

        int result =
                target.findAndSelect(
                        widgetOffset,
                        option.getSearchedStr(),
                        isForward,
                        option.isCaseSensitive(),
                        option.isWholeWord(),
                        option.isRegularExpressions());

        if (result < 0 && option.isWrapSearch()) {
            result =
                    target.findAndSelect(
                            isForward ? 0 : st.getTextWidget().getText().length() - 1,
                            option.getSearchedStr(),
                            isForward,
                            option.isCaseSensitive(),
                            option.isWholeWord(),
                            option.isRegularExpressions());
        }

        return result;
    }
}
