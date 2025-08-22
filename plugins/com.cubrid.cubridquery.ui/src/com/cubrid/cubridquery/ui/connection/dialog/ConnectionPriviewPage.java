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
package com.cubrid.cubridquery.ui.connection.dialog;

import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.CubridServer;
import com.cubrid.common.ui.spi.model.ICubridNodeLoader;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.user.model.DbUserInfo;
import com.cubrid.cubridmanager.ui.spi.model.loader.CQBDbConnectionLoader;
import com.cubrid.cubridmanager.ui.spi.persist.CQBDBNodePersistManager;
import com.cubrid.cubridquery.ui.connection.Messages;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

/**
 * The Connection Priview Page
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 20, 2012 created by Kevin.Wang
 */
public class ConnectionPriviewPage extends WizardPage implements IPageChangedListener {

    public static final String PAGENAME = "CreateConnectionByUrlWizard/ConnectionPriviewPage";

    private InputUrlPage inputUrlPage;
    private TableViewer priviewViewer;
    private List<CubridDatabase> parsedDatabaseList;

    private List<CubridDatabase> savedDatabaseList =
            new ArrayList<CubridDatabase>(); // both saved and imported

    /** @param pageName */
    protected ConnectionPriviewPage(InputUrlPage inputUrlPage) {
        super(PAGENAME);
        this.inputUrlPage = inputUrlPage;
    }

    public void createControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        composite.setLayout(gridLayout);

        Label infoLabel = new Label(composite, SWT.None);
        infoLabel.setText(Messages.lblDatabaseInfo);
        infoLabel.setLayoutData(CommonUITool.createGridData(1, 1, -1, -1));

        priviewViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        priviewViewer
                .getTable()
                .setLayoutData(CommonUITool.createGridData(GridData.FILL_BOTH, 1, 1, -1, -1));
        priviewViewer.getTable().setLinesVisible(true);
        priviewViewer.getTable().setHeaderVisible(true);

        priviewViewer.setContentProvider(new TableContentProvider());
        priviewViewer.setLabelProvider(new TableLabelProvider());

        final TableColumn connNameColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        connNameColumn.setText(Messages.columnConnectionName);
        connNameColumn.setWidth(120);

        final TableColumn dbNameColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        dbNameColumn.setText(Messages.columnDBName);
        dbNameColumn.setWidth(90);

        final TableColumn dbIPColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        dbIPColumn.setText(Messages.columnDBHost);
        dbIPColumn.setWidth(90);

        final TableColumn dbPortColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        dbPortColumn.setText(Messages.columnDBPort);
        dbPortColumn.setWidth(60);

        final TableColumn userNameColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        userNameColumn.setText(Messages.columnDBUser);
        userNameColumn.setWidth(70);

        final TableColumn passwordColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        passwordColumn.setText(Messages.columnDBPassword);
        passwordColumn.setWidth(70);

        final TableColumn charsetColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        charsetColumn.setText(Messages.columnDBCharset);
        charsetColumn.setWidth(70);

        final TableColumn attrColumn = new TableColumn(priviewViewer.getTable(), SWT.NONE);
        attrColumn.setText(Messages.columnConnectAttr);
        attrColumn.setWidth(160);

