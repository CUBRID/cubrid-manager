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
package com.cubrid.cubridmanager.ui.replication.control;

import com.cubrid.common.ui.spi.dialog.IUpdatable;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.replication.Messages;
import java.util.Map;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Set replication parameters page for change slave database wizard
 *
 * @author wuyingshi
 * @version 1.0 - 2009-9-17 created by wuyingshi
 */
public class SetReplicationParamPage extends WizardPage implements IUpdatable {

    public static final String PAGENAME = "ChangeSlaveDbWizard/SetReplicationParamPage";
    private final SetReplicationParamComp paramEditor;

    /** The constructor */
    public SetReplicationParamPage() {
        super(PAGENAME);
        paramEditor = new SetReplicationParamComp(this);
    }

    /**
     * Creates the controls for this page
     *
     * @param parent Composite
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        paramEditor.createReplicationParamComp(composite);
        paramEditor.init();

        setTitle(Messages.chsldb1titleSetReplicationParamPage);
        setMessage(Messages.chsldb1msgSetReplicationParamPage);
        setControl(composite);
    }

    /**
     * Get parameter map
     *
     * @return paramEditor.getParamMap()
     */
    public Map<String, String> getParamMap() {
        return paramEditor.getParamMap();
    }

    /** @see com.cubrid.common.ui.spi.dialog.IUpdatable#updateUI() */
    public void updateUI() {
        String errorMsg = paramEditor.getErrorMsg();
        if (errorMsg != null) {
            CommonUITool.openErrorBox(errorMsg);
        }
    }
}
