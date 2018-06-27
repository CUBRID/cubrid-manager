/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search
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
package com.cubrid.common.ui.spi.persist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.control.RecentlyUsedSQLComposite;
import com.cubrid.common.ui.query.control.SQLHistoryDetail;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.xml.IXMLMemento;
import com.cubrid.cubridmanager.core.common.xml.XMLMemento;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

public class RecentlyUsedSQLDetailPersistUtils {
	private static final Logger LOGGER = LogUtil.getLogger(RecentlyUsedSQLDetailPersistUtils.class);

	private final static HashMap<String, LinkedList<SQLHistoryDetail>> logs = new HashMap<String, LinkedList<SQLHistoryDetail>>();
	private static final String[] SUPPORTSQL = {"select", "update", "insert", "delete", "with"};
	private static final int MAXSQLCOUNT = 100;
	private static final Object LOCK = new Object();

	public static void clean(CubridDatabase cubridDatabase) {
		if (logs == null) {
			return;
		}

		String id = getId(cubridDatabase);
		logs.remove(id);
	}

	public static void load(CubridDatabase cubridDatabase) {
		String id = getId(cubridDatabase);
		synchronized (LOCK) {
			if (logs.containsKey(id)) {
				return;
			}

			LinkedList<SQLHistoryDetail> sqlHistories = logs.get(id);
			if (sqlHistories == null) {
				sqlHistories = new LinkedList<SQLHistoryDetail>();
				logs.put(id, sqlHistories);

				IXMLMemento memento = PersistUtils.getXMLMemento(RecentlyUsedSQLComposite.ID, id);
				if (memento == null) {
					return;
				}

				try {
					List<SQLHistoryDetail> list = loadFromXML(memento);
					sqlHistories.addAll(list);
				} catch (Exception e) {
					LOGGER.error("parse recently used SQL error", e);
				}
			}
		}
	}

	public static List<SQLHistoryDetail> getLog(CubridDatabase cubridDatabase) {
		if (logs == null) {
			return new LinkedList<SQLHistoryDetail>();
		}

		String id = getId(cubridDatabase);
		synchronized (LOCK) {
			return logs.get(id);
		}
	}

	/**
	 * read SQLHistoryDetail List 
	 *
	 * @param memento IXMLMemento
	 * @return
	 */
	private static List<SQLHistoryDetail> loadFromXML(IXMLMemento memento) throws Exception{
		List<SQLHistoryDetail> recentlyUsedSQLContentsList = new LinkedList<SQLHistoryDetail>();
		try {
			int index = 1;
			for(IXMLMemento xmlDetail : memento.getChildren("SQLHistoryDetail")) {
				SQLHistoryDetail historyDetail = new SQLHistoryDetail();
				historyDetail.setSql(xmlDetail.getString("SQL"));
				historyDetail.setElapseTime(xmlDetail.getString("elapseTime"));
				historyDetail.setExecuteTime(xmlDetail.getString("executeTime"));
				historyDetail.setExecuteInfo(xmlDetail.getString("LOG"));
				historyDetail.setIndex(index++);
				recentlyUsedSQLContentsList.add(historyDetail);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return recentlyUsedSQLContentsList;
	}

	public static void addLog(CubridDatabase cubridDatabase, SQLHistoryDetail sqlHistory) {
		if (cubridDatabase == null || sqlHistory == null) {
			return;
		}

		String id = getId(cubridDatabase);
		synchronized (LOCK) {
			if (!checkSupported(StringUtil.cutAnnotationBeforeSQL(sqlHistory.getSql()))) {
				return;
			}

			LinkedList<SQLHistoryDetail> sqlHistories = logs.get(id);
			if (sqlHistories.size() > MAXSQLCOUNT - 1) {
				sqlHistories.removeLast();
			}

			sqlHistories.addFirst(sqlHistory);
			sqlHistory.setIndex(sqlHistories.getLast().getIndex() + 1);
		}
	}

	public static void save(CubridDatabase cubridDatabase) {
		String id = getId(cubridDatabase);
		synchronized (LOCK) {
			LinkedList<SQLHistoryDetail> sqlHistories = logs.get(id);
			saveToXML(id, sqlHistories);
		}
	}

	/**
	 * save the sql to preference
	 *
	 * @param id
	 * @param recentlyUsedSQLContentList
	 */
	private static void saveToXML(String id, LinkedList<SQLHistoryDetail> sqlHistories) {
		if (sqlHistories == null) {
			return;
		}

		XMLMemento memento = XMLMemento.createWriteRoot("SQLHistoryDetails");
		for (int i = 0, len = sqlHistories.size(); i < len; i++) {
			IXMLMemento subNodes = memento.createChild("SQLHistoryDetail");
			SQLHistoryDetail historyDetail = (SQLHistoryDetail)sqlHistories.get(i);
			subNodes.putString("SQL",historyDetail.getSql());
			subNodes.putString("elapseTime",historyDetail.getElapseTime());
			subNodes.putString("executeTime",historyDetail.getExecuteTime());
			subNodes.putString("LOG",historyDetail.getExecuteInfo());
		}

		PersistUtils.saveXMLMemento(RecentlyUsedSQLComposite.ID, id,  memento);
	}

	public static void remove(CubridDatabase cubridDatabase, List<SQLHistoryDetail> historyToRemove) {
		if (historyToRemove == null || historyToRemove.size() == 0) {
			return;
		}

		String id = getId(cubridDatabase);
		synchronized (LOCK) {
			LinkedList<SQLHistoryDetail> sqlHistories = logs.get(id);
			if (sqlHistories == null) {
				return;
			}

			for (SQLHistoryDetail remove : historyToRemove) {
				sqlHistories.remove(remove);
			}

			saveToXML(id, sqlHistories);
		}
	}

	/**
	 * check whether the sql is a supported statement
	 *
	 * @param sql
	 * @return boolean
	 */
	private static boolean checkSupported(String sql) {
		for (String statement : SUPPORTSQL) {
			if (StringUtil.startsWithIgnoreCase(statement,sql)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * get id by CubridDatabase
	 *
	 * @param database
	 * @return id
	 */
	private static String getId(CubridDatabase database) {
		DatabaseInfo dbInfo = database.getDatabaseInfo();
		if (dbInfo == null) {
			return "";
		}

		String id = dbInfo.getBrokerIP() + ":" +
		dbInfo.getBrokerPort() + ":" + dbInfo.getDbName() ;
		return id;
	}
}