        setControl(composite);
        setTitle(Messages.titleConnectionPriviewPage);
    }

    /**
     * Call this method when from a page to a page
     *
     * @param event the page changed event
     */
    public void pageChanged(PageChangedEvent event) {
        IWizardPage page = (IWizardPage) event.getSelectedPage();
        if (page.getName().equals(PAGENAME)) {
            setMessage(null);

            parsedDatabaseList = parseDatabases();
            priviewViewer.setInput(parsedDatabaseList);
            priviewViewer.refresh();

            if (parsedDatabaseList.size() == 0) {
                setErrorMessage(Messages.errNoParseDatabase);
                setPageComplete(false);
            } else {
                setErrorMessage(null);
                setPageComplete(true);
            }

            CommonUITool.packTable(priviewViewer.getTable(), 20, 200);
        }
    }

    /**
     * Parse the databases from the url
     *
     * @return
     */
    private List<CubridDatabase> parseDatabases() {
        savedDatabaseList.clear();
        savedDatabaseList.addAll(CQBDBNodePersistManager.getInstance().getAllDatabase());

        List<CubridDatabase> databaseList = new ArrayList<CubridDatabase>();

        String content = inputUrlPage.getUrls();

        String[] urlArray = content.split("\n|\r");
        for (String url : urlArray) {
            if (url.length() > 0) {
                CubridDatabase database = parseURL(url);
                if (database != null) {
                    if (isContainSameHost(database)) {
                        setMessage(Messages.msgInfoChangeName, IMessageProvider.WARNING);
                        /*Update the name*/
                        updateDatabaseName(database);
                    }
                    savedDatabaseList.add(database);

                    databaseList.add(database);
                }
            }
        }

        return databaseList;
    }

    /**
     * Update the database name
     *
     * @param database
     */
    private void updateDatabaseName(CubridDatabase database) {
        int index = 1;

        String name = database.getName();
        while (isContainSameHost(database)) {
            database.setLabel(name + "_" + "(" + String.valueOf(index) + ")");
            database.setId(
                    database.getLabel() + ICubridNodeLoader.NODE_SEPARATOR + database.getLabel());

            index++;
        }
    }

    /**
     * Judge in contain the same database
     *
     * @param database
     * @return
     */
    private boolean isContainSameHost(CubridDatabase database) {
        for (int i = 0; i < savedDatabaseList.size(); i++) {
            CubridDatabase serv = savedDatabaseList.get(i);
            if (serv.getId().equals(database.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Parse the databases from the url
     *
     * @return
     */
    private CubridDatabase parseURL(String url) { // FIXME extract

        CubridDatabase database = null;

        String mainParams = null;
        String otherParams = null;

        /*Split the parameter by ?*/
        int questionIndex = url.indexOf("?");
        if (questionIndex > 0) {
            mainParams = url.substring(0, questionIndex);
            if (questionIndex + 1 < url.length()) {
                otherParams = url.substring(questionIndex + 1);
            }
        } else {
            mainParams = url;
        }

        String[] colonParams = mainParams.split(":");
        if (colonParams.length >= 5) {
            String host = colonParams[2];
            String port = colonParams[3];
            String dbName = colonParams[4];

            String dbUser = "", dbPassword = "";
            if (colonParams.length >= 6) {
                dbUser = colonParams[5];
            }
            if (colonParams.length >= 7) {
                dbPassword = colonParams[6];
            }

            String charset = null;
            StringBuilder jdbcAttrSB = new StringBuilder();
            /*Parse the connection parameters*/
            if (otherParams != null) {

                /*find the charset parameter*/
                String[] propertyArray = otherParams.split("&");
                for (int i = 0; i < propertyArray.length; i++) {
                    String str = propertyArray[i];

                    if (str.toLowerCase().indexOf("charset") >= 0) {
                        int index = str.indexOf("=");
                        if (index >= 0 && index + 1 < str.length()) {
                            charset = str.substring(index + 1);
                            propertyArray[i] = null;
                        }
                    }
                }
                /*find other parameters*/
                for (int i = 0; i < propertyArray.length; i++) {
                    if (propertyArray[i] != null) {
                        jdbcAttrSB.append(propertyArray[i]);
                    }
                    if (i + 1 < propertyArray.length && propertyArray[i + 1] != null) {
                        jdbcAttrSB.append("&");
                    }
                }
            }

            String connenctionName = host + ":" + port + ":" + dbName + ":" + dbUser;
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setServerName(connenctionName);
            serverInfo.setHostAddress(host);
            serverInfo.setHostMonPort(Integer.parseInt(port));
            serverInfo.setHostJSPort(Integer.parseInt(port) + 1);
            serverInfo.setUserName(dbName + "@" + host);

            DatabaseInfo dbInfo = new DatabaseInfo(dbName, serverInfo);
            dbInfo.setBrokerIP(host);
            dbInfo.setBrokerPort(port);
            if (charset != null) {
                dbInfo.setCharSet(charset);
            }
            dbInfo.setJdbcAttrs(jdbcAttrSB.toString());

            DbUserInfo userInfo = new DbUserInfo();
            userInfo.setDbName(dbName);
            userInfo.setName(dbUser);
            userInfo.setNoEncryptPassword(dbPassword);
            dbInfo.setAuthLoginedDbUserInfo(userInfo);

            CubridServer server = new CubridServer(connenctionName, connenctionName, null, null);
            server.setServerInfo(serverInfo);
            server.setType(NodeType.SERVER);
            String dbId = connenctionName + ICubridNodeLoader.NODE_SEPARATOR + connenctionName;

            database = new CubridDatabase(dbId, dbName);
            database.setDatabaseInfo(dbInfo);
            database.setServer(server);
            database.setStartAndLoginIconPath("icons/navigator/database_start_connected.png");
            database.setStartAndLogoutIconPath("icons/navigator/database_start_disconnected.png");
            database.setAutoSavePassword(true);
            database.setLabel(connenctionName);

            CubridNodeLoader loader = new CQBDbConnectionLoader();
            loader.setLevel(ICubridNodeLoader.FIRST_LEVEL);
            database.setLoader(loader);
        }

        return database;
    }

    public List<CubridDatabase> getParsedConnection() {
        return parsedDatabaseList;
    }
}

/**
 * The table content provider
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 19, 2012 created by Kevin.Wang
 */
class TableContentProvider implements IStructuredContentProvider {

    public void dispose() {}

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    @SuppressWarnings("rawtypes")
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List) inputElement).toArray();
        }
        return new Object[0];
    }
}

/**
 * The Table Label Provider
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 19, 2012 created by Kevin.Wang
 */
class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof CubridDatabase) {
            CubridDatabase database = (CubridDatabase) element;
            switch (columnIndex) {
                case 0:
                    return database.getLabel();
                case 1:
                    return database.getDatabaseInfo().getDbName();
                case 2:
                    return database.getDatabaseInfo().getBrokerIP();
                case 3:
                    return database.getDatabaseInfo().getBrokerPort();
                case 4:
                    return database.getDatabaseInfo().getAuthLoginedDbUserInfo().getName();
                case 5:
                    return database.getDatabaseInfo()
                            .getAuthLoginedDbUserInfo()
                            .getNoEncryptPassword();
                case 6:
                    return database.getDatabaseInfo().getCharSet();
                case 7:
                    return database.getDatabaseInfo().getJdbcAttrs();
            }
        }
        return null;
    }
}
