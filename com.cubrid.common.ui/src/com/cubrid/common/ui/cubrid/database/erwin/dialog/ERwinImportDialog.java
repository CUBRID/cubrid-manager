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
package com.cubrid.common.ui.cubrid.database.erwin.dialog;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.common.Messages;
import com.cubrid.common.ui.cubrid.database.erwin.ERXmlContainer;
import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.util.CommonUITool;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ERwinImportDialog extends CMTitleAreaDialog {
    private static final Logger LOGGER = LogUtil.getLogger(ERwinImportDialog.class);

    private String filename = null;
    private Combo subjectCombo = null;
    private CubridDatabase database = null;;
    private Document doc = null;
    private final ERXmlContainer container = new ERXmlContainer();

    public ERwinImportDialog(Shell parentShell, CubridDatabase database) {
        super(parentShell);
        this.database = database;
    }

    protected void constrainShellSize() {
        super.constrainShellSize();
        getShell().pack();
    }

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

        Button fileButton = new Button(comp, SWT.PUSH);
        fileButton.setText(Messages.btnBrowse);
        fileButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
                        dialog.setFilterExtensions(new String[] {"*.xml"});
                        dialog.setFilterNames(new String[] {"ERwin XML"});
                        String filePath = dialog.open();
                        if (filePath == null || filePath.equals("")) {
                            CommonUITool.openErrorBox(Messages.infoMissingSelectFile);
                            return;
                        }
                        filename = filePath;
                        filePathText.setText(filename);
                        handleDocument(filename);
                    }

                    private void handleDocument(
                            String filePath) { // FIXME logic code move to core module
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
                            Map<String, Node> subjectMap = readSubjectAre(doc);
                            setToCombo(subjectMap);
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

                    private Map<String, Node> readSubjectAre(Document doc) {
                        if (doc == null) {
                            return null;
                        }

                        NodeList subjectAreas = doc.getElementsByTagName("Subject_Area");
                        if (subjectAreas == null || subjectAreas.getLength() == 0) {
                            return null;
                        }
                        Map<String, Node> subjectAreaMap = new HashMap<String, Node>();
                        for (int i = 0; i < subjectAreas.getLength(); i++) {
                            Node subject = subjectAreas.item(i);
                            String name =
                                    subject.getAttributes().getNamedItem("Name").getNodeValue();
                            name = name.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
                            subjectAreaMap.put(name, subject);
                        }

                        return subjectAreaMap;
                    }

                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });

        Label label = new Label(comp, SWT.LEFT);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        label.setText(Messages.lblSubjectArea);

        subjectCombo = new Combo(comp, SWT.READ_ONLY);
        {
            GridData data = new GridData();
            data.horizontalSpan = 2;
            data.widthHint = 210;
            subjectCombo.setLayoutData(data);
        }

        setTitle(Messages.titleImportSchemaFromERwin);
        setMessage(Messages.msgImportSchemaFromERwin);
        getShell().setText(Messages.titleImportSchemaFromERwin);

        comp.pack();
        return comp;
    }

    protected void setToCombo(Map<String, Node> subjectMap) {
        if (subjectCombo == null) {
            return;
        }

        subjectCombo.removeAll();
        subjectCombo.add(Messages.msgSubjectAll);

        if (subjectMap != null) {
            subjectCombo.setData(subjectMap);
            Set<String> keys = subjectMap.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                subjectCombo.add(key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void okPressed() {
        String subjectName = subjectCombo.getText();
        if (subjectCombo.getData() != null) {
            Map<String, Node> subjectMap = (Map<String, Node>) subjectCombo.getData();
            Node subjectNode = subjectMap.get(subjectName);
            container.parse(database, doc, subjectNode);
        } else {
            container.parse(database, doc);
        }
        super.okPressed();
    }

    public ERXmlContainer getContainer() {
        return container;
    }
}
