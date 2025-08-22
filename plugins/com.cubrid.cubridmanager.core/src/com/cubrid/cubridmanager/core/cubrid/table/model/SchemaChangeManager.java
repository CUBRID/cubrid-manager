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
package com.cubrid.cubridmanager.core.cubrid.table.model;

import com.cubrid.common.core.util.StringUtil;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.model.SchemaChangeLog.SchemeInnerType;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema change manager is responsible to manage the change log
 *
 * @author moulinwang
 * @version 1.0 - 2009-6-4 created by moulinwang
 */
public class SchemaChangeManager {

    private List<SchemaChangeLog> changeList = null;
    private DatabaseInfo databaseInfo = null;
    private boolean isNewTableFlag;

    /** a construct method for testing */
    public SchemaChangeManager() {
        // empty
    }

    /**
     * The constructor
     *
     * @param databaseInfo
     * @param isNewTableFlag
     */
    public SchemaChangeManager(DatabaseInfo databaseInfo, boolean isNewTableFlag) {
        super();
        this.changeList = new ArrayList<SchemaChangeLog>();
        this.databaseInfo = databaseInfo;
        this.isNewTableFlag = isNewTableFlag;
    }

    /**
     * Get the changeList
     *
     * @return List<SchemeChangeLog> the changeList
     */
    public List<SchemaChangeLog> getChangeList() {
        if (null == changeList) {
            return new ArrayList<SchemaChangeLog>();
        }
        return changeList;
    }

    public void setChangeList(List<SchemaChangeLog> changeList) {
        this.changeList = changeList;
    }

    /**
     * Return whether object with a type and a given value is a new added object
     *
     * @param type SchemeInnerType the reference of SchemeInnerType
     * @param value String The given value
     * @return boolean true if the type and value is a new added object,false otherwise
     */
    public boolean isNewAdded(SchemeInnerType type, String value) {
        SchemaChangeLog slog = findModifySchemeChangeLog(type, value, false);
        if (slog == null) {
            return false;
        } else {
            return slog.getOldValue() == null ? true : false;
        }
    }

    /**
     * return whether an attribute(with class or instance type) is a new added object
     *
     * @param attrName String the attribute name
     * @param isClassAttr whether is class attribute
     * @return boolean true if the added is new , false otherwise
     */
    public boolean isNewAdded(String attrName, boolean isClassAttr) {
        if (isClassAttr) {
            return isNewAdded(SchemeInnerType.TYPE_CLASSATTRIBUTE, attrName);
        } else {
            return isNewAdded(SchemeInnerType.TYPE_ATTRIBUTE, attrName);
        }
    }

    /**
     * return all changes about index of a schema
     *
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getIndexChangeLogs() {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        list.addAll(getChangeLogs(SchemeInnerType.TYPE_INDEX));
        return list;
    }

    /**
     * return all changes about FK of a schema
     *
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getFKChangeLogs() {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        list.addAll(getChangeLogs(SchemeInnerType.TYPE_FK));
        return list;
    }

    /**
     * return all changes about attribute of a schema
     *
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getAttrChangeLogs() {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        list.addAll(getChangeLogs(SchemeInnerType.TYPE_ATTRIBUTE));
        return list;
    }

    /**
     * return all position change logs
     *
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getPositionChangeLogs() {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        list.addAll(getChangeLogs(SchemeInnerType.TYPE_POSITION));
        return list;
    }

    /**
     * Return all changes about attribute of a schema
     *
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getClassAttrChangeLogs() {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        list.addAll(getChangeLogs(SchemeInnerType.TYPE_CLASSATTRIBUTE));
        return list;
    }

    /**
     * return all changes about a given type of a schema
     *
     * @param type SchemeInnerType the given reference of SchemeInnerType
     * @return List<SchemeChangeLog> a list includes the reference of SchemeChanageLog
     */
    public List<SchemaChangeLog> getChangeLogs(SchemeInnerType type) {
        List<SchemaChangeLog> list = new ArrayList<SchemaChangeLog>();
        List<SchemaChangeLog> changeLogList = getChangeList();
        for (SchemaChangeLog log : changeLogList) {
            if (log.getType() == type) {
                list.add(log);
            }
        }
        return list;
    }

    /**
     * find object in change list with a given type and a given value to new value or old value
     *
     * @param type SchemeInnerType the given scheme inner type
     * @param value String the given value
     * @param isOld boolean whether is old
     * @return SchemeChangeLog the object of SchemeChangeLog
     */
    private SchemaChangeLog findModifySchemeChangeLog(
            SchemeInnerType type, String value, boolean isOld) {
        if (value == null) {
            return null;
        }
        List<SchemaChangeLog> changeLogList = getChangeList();
        for (SchemaChangeLog log : changeLogList) {
            if (log.getType() == type) {
                String queryValue = (isOld ? log.getOldValue() : log.getNewValue());
                if (value.equals(queryValue)) {
                    return log;
                }
            }
        }
        return null;
    }

    /**
     * add a change log to change list
     * <li>1-2, 2-3 -->1-3
     *
     * @param log SchemeChangeLog the reference of SchemeChangeLog
     */
    public void addSchemeChangeLog(SchemaChangeLog log) {
        if (!isNewTableFlag) {
            SchemaChangeLog slog =
                    findModifySchemeChangeLog(log.getType(), log.getOldValue(), false);
            if (slog != null) {
                changeList.remove(slog);
                log.setOldValue(slog.getOldValue());
            }
            if (log.getOldValue() != null || log.getNewValue() != null) {
                changeList.add(log);
            }

            /*Update the change position log value*/
            if (!StringUtil.isEqualNotIgnoreNull(log.getOldValue(), log.getNewValue())) {
                for (SchemaChangeLog posLog : changeList) {
                    if (SchemeInnerType.TYPE_POSITION.equals(posLog.getType())
                            && StringUtil.isEqualNotIgnoreNull(
                                    posLog.getOldValue(), log.getOldValue())) {
                        posLog.setOldValue(log.getNewValue());
                        posLog.setNewValue(log.getNewValue());
                    }
                }
            }

            /*Delete the removed column log*/
            if (log.getNewValue() == null) {
                List<SchemaChangeLog> removedList = new ArrayList<SchemaChangeLog>();
                for (SchemaChangeLog posLog : changeList) {
                    if (SchemeInnerType.TYPE_POSITION.equals(posLog.getType())
                            && StringUtil.isEqualNotIgnoreNull(
                                    posLog.getOldValue(), log.getOldValue())) {
                        removedList.add(posLog);
                    }
                }
                changeList.removeAll(removedList);
            }
        }
    }

    public boolean isNewTableFlag() {
        return isNewTableFlag;
    }

    public void setNewTableFlag(boolean isNewTableFlag) {
        this.isNewTableFlag = isNewTableFlag;
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }
}
