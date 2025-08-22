/*
 * Copyright (C) 2012 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.common.dialog;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * edit annotation of cubrid broker conf dialog
 *
 * @author fulei
 * @version 1.0 - 2012-11-01 created by fulei
 */
public class CubridBrokerConfEditAnnotationDialog extends CMTitleAreaDialog {

    private final String parentString;
    private String annotation;
    private StyledText annotationText;
    private Map<String, String> valueMap;
    private String annotationKey;

    /**
     * constructor
     *
     * @param parentShell
     * @param annotation
     * @param parentString
     */
    public CubridBrokerConfEditAnnotationDialog(
            Shell parentShell,
            String parentString,
            String annotationKey,
            Map<String, String> valueMap) {
        super(parentShell);
        this.annotationKey = annotationKey;
        this.annotation = valueMap.get(annotationKey) == null ? "" : valueMap.get(annotationKey);
        this.parentString = parentString;
        this.valueMap = valueMap;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        Composite parentComp = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(parentComp, SWT.NONE);
        {
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            GridLayout layout = new GridLayout(1, true);
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
        }

        annotationText = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        annotationText.setLayoutData(
                CommonUITool.createGridData(GridData.FILL_BOTH, 2, 1, -1, 200));
        annotationText.setText(annotation);

        setTitle(Messages.cubridBrokerConfEditorTableMenuEditAnnotation);
        setMessage(Messages.cubridBrokerConfEditorTableMenuEditAnnotation + parentString);
        return parentComp;
    }

    /**
     * Call this method when press button
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            String annotation = annotationText.getText();

            if (validateAnnotation(annotation)) {
                return;
            }
            if (!annotation.endsWith(StringUtil.NEWLINE)) {
                annotation += StringUtil.NEWLINE;
            }
            valueMap.put(annotationKey, annotation);
        }
        super.buttonPressed(buttonId);
    }

    private boolean validateAnnotation(String annotation) {
        String[] lines = annotation.split(StringUtil.NEWLINE);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!line.trim().equals("") && !line.startsWith("#")) {
                String message =
                        Messages.bind(
                                Messages.cubridBrokerConfEditAnnotationDialogErrorMsg,
                                line,
                                Integer.toString(i + 1));
                CommonUITool.openErrorBox(message);
                return true;
            }
        }
        return false;
    }
}
