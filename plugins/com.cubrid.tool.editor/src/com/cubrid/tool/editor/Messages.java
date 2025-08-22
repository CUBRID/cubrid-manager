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

package com.cubrid.tool.editor;

import org.eclipse.osgi.util.NLS;

/**
 * This is message bundle classes and provide convenience methods for manipulating messages.
 *
 * @author Kevin Cao
 * @version 1.0 - 2011-2-16 created by Kevin Cao
 */
public class Messages extends NLS {
    static {
        NLS.initializeMessages(CUBRIDTextEditorPlugin.PLUGIN_ID + ".Messages", Messages.class);
    }

    public static String btnFind;
    public static String btnRepalceAndFind;
    public static String btnReplace;
    public static String btnReplaceAll;
    public static String btnClose;

    public static String grpOptions;
    public static String btnCaseSenitive;
    public static String btnWrapSearch;
    public static String btnWholeWord;
    public static String btnIncremental;
    public static String btnRegularExpressions;

    public static String grpDirection;
    public static String btnForward;
    public static String btnBackward;

    public static String grpScope;
    public static String btnScopeAll;
    public static String btnScopeSelectedLines;

    public static String labelFind;
    public static String labelRepalceWith;

    public static String messageStringNotFound;
    public static String messageTotalReplaced;
    // message box
    public static String btnYes;
    public static String btnNo;
    public static String titleError;
    public static String invalidateXML;
    // menu text
    public static String menuCut;
    public static String menuCopy;
    public static String menuPast;
    public static String menuFindReplace;
    public static String menuFormat;
    public static String findReplaceDialogTitle;
}
