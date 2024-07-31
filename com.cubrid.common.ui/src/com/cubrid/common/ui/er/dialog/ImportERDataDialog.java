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
package com.cubrid.common.ui.er.dialog;

import com.cubrid.common.core.util.FileUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlContainer;
import com.cubrid.common.ui.er.model.ERSchema;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Import ERWin or Gson format data to ERD
 *
 * @author Yu Guojia
 * @version 1.0 - 2013-7-23 created by Yu Guojia
 */
public class ImportERDataDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(ImportERDataDialog.class);

    private String filename = null;
    private ERSchema erSchema = null;;
    private Document doc = null;
    private ERXmlContainer container;
    private String gsonData = null;
    private int selectedMode = -1;

    public ImportERDataDialog(Shell parentShell, ERSchema erSchema) {
        super(parentShell);
        this.erSchema = erSchema;
    }

    @Override
    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().pack();
    }

    @Override
    protected Control createDialogArea(Composite p) {
        Composite parent = (Composite) super.createDialogArea(p);
        Composite comp = new Composite(parent, SWT.NONE);
        {
            final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
            layoutData.widthHint = 400;
            comp.setLayoutData(layoutData);

            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            layout.makeColumnsEqualWidth = false;
            comp.setLayout(layout);
        }

        Label fileLbl = new Label(comp, SWT.NONE);
        fileLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        fileLbl.setText(Messages.lblFilePath);

        final Text filePathText = new Text(comp, SWT.BORDER);
        {
            GridData gdFilePathData = new GridData();
            gdFilePathData.widthHint = 200;
            filePathText.setLayoutData(gdFilePathData);
        }

        final Button fileButton = new Button(comp, SWT.PUSH);
        fileButton.setText(Messages.btnBrowse);
        fileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                        dialog.setFilterExtensions(new String[] {"*.erd", "*.xml"});
                        dialog.setFilterNames(new String[] {"ERD file", "ERwin XML file"});
                        String filePath = dialog.open();
                        if (filePath == null || filePath.equals("")) {
                            ImportERDataDialog.this.close();
                            return;
                        }
                        filename = filePath;
                        filePathText.setText(filename);

                        selectedMode = dialog.getFilterIndex();
                        if (isGsonFile()) {
                            handleGsonFile(filename);
                        } else if (isERWinFile()) {
                            handleDocument(filename);
                        }
                    }

                    private void handleDocument(String filePath) {
                        DocumentBuilder docBuilder = null;
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        try {
                            factory.setIgnoringComments(true);
                            docBuilder = factory.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            LOGGER.error(e.getMessage(), e);
                            return;
                        }

                        try {
                            FileInputStream fis = new FileInputStream(filePath);
                            doc = docBuilder.parse(fis);
                            fis.close();
                        } catch (FileNotFoundException e) {
                            LOGGER.error(e.getMessage(), e);
                            CommonUITool.openErrorBox(Messages.errFileNotExist);
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage(), e);
                            String errMsg = Messages.bind(Messages.errFileCannotRead, filePath);
                            CommonUITool.openErrorBox(errMsg);
                        } catch (SAXException e) {
                            LOGGER.error(e.getMessage(), e);
                            CommonUITool.openErrorBox(Messages.errInvalidFile);
                        }
                    }

                    private void handleGsonFile(String filePath) {
                        try {
                            gsonData = FileUtil.readData(filePath, FileUtil.CHARSET_UTF8);
                        } catch (IOException e) {
                            CommonUITool.openErrorBox(e.getMessage());
                            return;
                        }
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });

        setTitle(com.cubrid.common.ui.er.Messages.titleImportSchemaData);
        setMessage(com.cubrid.common.ui.er.Messages.msgImportSchemaData);
        getShell().setText(com.cubrid.common.ui.er.Messages.titleImportSchemaData);

        comp.pack();
        return comp;
    }

    @Override
    protected void okPressed() {
        if (isERWinFile()) {
            container = new ERXmlContainer();
            container.parse(erSchema.getCubridDatabase(), doc, null);
        }
        super.okPressed();
    }

    public boolean isGsonFile() {
        if (selectedMode == 0) {
            return true;
        }
        return false;
    }

    public boolean isERWinFile() {
        if (selectedMode == 1) {
            return true;
        }
        return false;
    }

    public String getGsonData() {
        return gsonData;
    }

    public ERXmlContainer getERWinContainer() {
        return container;
    }
}
