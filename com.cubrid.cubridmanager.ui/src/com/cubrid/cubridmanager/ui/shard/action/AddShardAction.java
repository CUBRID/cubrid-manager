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
package com.cubrid.cubridmanager.ui.shard.action;

import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.cubridmanager.core.common.model.ServerUserInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DbCreateAuthType;
import com.cubrid.cubridmanager.ui.CubridManagerUIPlugin;
import com.cubrid.cubridmanager.ui.shard.Messages;
import com.cubrid.cubridmanager.ui.shard.control.AddShardWizard;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to add a shard.
 *
 * @author Tobi
 * @version 1.0
 * @date 2012-11-30
 */
public class AddShardAction extends SelectionAction {

    public static final String ID = AddShardAction.class.getName();
    private ICubridNode node;
    private CubridServer server;

    /**
     * The constructor
     *
     * @param shell
     */
    public AddShardAction(Shell shell) {
        this(
                shell,
                null,
                Messages.addShardActionName,
                CubridManagerUIPlugin.getImageDescriptor("icons/action/conf_edit.png"));
    }

    /**
     * The constructor
     *
     * @param shell
     * @param provider
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public AddShardAction(
            Shell shell, ISelectionProvider provider, String text, ImageDescriptor icon) {
        super(shell, provider, text, icon);
        this.setId(ID);
        this.setToolTipText(text);
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#allowMultiSelections ()
     * @return false
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * @see com.cubrid.common.ui.spi.action.ISelectionAction#isSupported(java .lang.Object)
     * @param obj the object
     * @return <code>true</code> if support this object;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (node.getServer() == null) {
                return false;
            }
            ServerUserInfo userInfo = node.getServer().getServerInfo().getLoginedUserInfo();
            if (userInfo != null && userInfo.getDbCreateAuthType() == DbCreateAuthType.AUTH_ADMIN) {
                return true;
            }
        }
        return false;
    }

    /** Open a dialog to add a shard. */
    public void run() {
        Object[] obj = this.getSelectedObj();
        if (obj != null && obj.length > 0) {
            if (!isSupported(obj[0])) {
                this.setEnabled(false);
                return;
            }
            node = (ICubridNode) obj[0];
            server = node.getServer();
        }
        CMWizardDialog dialog =
                new CMWizardDialog(getShell(), new AddShardWizard(server)) {
                    /**
                     * Overwrite the method. Auto add IPageChangingListener(s);
                     *
                     * @param parent of the control.
                     * @return Control
                     */
                    protected Control createContents(Composite parent) {
                        Control result = super.createContents(parent);
                        IWizardPage[] pages = this.getWizard().getPages();
                        for (IWizardPage page : pages) {
                            if (page instanceof IPageChangingListener) {
                                this.addPageChangingListener((IPageChangingListener) page);
                            }
                            if (page instanceof IPageChangedListener) {
                                this.addPageChangedListener((IPageChangedListener) page);
                            }
                        }
                        return result;
                    }
                };
        dialog.setPageSize(660, 450);
        dialog.open();
    }
}
