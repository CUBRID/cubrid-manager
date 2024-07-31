/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.shard.action;

import com.cubrid.common.ui.spi.LayoutManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.CasAuthType;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.ui.shard.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridShardFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * A action in order to show the shard status editor part
 *
 * @author Tobi
 * @version 1.0
 * @date 2013-1-6
 */
public class ShowShardStatusAction extends SelectionAction {

    public static final String ID = ShowShardStatusAction.class.getName();

    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    public ShowShardStatusAction(Shell shell) {
        this(shell, Messages.showShardStatusActionName, null);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param icon
     */
    public ShowShardStatusAction(Shell shell, String text, ImageDescriptor icon) {
        this(shell, null, text, icon);
    }

    /**
     * The constructor
     *
     * @param shell
     * @param text
     */
    protected ShowShardStatusAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * Override the run method in order to complete showing shard status <br>
     * server to a shard
     */
    public void run() {
        Object[] obj = this.getSelectedObj();
        ICubridNode node = (ICubridNode) obj[0];
        LayoutManager.getInstance().getWorkbenchContrItem().openEditorOrView(node);
    }

    /**
     * Makes this action not support to select multi object
     *
     * @return false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the object
     * @return <code>true</code> if supported;<code>false</code>
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof CubridShardFolder) {
            CubridShardFolder selection = ((CubridShardFolder) obj);
            if (!selection.isEnable()) {
                return false;
            }
            ServerUserInfo userInfo = selection.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo == null
                    || (CasAuthType.AUTH_ADMIN != userInfo.getCasAuth()
                            && CasAuthType.AUTH_MONITOR != userInfo.getCasAuth())) {
                return false;
            }
            return true;
        }
        return false;
    }
}
