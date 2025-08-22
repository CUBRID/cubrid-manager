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
package com.cubrid.common.ui.perspective;

/**
 * IConstance Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2014-4-18 created by Kevin.Wang
 */
public interface IPerspectiveConstance {
    public static final String PERSPECTIVE_ACTION_CONTRIBUTION_ID = "cubrid.tools.toolbar.view";
    public static final String HELP_ACTION_CONTRIBUTION_ID = "cubrid.tools.toolbar.help";
    public static final String SEARCH_ACTION_CONTRIBUTION_ID = "cubrid.tools.toolbar.search";
    public static final String MIGRATION_ACTION_CONTRIBUTION_ID = "cubrid.tools.toolbar.migration";
    public static final String MIGRATION_MENU_ID = "com.cubrid.cubridmigration.ui.menu.migration";
    /*The perspective id constance*/
    public static final String CM_PERSPECTIVE_ID =
            "org.cubrid.cubridmanager.plugin.manager.Perspective";
    public static final String CQB_PERSPECTIVE_ID =
            "org.cubrid.cubridquery.plugin.querybrowser.Perspective";
}
