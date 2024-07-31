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
package com.cubrid.common.ui.er.action;

import com.cubrid.common.ui.er.editor.ERSchemaEditor;
import com.cubrid.common.ui.er.model.ERSchema;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;

/**
 * Action to toggle the layout between manual and automatic
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-5-6 created by Yu Guojia
 */
public class FlyoutChangeLayoutAction extends Action {
    public static String ID = FlyoutChangeLayoutAction.class.getName();
    private IEditorPart editor;
    private boolean checked;

    public FlyoutChangeLayoutAction(IEditorPart editor) {
        super("Automatic Layout", Action.AS_CHECK_BOX);
        this.editor = editor;
    }

    public void run() {
        if (editor instanceof ERSchemaEditor) {
            ERSchemaEditor erSchemaEditor = (ERSchemaEditor) editor;
            ERSchema erSchema = erSchemaEditor.getSchema();
            boolean isManual = erSchema.isLayoutManualDesired();
            erSchema.setLayoutManualDesiredAndFire(!isManual);
            checked = !isManual;
            setChecked(checked);
        }
    }

    public boolean isChecked() {
        if (editor != null) {
            return isChecked(editor);
        } else {
            return super.isChecked();
        }
    }

    public boolean isChecked(IEditorPart editor) {
        if (editor instanceof ERSchemaEditor) {
            ERSchemaEditor erSchemaEditor = (ERSchemaEditor) editor;
            ERSchema erSchema = erSchemaEditor.getSchema();
            boolean checkTrue = erSchema.isLayoutManualDesired();
            return (!checkTrue);
        } else {
            return false;
        }
    }

    public void setActiveEditor(IEditorPart editor) {
        this.editor = editor;
        boolean localChecked = isChecked(editor);
        if (localChecked) {
            firePropertyChange(CHECKED, Boolean.FALSE, Boolean.TRUE);
        } else {
            firePropertyChange(CHECKED, Boolean.TRUE, Boolean.FALSE);
        }
    }
}
