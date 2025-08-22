/*
 * Copyright (C) 2010 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.spi.persist;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.DateUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.tuner.QueryRecord;
import com.cubrid.common.ui.query.tuner.QueryRecordProject;
import com.cubrid.common.ui.spi.model.RestorableQueryEditorInfo;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;

/**
 * Application Persist Util
 *
 * @author Kevin.Wang
 * @version 1.0 - Jun 4, 2012 created by Kevin.Wang
 */
public class ApplicationPersistUtil {
    private static final Logger LOGGER = LogUtil.getLogger(ApplicationPersistUtil.class);
    private static final String fileName = "application.xml";

    private static final String DATE_PATTERN = "yyyy-MM-dd a hh:mm:ss";
    private final String path;
    private XMLMemento root;

    private final List<ArrayList<RestorableQueryEditorInfo>> editorStatusList =
            new ArrayList<ArrayList<RestorableQueryEditorInfo>>();
    private final List<ArrayList<RestorableQueryEditorInfo>> editorStatusListAtLastSession =
            new ArrayList<ArrayList<RestorableQueryEditorInfo>>();
    private final Map<String, List<QueryRecordProject>> queryRecordMap =
            new LinkedHashMap<String, List<QueryRecordProject>>();

    private static ApplicationPersistUtil instance;

    public static ApplicationPersistUtil getInstance() {
        synchronized (ApplicationPersistUtil.class) {
            if (instance == null) {
                instance = new ApplicationPersistUtil();
            }
        }
        return instance;
    }

    private ApplicationPersistUtil() {
        this.path = Platform.getInstanceLocation().getURL().getPath() + File.separator + fileName;
        File file = new File(path);
        if (!file.exists()) {
            createTemplateFile(file);
        }

        load();
    }

