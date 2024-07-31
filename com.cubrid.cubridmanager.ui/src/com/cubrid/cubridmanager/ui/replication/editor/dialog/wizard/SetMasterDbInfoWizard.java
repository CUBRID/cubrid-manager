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
package com.cubrid.cubridmanager.ui.replication.editor.dialog.wizard;

import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import java.util.List;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Set master database information wizard
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SetMasterDbInfoWizard extends Wizard {
    private SelectTablesPage selectClassesPage = null;
    private SelectDatabasePage selectDatabasePage = null;
    private final MasterNode master;
    private boolean isEditable = true;

    /** The constructor */
    public SetMasterDbInfoWizard(MasterNode master) {
        setWindowTitle(Messages.titleSetMdbInfoDialog);
        this.master = master;
    }

    public MasterNode getMaster() {
        return this.master;
    }

    /** Add wizard pages */
    public void addPages() {
        selectDatabasePage = new SelectDatabasePage(master);
        selectDatabasePage.setEditable(isEditable);
        addPage(selectDatabasePage);
        selectClassesPage = new SelectTablesPage(master);
        selectClassesPage.setEditable(isEditable);
        addPage(selectClassesPage);
        WizardDialog dialog = (WizardDialog) getContainer();
        dialog.addPageChangedListener(selectClassesPage);
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     * @return boolean
     */
    public boolean canFinish() {
        return getContainer().getCurrentPage() == selectClassesPage;
    }

    /**
     * Called when user clicks Finish
     *
     * @return boolean
     */
    public boolean performFinish() {
        String mdbName = selectDatabasePage.getDbName();
        String dbaPassword = selectDatabasePage.getDbaPassword();
        String replServerPort = selectDatabasePage.getReplServerPort();
        List<String> replTableList = selectClassesPage.getReplTableList();
        boolean isReplAllTables = selectClassesPage.isReplAllTables();
        String dbPath = "";
        HostNode host = (HostNode) master.getParent();
        List<DatabaseInfo> dbInfoList = host.getDatabaseInfoList();
        for (int i = 0; dbInfoList != null && i < dbInfoList.size(); i++) {
            DatabaseInfo dbInfo = dbInfoList.get(i);
            if (mdbName.equalsIgnoreCase(dbInfo.getDbName())) {
                dbPath = dbInfo.getDbDir();
            }
        }
        master.setDbName(mdbName);
        master.setDbaPassword(dbaPassword);
        master.setReplServerPort(replServerPort);
        master.setReplicatedClassList(replTableList);
        master.setReplicateAll(isReplAllTables);
        master.setName(mdbName);
        master.setDbPath(dbPath);
        return true;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
}
