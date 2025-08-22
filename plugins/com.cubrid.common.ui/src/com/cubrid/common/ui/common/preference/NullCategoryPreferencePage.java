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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Category Preference page
 *
 * @author pangqiren
 * @version 1.0 - 2010-12-16 created by pangqiren
 */
public class NullCategoryPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String ID = "com.cubrid.common.ui.preference.category";
    private String message = null;

    public NullCategoryPreferencePage() {
        noDefaultAndApplyButton();
    }

    public NullCategoryPreferencePage(String title, String msg) {
        super(title);
        message = msg;
        noDefaultAndApplyButton();
    }

    /**
     * Create the content
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createContents(Composite parent) {
        Composite nullComposite = new Composite(parent, SWT.NONE);
        nullComposite.setLayout(new GridLayout());
        nullComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        if (message != null) {
            Label label = new Label(nullComposite, SWT.NONE);
            label.setText(message);
        }
        return nullComposite;
    }

    /**
     * Initial
     *
     * @param workbench IWorkbench
     */
    public void init(IWorkbench workbench) {}
}