    private void createTemplateFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            LOGGER.error(getClass().getName(), e);
        }
        root = XMLMemento.createWriteRoot("application_data");
        save();
    }

    private void load() {
        editorStatusList.clear();
        editorStatusListAtLastSession.clear();
        queryRecordMap.clear();

        try {
            root = (XMLMemento) XMLMemento.loadMemento(path);
        } catch (IOException e) {
            LOGGER.error(getClass().getName(), e);
        }

        IXMLMemento[] children = root == null ? null : root.getChildren("editor_status");
        for (int i = 0; children != null && i < children.length; i++) {
            IXMLMemento child = children[i];
            editorStatusListAtLastSession.add(loadSQLTabItem(child));
        }

        IXMLMemento[] queryRecordChildren =
                root == null ? null : root.getChildren("query_record_list_data");
        for (int i = 0; queryRecordChildren != null && i < queryRecordChildren.length; i++) {
            IXMLMemento child = queryRecordChildren[i];
            loadQueryRecordList(child);
        }
    }

    /**
     * loadSQLTabItem if has sql_tabItem node ,is new version old version not have this node
     *
     * @param element
     * @return
     */
    private ArrayList<RestorableQueryEditorInfo> loadSQLTabItem(IXMLMemento element) {
        IXMLMemento[] sql_tabItemArray = element.getChildren("sql_tabItem");
        ArrayList<RestorableQueryEditorInfo> sql_tabItemList =
                new ArrayList<RestorableQueryEditorInfo>();
        if (sql_tabItemArray != null && sql_tabItemArray.length > 0) {
            for (int i = 0; sql_tabItemArray != null && i < sql_tabItemArray.length; i++) {
                IXMLMemento child = sql_tabItemArray[i];
                sql_tabItemList.add(loadRestorableQueryEditorInfo(child));
            }
        } else {
            if (element.getString("database") != null) {
                sql_tabItemList.add(loadRestorableQueryEditorInfo(element));
            }
        }
        return sql_tabItemList;
    }

    /**
     * Load query record list
     *
     * @param element
     */
    private void loadQueryRecordList(IXMLMemento element) {
        DateFormat formater = DateUtil.getDateFormat(DATE_PATTERN, Locale.ENGLISH);
        IXMLMemento[] dataArray = element.getChildren("query_redord_list");
        if (dataArray != null && dataArray.length > 0) {
            for (int i = 0; i < dataArray.length; i++) {
                IXMLMemento child = dataArray[i];
                String key = child.getString("database_key");
                String dateStr = child.getString("create_date");
                String name = child.getString("name");

                QueryRecordProject recordList = new QueryRecordProject();
                recordList.setDatabaseKey(key);
                recordList.setName(name);
                try {
                    recordList.setCreateDate(formater.parse(dateStr));
                } catch (ParseException e) {
                    recordList.setCreateDate(new Date());
                }

                IXMLMemento[] queryRecordArray = child.getChildren("query_record");
                if (queryRecordArray != null && queryRecordArray.length > 0) {
                    for (int j = 0; j < queryRecordArray.length; j++) {
                        IXMLMemento memen = queryRecordArray[j];
                        QueryRecord queryRecord = loadQueryRecord(memen);

                        recordList.addQueryRecord(queryRecord);
                    }
                }
                List<QueryRecordProject> list = queryRecordMap.get(key);
                if (list == null) {
                    list = new ArrayList<QueryRecordProject>();
                    queryRecordMap.put(key, list);
                }
                list.add(recordList);
            }
        }
    }

    /**
     * Load query record
     *
     * @param element
     * @return
     */
    private QueryRecord loadQueryRecord(IXMLMemento element) {
        DateFormat formater = DateUtil.getDateFormat(DATE_PATTERN, Locale.ENGLISH);
        QueryRecord queryRecord = null;
        String name = element.getString("name");
        String dateStr = element.getString("create_date");
        long startTime = StringUtil.intValue(element.getString("start_time"), -1);
        long stopTime = StringUtil.intValue(element.getString("stop_time"), -1);
        String query = element.getString("query");

        StructQueryPlan queryPlan = null;
        IXMLMemento[] planArray = element.getChildren("query_plan");
        if (planArray != null && planArray.length > 0) {
            queryPlan = loadQueryPlan(planArray[0]);
        }
        LinkedHashMap<String, String> statistics = null;
        IXMLMemento[] statisticsArray = element.getChildren("statistics");
        if (statisticsArray != null && statisticsArray.length > 0) {
            statistics = loadPlanStatistics(statisticsArray[0]);
        }

        Date createDate = null;
        try {
            createDate = formater.parse(dateStr);
        } catch (ParseException e) {
            createDate = new Date();
        }

        queryRecord = new QueryRecord(query, startTime, stopTime, createDate);
        queryRecord.setName(name);
        queryRecord.setQueryPlan(queryPlan);
        queryRecord.setStatistics(statistics);

        return queryRecord;
    }

    /**
     * Load query plan
     *
     * @param element
     * @return
     */
    private StructQueryPlan loadQueryPlan(IXMLMemento element) {
        DateFormat formater = DateUtil.getDateFormat(DATE_PATTERN, Locale.ENGLISH);
        String query = element.getString("query");
        String dateStr = element.getString("create_date");
        String plan = element.getString("plan");
        Date date = null;
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
            date = new Date();
        }

        return new StructQueryPlan(query, plan, date);
    }

    /**
     * Load plan statistics
     *
     * @param element
     * @return
     */
    private LinkedHashMap<String, String> loadPlanStatistics(IXMLMemento element) {
        LinkedHashMap<String, String> statistics = null;

        if (element != null) {
            statistics = new LinkedHashMap<String, String>();
            List<String> keyList = element.getAttributeNames();
            if (keyList != null && keyList.size() > 0) {
                for (String key : keyList) {
                    String value = element.getString(key);

                    statistics.put(key, value);
                }
            }
        }

        return statistics;
    }

    /**
     * load RestorableQueryEditorInfo from IXMLMemento
     *
     * @param element
     * @return
     */
    public RestorableQueryEditorInfo loadRestorableQueryEditorInfo(IXMLMemento element) {
        DateFormat formater = DateUtil.getDateFormat(DATE_PATTERN, Locale.ENGLISH);
        RestorableQueryEditorInfo status = new RestorableQueryEditorInfo();
        status.setServerName(element.getString("server"));
        status.setDatabaseName(element.getString("database"));
        try {
            status.setCreatedTime(formater.parse(element.getString("create_time")));
        } catch (Exception e) {
            LOGGER.error("Parse the create time failed:" + e.getMessage(), e);
        }
        status.setQueryContents(element.getString("content"));
        return status;
    }

    public void save() {
        root = XMLMemento.createWriteRoot("application_data");
        saveToXmlFile(root);

        try {
            root.saveToFile(path);
        } catch (IOException e) {
            LOGGER.error(getClass().getName(), e);
        }
    }

    public List<QueryRecordProject> getQueryRecordProject(DatabaseInfo databaseInfo) {
        List<QueryRecordProject> list = queryRecordMap.get(getDBMapKey(databaseInfo));
        if (list == null) {
            list = new ArrayList<QueryRecordProject>();
            queryRecordMap.put(getDBMapKey(databaseInfo), list);
        }

        List<QueryRecordProject> result = new ArrayList<QueryRecordProject>();
        for (QueryRecordProject project : list) {
            result.add(project.clone());
        }

        return result;
    }

    public void addQueryRecordProject(
            DatabaseInfo databaseInfo, QueryRecordProject queryRecordProject) {
        String key = getDBMapKey(databaseInfo);
        List<QueryRecordProject> list = queryRecordMap.get(key);
        if (list == null) {
            list = new ArrayList<QueryRecordProject>();
            queryRecordMap.put(key, list);
        }

        list.add(queryRecordProject.clone());
    }

    public void removeQueryRecordProject(DatabaseInfo databaseInfo, String projectName) {
        int index = -1;
        List<QueryRecordProject> list = queryRecordMap.get(getDBMapKey(databaseInfo));

        for (int i = 0; i < list.size(); i++) {
            QueryRecordProject queryRecordProject = list.get(i);
            if (StringUtil.isEqual(projectName, queryRecordProject.getName())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            list.remove(index);
        }
    }

    /**
     * Find query record project by name
     *
     * @param projectName
     * @return
     */
    public QueryRecordProject findQueryRecordProject(
            DatabaseInfo databaseInfo, String projectName) {
        List<QueryRecordProject> list = getQueryRecordProject(databaseInfo);

        if (list != null && list.size() > 0) {
            for (QueryRecordProject temp : list) {
                if (StringUtil.isEqual(projectName, temp.getName())) {
                    return temp;
                }
            }
        }

        return null;
    }

    private void saveToXmlFile(IXMLMemento parent) {
        if (editorStatusList == null) {
            return;
        }
        DateFormat formater = DateUtil.getDateFormat(DATE_PATTERN, Locale.ENGLISH);
        for (List<RestorableQueryEditorInfo> statusList : editorStatusList) {
            IXMLMemento memento = parent.createChild("editor_status");
            for (RestorableQueryEditorInfo status : statusList) {
                IXMLMemento tabItem = memento.createChild("sql_tabItem");
                tabItem.putString("content", status.getQueryContents());
                tabItem.putString("create_time", formater.format(status.getCreatedTime()));
                tabItem.putString(
                        "database",
                        status.getDatabaseName() == null ? "" : status.getDatabaseName());
                tabItem.putString(
                        "server", status.getServerName() == null ? "" : status.getServerName());
            }
        }

        IXMLMemento queryListDataMemento = parent.createChild("query_record_list_data");
        for (Entry<String, List<QueryRecordProject>> entry : queryRecordMap.entrySet()) {
            String key = entry.getKey();
            List<QueryRecordProject> list = entry.getValue();
            for (QueryRecordProject queryRecordList : list) {
                IXMLMemento queryListMemento =
                        queryListDataMemento.createChild("query_redord_list");
                String createDate = formater.format(queryRecordList.getCreateDate());

                queryListMemento.putString("database_key", key);
                queryListMemento.putString("create_date", createDate);
                queryListMemento.putString("name", queryRecordList.getName());

                for (QueryRecord queryRecord : queryRecordList.getQueryRecordList()) {
                    IXMLMemento queryRecordMemento = queryListMemento.createChild("query_record");
                    queryRecordMemento.putString("name", queryRecord.getName());
                    queryRecordMemento.putString(
                            "create_date", formater.format(queryRecord.getCreateDate()));
                    queryRecordMemento.putString(
                            "start_time", String.valueOf(queryRecord.getStartTime()));
                    queryRecordMemento.putString(
                            "stop_time", String.valueOf(queryRecord.getStopTime()));
                    queryRecordMemento.putString("query", queryRecord.getQuery());

                    if (queryRecord.getQueryPlan() != null) {
                        StructQueryPlan queryPlan = queryRecord.getQueryPlan();
                        IXMLMemento queryPlanMemento = queryRecordMemento.createChild("query_plan");
                        queryPlanMemento.putString("query", queryPlan.getSql());
                        queryPlanMemento.putString("plan", queryPlan.getPlanRaw());
                        queryPlanMemento.putString(
                                "create_date", formater.format(queryPlan.getCreated()));
                    }

                    if (queryRecord.getStatistics() != null) {
                        IXMLMemento statisticsMemento =
                                queryRecordMemento.createChild("statistics");
                        for (Entry<String, String> prop : queryRecord.getStatistics().entrySet()) {
                            statisticsMemento.putString(prop.getKey(), prop.getValue());
                        }
                    }
                }
            }
        }
    }

    public List<ArrayList<RestorableQueryEditorInfo>> getEditorStatusList() {
        return editorStatusList;
    }

    public void addEditorStatus(ArrayList<RestorableQueryEditorInfo> editorStatusList) {
        getEditorStatusList().add(editorStatusList);
    }

    public void clearAllEditorStatus() {
        editorStatusList.clear();
    }

    public void clearRestorableQueryEditors() {
        editorStatusListAtLastSession.clear();
    }

    public int countOfRestorableQueryEditorsAtLastSession() {
        return editorStatusListAtLastSession.size();
    }

    public List<ArrayList<RestorableQueryEditorInfo>> getEditorStatusListAtLastSession() {
        return editorStatusListAtLastSession;
    }

    /**
     * Get the map key for the database
     *
     * @param database - CubridDatabase
     * @return String
     */
    private String getDBMapKey(DatabaseInfo databaseInfo) {
        String dbUser = "";
        String dbName = "";
        String address = "";
        String port = "";
        String serverName = "";

        if (databaseInfo.getAuthLoginedDbUserInfo() != null) {
            dbUser = databaseInfo.getAuthLoginedDbUserInfo().getName();
        }

        if (databaseInfo.getDbName() != null) {
            dbName = databaseInfo.getDbName();
        }

        if (databaseInfo.getBrokerIP() != null) {
            address = databaseInfo.getBrokerIP();
        }
        if (databaseInfo.getBrokerIP() != null) {
            port = databaseInfo.getBrokerIP();
        }

        if (databaseInfo.getServerInfo() != null
                && databaseInfo.getServerInfo().getServerName() != null) {
            serverName = databaseInfo.getServerInfo().getServerName();
        }

        return getDBMapKey(dbUser, dbName, address, port, serverName);
    }

    /**
     * Get the map key for the database
     *
     * @param dbUser - String
     * @param dbName - String
     * @param address - String
     * @param port - String
     * @param serverName - String
     * @return String
     */
    private String getDBMapKey(
            String dbUser, String dbName, String address, String port, String serverName) {
        StringBuffer sb = new StringBuffer();
        sb.append(dbUser);
        sb.append("@");
        sb.append(dbName);
        sb.append("@");
        sb.append(address);
        sb.append("@");
        sb.append(port);
        if (serverName != null) {
            sb.append("@");
            sb.append(serverName);
        }

        return sb.toString();
    }
}
