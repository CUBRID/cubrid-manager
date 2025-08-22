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
package com.cubrid.common.ui.common.preference;

import com.cubrid.common.ui.common.Messages;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for JDBC Management
 *
 * @author robinhood 2009-9-8
 */
public class JdbcManagePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "com.cubrid.common.ui.preference.jdbcmanage";

    private JdbcManageComposite container;

    public JdbcManagePreferencePage() {
        super(Messages.jdbcManagePageName, null);
        noDefaultAndApplyButton();
    }

    /**
     * Initial the data
     *
     * @param workbench the workbench
     */
    public void init(IWorkbench workbench) {}

    /** Load the preference */
    private void loadPreference() {
        container.loadPreference();
    }

    /**
     * Execute and save
     *
     * @return <code>true</code> if it is successful;<code>false</code>otherwise
     */
    public boolean performOk() {
        if (!checkValues()) {
            return false;
        }
        if (container != null) {
            container.save();
        }
        return true;
    }

    /**
     * check input value
     *
     * @return <code>true</code> if it is valid;<code>false</code>otherwise
     */
    private boolean checkValues() {
        return true;
    }

    /**
     * Create the page content
     *
     * @param parent the parent composite
     * @return the control
     */
    protected Control createContents(Composite parent) {
        container = new JdbcManageComposite(parent);
        loadPreference();
        return container;
    }
}
