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

import com.cubrid.common.ui.spi.progress.ExecTaskWithProgress;
import com.cubrid.cubridmanager.core.cubrid.table.model.DBClasses;
import com.cubrid.cubridmanager.ui.replication.Messages;
import com.cubrid.cubridmanager.ui.replication.control.GetAllClassesTaskExecutor;
import com.cubrid.cubridmanager.ui.replication.control.SelectTableComp;
import com.cubrid.cubridmanager.ui.replication.editor.model.HostNode;
import com.cubrid.cubridmanager.ui.replication.editor.model.MasterNode;
import java.util.List;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Select replicated tables wizard page for set master database information wizard
 *
 * @author pangqiren
 * @version 1.0 - 2009-8-26 created by pangqiren
 */
public class SelectTablesPage extends WizardPage implements IPageChangedListener {

    public static final String PAGENAME = "SetMasterDbWizard/SelectTablesPage";
    private final MasterNode master;
    private HostNode host = null;
    private boolean isEditable = true;
    SelectTableComp selectTableComp = null;

    /** The constructor */
    public SelectTablesPage(MasterNode master) {
        super(PAGENAME);
        this.master = master;
        if (this.master != null && this.master.getParent() instanceof HostNode) {
            host = (HostNode) master.getParent();
        }
        selectTableComp = new SelectTableComp();
        setPageComplete(false);
    }

    /**
     * Create the control for this page
     *
     * @param parent Composite
     */
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        composite.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(gridData);

        selectTableComp.createTableGroup(composite);
        selectTableComp.setEditable(isEditable);

        setTitle(Messages.titleSelectReplTables);
        setMessage(Messages.msgSelectReplTables);

        setControl(composite);
    }

    /**
     * @see
     *     org.eclipse.jface.dialogs.IPageChangedListener#pageChanged(org.eclipse.jface.dialogs.PageChangedEvent)
     * @param event PageChangedEvent
     */
    public void pageChanged(PageChangedEvent event) {
        IWizardPage page = (IWizardPage) event.getSelectedPage();
        if (page.getName().equals(PAGENAME)) {
            SelectDatabasePage selectDatabasePage =
                    (SelectDatabasePage) getWizard().getPage(SelectDatabasePage.PAGENAME);
            String dbName = selectDatabasePage.getDbName();
            String dbaPassword = selectDatabasePage.getDbaPassword();
            if (host != null && host.isValid()) {
                String ip = host.getIp();
                String port = host.getPort();
                String userName = host.getUserName();
                String password = host.getPassword();
                GetAllClassesTaskExecutor taskExcutor =
                        new GetAllClassesTaskExecutor(
                                getShell(), ip, port, userName, password, dbName, dbaPassword);
                new ExecTaskWithProgress(taskExcutor).exec();
                if (taskExcutor.isSuccess()) {
                    DBClasses dbClasses = taskExcutor.getDBClasses();
                    selectTableComp.fillTableViewer(dbClasses);
                    selectTableComp.setReplAllTables(master.isReplicateAll());
                    selectTableComp.setSelectedTableList(master.getReplicatedClassList());
                    setErrorMessage(null);
                    setPageComplete(isEditable);
                } else {
                    setErrorMessage(Messages.errPreviousPage);
                    setPageComplete(false);
                }
            }
        }
    }

    public List<String> getReplTableList() {
        return selectTableComp.getSelectedTableList();
    }

    public boolean isReplAllTables() {
        return selectTableComp.isReplAllTables();
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }
}
