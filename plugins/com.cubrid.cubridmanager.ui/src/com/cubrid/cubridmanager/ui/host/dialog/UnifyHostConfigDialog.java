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
package com.cubrid.cubridmanager.ui.host.dialog;

import com.cubrid.common.ui.spi.dialog.CMTitleAreaDialog;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.ui.host.Messages;
import com.cubrid.cubridmanager.ui.host.editor.UnifyHostConfigEditorInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * before open unify host editor select edit type on this dialog
 *
 * @author fulei
 * @version 1.0 - 2013-1-30 created by fulei
 */
public class UnifyHostConfigDialog extends CMTitleAreaDialog {

    private Button editCubridConfBtn;
    private Button editBrokerConfBtn;
    private Button editCMConfBtn;
    private Button editHAConfBtn;
    private Button editACLConfBtn;
    private TableViewer hostTableViewer;
    private Composite tableComposite;

    private CubridServer[] cubridServers;
    private UnifyHostConfigEditorInput editorInput = null;

    private UnifyHostConfigDialogDNDController dialogDNDController;

    public UnifyHostConfigDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE | SWT.MAX);
    }

    public UnifyHostConfigDialog(Shell parentShell, CubridServer[] cubridServers) {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.RESIZE | SWT.MAX);
        this.cubridServers = cubridServers;
    }

    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.unifyHostConfigDialogTitle);
        setMessage(Messages.unifyHostConfigDialogMessage, IMessageProvider.INFORMATION);

        Composite parentComp = (Composite) super.createDialogArea(parent);
        Composite btnComposite = new Composite(parentComp, SWT.NONE);
        {
            btnComposite.setLayoutData(new GridData());
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            btnComposite.setLayout(layout);
        }
        editCubridConfBtn = new Button(btnComposite, SWT.CHECK);
        editCubridConfBtn.setText(
                com.cubrid.cubridmanager.ui.spi.Messages.editCubridConf + "(cubrid.conf)");
        editCubridConfBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        validate();
                    }
                });

        editBrokerConfBtn = new Button(btnComposite, SWT.CHECK);
        editBrokerConfBtn.setText(
                com.cubrid.cubridmanager.ui.spi.Messages.editBrokerConf + "(cubrid_broker.conf)");
        editBrokerConfBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        validate();
                    }
                });

        editCMConfBtn = new Button(btnComposite, SWT.CHECK);
        editCMConfBtn.setText(com.cubrid.cubridmanager.ui.spi.Messages.editCmConf + "(cm.conf)");
        editCMConfBtn.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        validate();
                    }
                });

        //		editHAConfBtn = new Button(btnComposite, SWT.CHECK);
        //		editHAConfBtn.setText(com.cubrid.cubridmanager.ui.spi.Messages.editHaConf+
        // "(cubrid_ha.conf)");
        //		editHAConfBtn.addSelectionListener(new SelectionAdapter() {
        //			public void widgetSelected(SelectionEvent event) {
        //				validate();
        //			}
        //		});
        //
        //
        //		editACLConfBtn = new Button(btnComposite, SWT.CHECK);
        //		editACLConfBtn.setText(Messages.unifyHostConfigDialogACLConfBtnLabel);
        //		editACLConfBtn.addSelectionListener(new SelectionAdapter() {
        //			public void widgetSelected(SelectionEvent event) {
        //				validate();
        //			}
        //		});

        tableComposite = new Composite(parentComp, SWT.NONE);
        {
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.horizontalSpan = 1;
            gd.heightHint = 80;
            tableComposite.setLayoutData(gd);
            GridLayout layout = new GridLayout();
            layout.numColumns = 1;
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing =
                    convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            tableComposite.setLayout(layout);
        }

        hostTableViewer =
                new TableViewer(
                        tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL);
        hostTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        hostTableViewer.getTable().setLinesVisible(true);
        hostTableViewer.getTable().setHeaderVisible(false);

        final TableViewerColumn databaseCol = new TableViewerColumn(hostTableViewer, SWT.NONE);
        databaseCol.getColumn().setWidth(500);
        databaseCol.getColumn().setText("");

        hostTableViewer.setContentProvider(new HostTableContentProvider());
        hostTableViewer.setLabelProvider(new HostTableLabelProvider());
        registerContextMenu();
        setInput();
        //		setAllChecked();
        return parent;
    }

    public void setAllChecked() {
        editCubridConfBtn.setSelection(false);
        editBrokerConfBtn.setSelection(false);
        editCMConfBtn.setSelection(false);
        //		editHAConfBtn.setSelection(true);
        //		editACLConfBtn.setSelection(true);
    }
    /** register context menu */
    private void registerContextMenu() {

        MenuManager menuManager = new MenuManager();
        menuManager.setRemoveAllWhenShown(true);

        Menu contextMenu = menuManager.createContextMenu(hostTableViewer.getTable());
        hostTableViewer.getTable().setMenu(contextMenu);

        Menu menu = new Menu(getShell(), SWT.POP_UP);
        final MenuItem itemDelete = new MenuItem(menu, SWT.PUSH);
        itemDelete.setText("delete");
        itemDelete.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        List<CubridServer> deleteCubridServer = new ArrayList<CubridServer>();
                        for (int i = 0;
                                i < hostTableViewer.getTable().getSelectionIndices().length;
                                i++) {
                            int index = hostTableViewer.getTable().getSelectionIndices()[i];
                            deleteCubridServer.add(cubridServers[index]);
                        }

                        List<CubridServer> hostList = Arrays.asList(cubridServers);

                        ArrayList<CubridServer> newServerList = new ArrayList<CubridServer>();
                        for (CubridServer cubridServer : hostList) {
                            if (deleteCubridServer.contains(cubridServer)) {
                                continue;
                            }
                            newServerList.add(cubridServer);
                        }
                        cubridServers =
                                newServerList.toArray(new CubridServer[newServerList.size()]);
                        setInput();
                        validate();
                    }
                });

        hostTableViewer.getTable().setMenu(menu);

        dialogDNDController = new UnifyHostConfigDialogDNDController(this);
        dialogDNDController.registerDropTarget();
    }
    /** set data to table viewer */
    public void setInput() {
        hostTableViewer.setInput(cubridServers);
        hostTableViewer.refresh();
    }

    /**
     * add
     *
     * @param selectedNodes
     */
    public void addHost(Object[] selectedNodes) {
        List<CubridServer> serverList = new ArrayList<CubridServer>();
        for (CubridServer server : cubridServers) {
            serverList.add(server);
        }

        for (Object o : selectedNodes) {
            if (!(o instanceof CubridServer)) {
                continue;
            }
            CubridServer cubridServer = (CubridServer) o;
            if (cubridServer != null
                    && !serverList.contains(cubridServer)
                    && cubridServer.isConnected()) {
                serverList.add(cubridServer);
            }
        }
        cubridServers = serverList.toArray(new CubridServer[serverList.size()]);
        setInput();
    }

    /**
     * When press button,call it
     *
     * @param buttonId the button id
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            for (CubridServer server : cubridServers) {
                if (!server.isConnected()) {
                    CommonUITool.openErrorBox(
                            Messages.bind(Messages.unifyHostConfigDialogErrMsg2, server.getName()));
                    return;
                }
            }
            editorInput = new UnifyHostConfigEditorInput(cubridServers);
            editorInput.setEditCubridConf(editCubridConfBtn.getSelection());
            editorInput.setEditBrokerConf(editBrokerConfBtn.getSelection());
            editorInput.setEditCMConf(editCMConfBtn.getSelection());
            //			editorInput.setEditHAConf(editHAConfBtn.getSelection());
            //			editorInput.setEditACLConf(editACLConfBtn.getSelection());
        }

        setReturnCode(buttonId);
        close();
    }

    /**
     * DatabaseTableContentProvider
     *
     * @author fulei
     */
    static class HostTableContentProvider implements IStructuredContentProvider {
        public void dispose() {}

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

        @SuppressWarnings("all")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof CubridServer[]) {
                CubridServer[] cubridServers = (CubridServer[]) inputElement;
                return cubridServers;
            }
            return new Object[] {};
        }
    }

    /**
     * DatabaseTableLabelProvider
     *
     * @author fulei
     */
    static class HostTableLabelProvider extends LabelProvider implements ITableLabelProvider {
        /**
         * Default return null
         *
         * @param element to be display.
         * @param columnIndex is the index of column. Begin with 0.
         * @return null
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * Retrieves the column's text by column index
         *
         * @param element to be displayed.
         * @param columnIndex is the index of column. Begin with 0.
         * @return String to be filled in the column.
         */
        public String getColumnText(Object element, int columnIndex) {
            CubridServer server = (CubridServer) element;
            if (server == null) {
                return "";
            }

            if (columnIndex != 0) {
                return null;
            }

            return server.getName();
        }
    }

    public Composite getTableComposite() {
        return tableComposite;
    }

    /**
     * Create buttons for button bar
     *
     * @param parent the parent composite
     */
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /** validate whether can click ok button */
    public void validate() {
        if ((editCubridConfBtn.getSelection() || editBrokerConfBtn.getSelection())
                || editCMConfBtn.getSelection()
                        //				|| editHAConfBtn.getSelection()
                        //				|| editACLConfBtn.getSelection())
                        && cubridServers.length > 0) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            setErrorMessage(null);
        } else {
            setErrorMessage(Messages.unifyHostConfigDialogErrMsg);
            getButton(IDialogConstants.OK_ID).setEnabled(false);
        }
    }

    public UnifyHostConfigEditorInput getEditorInput() {
        return editorInput;
    }
}
