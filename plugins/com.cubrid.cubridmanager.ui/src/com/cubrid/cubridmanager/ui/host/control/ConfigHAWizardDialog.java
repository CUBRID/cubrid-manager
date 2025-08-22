/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.cubridmanager.ui.host.control;

import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Config HA Wizard Dialog Description
 *
 * @author Kevin.Wang
 * @version 1.0 - 2012-11-27 created by Kevin.Wang
 */
public class ConfigHAWizardDialog extends CMWizardDialog {
    /**
     * @param parentShell
     * @param newWizard
     */
    public ConfigHAWizardDialog(Shell parentShell, IWizard newWizard) {
        super(parentShell, newWizard);
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
    }

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

    /**
     * Overwrite the method. disable the ProgressMonitorPart which take up place on bottom of page
     *
     * @param parent Composite
     * @return Control
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        for (Control control : composite.getChildren()) {
            if (control instanceof ProgressMonitorPart) {
                GridData gd = (GridData) control.getLayoutData();
                gd.exclude = true;
            }
        }

        return composite;
    }
}
