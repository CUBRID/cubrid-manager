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
package com.cubrid.cubridquery.ui.connection.action;

import com.cubrid.common.ui.spi.dialog.CMWizardDialog;
import com.cubrid.cubridquery.ui.connection.dialog.CreateConnectionByUrlWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Create Connection By URL Action
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 18, 2012 created by Kevin.Wang
 */
public class ConnectionUrlImportAction extends Action {

    public static final String ID = ConnectionUrlImportAction.class.getName();

    /**
     * The constructor
     *
     * @param text String
     * @param image ImageDescriptor
     */
    public ConnectionUrlImportAction(Shell shell, String text, ImageDescriptor image) {
        super(text);
        this.setId(ID);
        setImageDescriptor(image);
    }

    /** Import hosts and groups */
    public void run() {
        CMWizardDialog dialog =
                new CMWizardDialog(
                        Display.getCurrent().getActiveShell(), new CreateConnectionByUrlWizard());
        dialog.setPageSize(600, 300);
        dialog.open();
    }
}
