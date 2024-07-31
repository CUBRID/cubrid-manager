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
package com.cubrid.cubridmanager.ui.mondashboard.dialog.wizard;

import com.cubrid.cubridmanager.core.common.model.ServerType;
import com.cubrid.cubridmanager.ui.mondashboard.Messages;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.BrokerNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.DatabaseNode;
import com.cubrid.cubridmanager.ui.mondashboard.editor.model.HostNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Add host and database to dash board wizard
 *
 * @author pangqiren
 * @version 1.0 - 2010-6-2 created by pangqiren
 */
public class AddHostAndDbWizard extends Wizard {

    private SelectDbPage selectDbPage;
    private SelectBrokerPage selectBrokerPage;
    private final List<HostNode> addedHostNodeList = new ArrayList<HostNode>();
    private SetHostInfoPage setHostInfoPage;
    private final HostNode selectedHostNode;
    private final List<HostNode> allHostNodeList;
    // 0: Add all 1: add database monitor 2: add broker monitor
    private final int addedType;

    /**
     * The constructor
     *
     * @param addedHostNodeList
     * @param hostNode
     */
    public AddHostAndDbWizard(HostNode hostNode, List<HostNode> allHostNodeList, int type) {
        setWindowTitle(Messages.titleAddDashboardWizard);
        selectedHostNode = hostNode;
        this.allHostNodeList = allHostNodeList;
        addedType = type;
    }

    /** Add wizard pages */
    public void addPages() {
        WizardDialog dialog = (WizardDialog) getContainer();
        if (addedType == 0
                || selectedHostNode == null
                || selectedHostNode.getServerInfo() == null) {
            setHostInfoPage =
                    new SetHostInfoPage(selectedHostNode, addedHostNodeList, allHostNodeList);
            addPage(setHostInfoPage);
        }
        ServerType serverType =
                selectedHostNode == null || selectedHostNode.getServerInfo() == null
                        ? null
                        : selectedHostNode.getServerInfo().getServerType();
        if (addedType == 0 || addedType == 1) {
            selectDbPage = new SelectDbPage();
            selectDbPage.setServerType(serverType);
            addPage(selectDbPage);
            dialog.addPageChangedListener(selectDbPage);
        }
        if (addedType == 0 || addedType == 2) {
            selectBrokerPage = new SelectBrokerPage();
            selectBrokerPage.setServerType(serverType);
            addPage(selectBrokerPage);
            dialog.addPageChangedListener(selectBrokerPage);
        }
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @return boolean
     */
    public boolean canFinish() {
        if (getContainer().getCurrentPage() == setHostInfoPage) {
            return setHostInfoPage.isCanFinished() && setHostInfoPage.isPageComplete();
        } else {
            return true;
        }
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {
        List<Map<String, Object>> dbNodeList =
                selectDbPage == null ? null : selectDbPage.getDbNodeList();
        List<Map<String, Object>> brokerNodeNodeList =
                selectBrokerPage == null ? null : selectBrokerPage.getBrokerNodeList();
        HostNode hostNode =
                mergeHostNode(setHostInfoPage == null ? null : setHostInfoPage.getHostNode());
        for (int i = 0; dbNodeList != null && i < dbNodeList.size(); i++) {
            Map<String, Object> map = dbNodeList.get(i);
            hostNode = mergeHostNode((HostNode) map.get("6"));
            DatabaseNode dbNode = (DatabaseNode) map.get("7");
            if (hostNode != null) {
                dbNode.setParent(hostNode);
                hostNode.getCopyedHaNodeList().remove(dbNode);
                hostNode.getCopyedHaNodeList().add(dbNode);
            }
        }
        for (int i = 0; brokerNodeNodeList != null && i < brokerNodeNodeList.size(); i++) {
            Map<String, Object> map = brokerNodeNodeList.get(i);
            hostNode = mergeHostNode((HostNode) map.get("8"));
            BrokerNode brokerNode = (BrokerNode) map.get("9");
            if (hostNode != null) {
                brokerNode.setParent(hostNode);
                hostNode.getCopyedHaNodeList().remove(brokerNode);
                hostNode.getCopyedHaNodeList().add(brokerNode);
            }
        }
        return true;
    }

    /**
     * Merger host node
     *
     * @param hostNode The HostNode
     * @return The HostNode
     */
    private HostNode mergeHostNode(HostNode hostNode) {
        if (hostNode == null) {
            return null;
        }
        for (int i = 0; i < addedHostNodeList.size(); i++) {
            HostNode node = addedHostNodeList.get(i);
            if (hostNode.equals(node)) {
                node.setPassword(hostNode.getPassword());
                node.setHostStatusInfo(hostNode.getHostStatusInfo());
                return node;
            }
        }
        addedHostNodeList.add(hostNode);
        return hostNode;
    }

    public List<HostNode> getAddedHostNodeList() {
        return addedHostNodeList;
    }

    public SelectDbPage getSelectDbPage() {
        return selectDbPage;
    }

    public SelectBrokerPage getSelectBrokerPage() {
        return selectBrokerPage;
    }

    public SetHostInfoPage getSetHostInfoPage() {
        return setHostInfoPage;
    }

    public int getAddedType() {
        return addedType;
    }

    public HostNode getSelectedHostNode() {
        return selectedHostNode;
    }
}
