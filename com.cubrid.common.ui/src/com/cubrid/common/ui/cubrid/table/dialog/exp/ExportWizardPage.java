/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.cubrid.table.dialog.exp;

import com.cubrid.common.ui.cubrid.table.Messages;
import com.cubrid.common.ui.cubrid.table.progress.ExportConfig;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * The ExportWizardPage
 *
 * @author Kevin.Wang
 * @version 1.0 - 2013-9-6 created by Kevin.Wang
 */
public class ExportWizardPage extends WizardPage
        implements IPageChangedListener, IPageChangingListener {

    protected ExportWizardPage(String pageName) {
        super(pageName);
    }

    public ExportWizardPage(String pageName, String title, ImageDescriptor titleImage) {
        super(pageName, Messages.exportShellTitle, titleImage);
    }

    public void createControl(Composite parent) {}

    /**
     * Handle export wizard page changed.
     *
     * @param event PageChangedEvent
     */
    public void pageChanged(PageChangedEvent event) {
        if (event.getSelectedPage() == this) {
            afterShowCurrentPage(event);
        }
    }

    /**
     * Handle export wizard page changing.
     *
     * @param event PageChangingEvent
     */
    public void handlePageChanging(PageChangingEvent event) {
        if (!event.doit) {
            return;
        }
        if (event.getCurrentPage() == this) {
            handlePageLeaving(event);
        } else if (event.getTargetPage() == this) {
            handlePageShowing(event);
        }
    }

    /**
     * When export wizard displayed current page.
     *
     * @param event PageChangedEvent
     */
    protected void afterShowCurrentPage(PageChangedEvent event) {
        // Default is doing nothing.
    }

    /**
     * When export wizard will show next page or previous page.
     *
     * @param event PageChangingEvent
     */
    protected void handlePageLeaving(PageChangingEvent event) {
        // Default is doing nothing.
    }

    /**
     * When export wizard will show this page.
     *
     * @param event PageChangingEvent
     */
    protected void handlePageShowing(PageChangingEvent event) {
        // Default is doing nothing.
    }

    public ExportDataWizard getExportDataWizardWizard() {
        return (ExportDataWizard) super.getWizard();
    }

    /**
     * getDatabase
     *
     * @return
     */
    public CubridDatabase getDatabase() {
        return getExportDataWizardWizard().getDatabase();
    }

    /**
     * get config model
     *
     * @return
     */
    public ExportConfig getExportConfig() {
        return getExportDataWizardWizard().getConfigModel();
    }
}
