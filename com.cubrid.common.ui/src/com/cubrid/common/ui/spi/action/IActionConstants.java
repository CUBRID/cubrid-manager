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
package com.cubrid.common.ui.spi.action;

import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * Action ids for actions,group in the workbench menu bar, tool bar,popup menu in the navigator
 *
 * <p>This interface contains constants only;it is not intended to be implemented or extended.
 *
 * <h3>Standard menus</h3>
 *
 * <ul>
 *   <li>File menu
 *   <li>Edit menu
 *   <li>Tools menu
 *   <li>Action menu
 *   <li>Help menu
 * </ul>
 *
 * @author Administrator
 * @version 1.0 - 2010-10-4 created by Administrator
 */
public final class IActionConstants {

    // menu bar
    public static final String MENU_FILE = IWorkbenchActionConstants.M_FILE;
    public static final String MENU_EDIT = IWorkbenchActionConstants.M_EDIT;
    public static final String MENU_ACTION = "action";
    public static final String MENU_TOOLS = "tools";
    public static final String MENU_HELP = IWorkbenchActionConstants.M_HELP;
    public static final String MENU_CUBRID = "CUBRID";

    // tool bar
    public static final String TOOL_NEW1 = "new1";
    public static final String TOOL_NEW2 = "new2";
    public static final String TOOL_SERVICE = "service";
    public static final String TOOL_DATABASE1 = "database1";
    public static final String TOOL_DATABASE2 = "database2";
    public static final String TOOL_VERSION = "version";
    public static final String TOOL_ISSUE = "issue";
    public static final String TOOL_SEARCH = "search";

    // group
    public static final String GROUP_HOST = "host";
}
