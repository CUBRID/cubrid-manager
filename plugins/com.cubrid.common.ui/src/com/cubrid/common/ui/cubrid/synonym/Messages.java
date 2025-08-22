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
package com.cubrid.common.ui.cubrid.synonym;

import com.cubrid.common.ui.CommonUIPlugin;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    static {
        NLS.initializeMessages(
                CommonUIPlugin.PLUGIN_ID + ".cubrid.synonym.Messages", Messages.class);
    }

    public static String dropSynonymWarnMSG1;
    public static String dropSynonymWarnMSG2;
    public static String infoSQLScriptTab;
    public static String infoSynonymTab;
    public static String msgWarning;
    public static String okBTN;
    public static String cancleBTN;
    public static String invalidSynonymNameError;
    public static String invalidTargetNameError;
    public static String enterEventTargetMSG;
    public static String synonymInfo;
    public static String synonymGroupName;
    public static String synonymName;
    public static String synonymDesscription;

    public static String msgInformation;
    public static String newSynonymSuccess;
    public static String newSynonymMSGTitle;
    public static String newSynonymMsg;

    public static String synonymActionGroupText;
    public static String synonymStatusGroupText;
    public static String synonymPriorityGroupText;
    public static String synonymPriorityText;
    public static String sqlStatementMSG;
    public static String synonymAlterMSG;
    public static String alterSynonymShellTitle;
    public static String synonymAlterMSGTitle;
    public static String alterSynonymSuccess;
    public static String errFormatPriority;
    public static String errRangePriority;
    public static String errPriorityFormat;

    public static String dropSynonymTaskName;
    public static String addSynonymTaskName;
    public static String alterSynonymTaskName;

    public static String errNameNoExist;

    public static String synonymsDetailInfoPartTitle;

    public static String synonymsDetailInfoPartCreateSynonymBtn;
    public static String synonymsDetailInfoPartEditSynonymBtn;
    public static String synonymsDetailInfoPartDropSynonymBtn;

    public static String synonymsDetailInfoPartTableOwnerCol;
    public static String synonymsDetailInfoPartTableNameCol;
    public static String synonymsDetailInfoPartTableTargetOwnerCol;
    public static String synonymsDetailInfoPartTableTargetNameCol;
    public static String synonymsDetailInfoPartTableCommentCol;
    public static String errSynonymNoSelection;

    public static String dropSynonymSuccessMsg;

    public static String ToolTipName;
    public static String ToolTipOwner;
    public static String ToolTipTargetName;
    public static String ToolTipTargetOwner;
    public static String ToolTipComment;

    public static String synonymInfoLabel;
    public static String synonymTargetInfoLabel;
    public static String synonymCommentLabel;
}
