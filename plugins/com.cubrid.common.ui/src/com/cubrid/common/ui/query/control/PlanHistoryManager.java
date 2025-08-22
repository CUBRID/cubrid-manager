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
package com.cubrid.common.ui.query.control;

import com.cubrid.common.core.queryplan.StructQueryPlan;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.persist.PersistUtils;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;

/**
 * Plan history file manager
 *
 * <p>PlanHistoryManager Description
 *
 * @author pcraft
 * @version 1.0 - 2009. 06. 06 created by pcraft
 */
public final class PlanHistoryManager { // FIXME move to core module

    private static final Logger LOGGER = LogUtil.getLogger(PlanHistoryManager.class);
    public static final String ID = PlanHistoryManager.class.getName();
    public static final int MAX_PLAN_HISTORY_COUNT = 50;
    private static HashMap<String, LinkedList<StructQueryPlan>> planHistoryContent =
            new HashMap<String, LinkedList<StructQueryPlan>>();

    private PlanHistoryManager() {}

    /**
     * open the file
     *
     * @param file File
     * @return sqList
     * @throws IOException if failed
     */
    public static List<StructQueryPlan> openFile(File file) throws IOException {
        String xml = read(file);
        if (xml == null || xml.length() == 0) {
            return null;
        }
        LOGGER.debug(xml);
        List<StructQueryPlan> sqList = StructQueryPlan.unserialize(xml);
        LOGGER.debug("{}", sqList);

        return sqList;
    }

    /**
     * save the file
     *
     * @param file File
     * @param sqList List<StructQueryPlan>
     * @throws IOException if failed
     */
    public static void saveFile(File file, List<StructQueryPlan> sqList) throws IOException {
        write(file, StructQueryPlan.serialize(sqList));
    }

    /**
     * write xml file
     *
     * @param file File
     * @param xml String
     * @return boolean
     * @throws IOException if failed
     */
    public static boolean write(File file, String xml) throws IOException {
        if (file == null) {
            return false;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(xml);

            return true;
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * add recently used SQL list by id
     *
     * @param id IP + PROT + DBNAME
     * @param sql sql;
     */
    public static void addStructQueryPlanListToPreference(String id, StructQueryPlan sq) {
        if (sq == null) {
            return;
        }
        synchronized (planHistoryContent) {
            LinkedList<StructQueryPlan> structQueryPlanList =
                    getStructQueryPlanListFromPreference(id);
            structQueryPlanList.addFirst(sq);
            for (int i = 0, total = structQueryPlanList.size(); i < total; i++) {
                if (i >= MAX_PLAN_HISTORY_COUNT) {
                    structQueryPlanList.remove(i);
                }
            }
        }
    }

    /**
     * add recently used SQL list by id
     *
     * @param id IP + PROT + DBNAME
     * @param sq StructQueryPlan sq
     */
    public static void deleteStructQueryFromPreference(String id, StructQueryPlan sq) {
        if (sq == null) {
            return;
        }
        synchronized (planHistoryContent) {
            LinkedList<StructQueryPlan> structQueryPlanList =
                    getStructQueryPlanListFromPreference(id);
            structQueryPlanList.remove(sq);
            saveStructQueryPlanListToPreference(id, structQueryPlanList);
        }
    }

    /**
     * add recently used SQL list by id
     *
     * @param id IP + PROT + DBNAME
     * @param sq StructQueryPlan sq
     */
    public static void deleteStructQueryFromPreference(
            CubridDatabase database, StructQueryPlan sq) {
        if (sq == null) {
            return;
        }
        deleteStructQueryFromPreference(getId(database), sq);
    }

    /**
     * delete StructQuerys
     *
     * @param id IP + PROT + DBNAME
     * @param sqList StructQueryPlan list
     */
    public static void deleteStructQuerysFromPreference(String id, List<StructQueryPlan> sqList) {
        if (sqList == null || sqList.size() == 0) {
            return;
        }
        synchronized (planHistoryContent) {
            LinkedList<StructQueryPlan> structQueryPlanList =
                    getStructQueryPlanListFromPreference(id);
            for (StructQueryPlan sq : sqList) {
                structQueryPlanList.remove(sq);
            }
            saveStructQueryPlanListToPreference(id, structQueryPlanList);
        }
    }

    /**
     * add recently used SQL list by id
     *
     * @param database CubridDatabase
     * @param sqList StructQueryPlan list
     */
    public static void deleteStructQuerysFromPreference(
            CubridDatabase database, List<StructQueryPlan> sqList) {
        if (sqList == null || sqList.size() == 0) {
            return;
        }
        deleteStructQuerysFromPreference(getId(database), sqList);
    }

    /**
     * save Struct Query Plan List To Preference
     *
     * @param sqList
     */
    public static void saveStructQueryPlanListToPreference(
            String id, List<StructQueryPlan> sqList) {
        PersistUtils.setPreferenceValue(
                PlanHistoryManager.ID, id, StructQueryPlan.serialize(sqList));
    }

    /**
     * addRecentlyUsedSQLContentsById
     *
     * @param database
     * @param sql
     */
    public static void addStructQueryPlanListToPreference(
            CubridDatabase database, StructQueryPlan sq) {
        addStructQueryPlanListToPreference(getId(database), sq);
    }

    /**
     * get Struct Query Plan List From Preference
     *
     * @param id IP + PROT + DBNAME
     * @return SQL list
     */
    public static LinkedList<StructQueryPlan> getStructQueryPlanListFromPreference(String id) {
        synchronized (planHistoryContent) {
            LinkedList<StructQueryPlan> structQueryPlanList = planHistoryContent.get(id);
            if (null == structQueryPlanList) {
                structQueryPlanList = new LinkedList<StructQueryPlan>();
                planHistoryContent.put(id, structQueryPlanList);
                String xml = PersistUtils.getPreferenceValue(PlanHistoryManager.ID, id);
                List<StructQueryPlan> list = StructQueryPlan.unserialize(xml);
                if (list != null) {
                    int count = 1;
                    List<StructQueryPlan> newList = new LinkedList<StructQueryPlan>();
                    for (StructQueryPlan plan : structQueryPlanList) {
                        if (count++ >= MAX_PLAN_HISTORY_COUNT) {
                            break;
                        }
                        newList.add(plan);
                    }
                    structQueryPlanList.addAll(newList);
                }
            }
            return structQueryPlanList;
        }
    }

    /**
     * get Struct Query Plan List From Preference
     *
     * @param id
     * @return
     */
    public static List<StructQueryPlan> getStructQueryPlanListFromPreference(
            CubridDatabase database) {
        if (database.getDatabaseInfo() == null) {
            return null;
        }
        return getStructQueryPlanListFromPreference(getId(database));
    }

    /**
     * read the file
     *
     * @param file File
     * @return file content
     * @throws IOException if failed
     */
    private static String read(File file) throws IOException {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(file));
            StringBuilder buff = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                buff.append(line).append(StringUtil.NEWLINE);
                line = br.readLine();
            }

            return buff.toString();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * get id by CubridDatabase
     *
     * @param database
     * @return id
     */
    public static String getId(CubridDatabase database) {
        DatabaseInfo dbInfo = database.getDatabaseInfo();
        String id = dbInfo.getBrokerIP() + ":" + dbInfo.getBrokerPort() + ":" + dbInfo.getDbName();
        return id;
    }
}
