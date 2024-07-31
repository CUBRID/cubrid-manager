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
package com.cubrid.cubridmanager.ui.common.action;

import com.cubrid.common.ui.spi.action.ActionManager;
import com.cubrid.common.ui.spi.action.SelectionAction;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerAction;
import com.cubrid.cubridmanager.ui.broker.action.StartBrokerEnvAction;
import com.cubrid.cubridmanager.ui.cubrid.database.action.StartDatabaseAction;
import com.cubrid.cubridmanager.ui.spi.model.CubridBroker;
import com.cubrid.cubridmanager.ui.spi.model.CubridBrokerFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * This action is responsible to start database or broker
 *
 * @author pangqiren
 * @version 1.0 - 2009-5-19 created by pangqiren
 */
public class StartRetargetAction extends SelectionAction {

    public static final String ID = StartRetargetAction.class.getName();
    public static final String ID_BIG = StartRetargetAction.class.getName() + "Big";

    /**
     * The constructor
     *
     * @param shell
     * @param text
     * @param enabledIcon
     * @param disabledIcon
     */
    public StartRetargetAction(
            Shell shell,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon,
            boolean bigButton) {
        this(shell, null, text, enabledIcon, disabledIcon, bigButton);
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
    public StartRetargetAction(
            Shell shell,
            ISelectionProvider provider,
            String text,
            ImageDescriptor enabledIcon,
            ImageDescriptor disabledIcon,
            boolean bigButton) {
        super(shell, provider, text, enabledIcon);
        this.setId(bigButton ? ID_BIG : ID);
        this.setToolTipText(text);
        this.setDisabledImageDescriptor(disabledIcon);
    }

    /**
     * Return whether this action support to select multi object,if not support,this action will be
     * disabled
     *
     * @return <code>true</code> if allow multi selection;<code>false</code> otherwise
     */
    public boolean allowMultiSelections() {
        return false;
    }

    /**
     * Return whether this action support this object,if not support,this action will be disabled
     *
     * @param obj the Object
     * @return <code>true</code> if support this obj;<code>false</code> otherwise
     */
    public boolean isSupported(Object obj) {
        if (obj instanceof ICubridNode) {
            ICubridNode node = (ICubridNode) obj;
            if (NodeType.SERVER.equals(node.getType())) {
                StartServiceAction startServiceAction =
                        (StartServiceAction)
                                ActionManager.getInstance().getAction(StartServiceAction.ID);
                return startServiceAction.isSupported(obj);
            }
        }
        if (obj instanceof ISchemaNode) {
            StartDatabaseAction startDatabaseAction =
                    (StartDatabaseAction)
                            ActionManager.getInstance().getAction(StartDatabaseAction.ID);
            return startDatabaseAction == null ? false : startDatabaseAction.isSupported(obj);
        }
        if (obj instanceof CubridBroker) {
            StartBrokerAction startBrokerAction =
                    (StartBrokerAction) ActionManager.getInstance().getAction(StartBrokerAction.ID);
            return startBrokerAction == null ? false : startBrokerAction.isSupported(obj);
        }
        if (obj instanceof CubridBrokerFolder) {
            StartBrokerEnvAction startBrokerEnvAction =
                    (StartBrokerEnvAction)
                            ActionManager.getInstance().getAction(StartBrokerEnvAction.ID);
            return startBrokerEnvAction == null ? false : startBrokerEnvAction.isSupported(obj);
        }
        return false;
    }

    /** start database or broker */
    public void run() {
        final Object[] obj = this.getSelectedObj();
        if (!isSupported(obj[0])) {
            return;
        }
        ICubridNode node = (ICubridNode) obj[0];

        if (NodeType.SERVER.equals(node.getType())) {
            StartServiceAction startServiceAction =
                    (StartServiceAction)
                            ActionManager.getInstance().getAction(StartServiceAction.ID);
            startServiceAction.run();
        }
        if (node instanceof ISchemaNode) {
            StartDatabaseAction startDatabaseAction =
                    (StartDatabaseAction)
                            ActionManager.getInstance().getAction(StartDatabaseAction.ID);
            startDatabaseAction.run();
        }
        if (node instanceof CubridBroker) {
            StartBrokerAction startBrokerAction =
                    (StartBrokerAction) ActionManager.getInstance().getAction(StartBrokerAction.ID);
            startBrokerAction.run();
        }
        if (node instanceof CubridBrokerFolder) {
            StartBrokerEnvAction startBrokerEnvAction =
                    (StartBrokerEnvAction)
                            ActionManager.getInstance().getAction(StartBrokerEnvAction.ID);
            startBrokerEnvAction.run();
        }
    }
}
