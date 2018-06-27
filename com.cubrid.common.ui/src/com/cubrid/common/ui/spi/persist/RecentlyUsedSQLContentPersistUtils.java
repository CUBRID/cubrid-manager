package com.cubrid.common.ui.spi.persist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import com.cubrid.common.ui.query.editor.RecentSQLContentAssistProcessor;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;

/**
 * recently used SQL content persist utils
 * @author fulei
 * @version 8.4.1 - 2012-04 modify by fulei
 */
public class RecentlyUsedSQLContentPersistUtils {
	private static final String SEPARATOR = "~-~-~-~-~";
	private static HashMap<String,LinkedList<String>> recentlyUsedSQLContent = new HashMap<String,LinkedList<String>>();
	private static final Logger LOGGER = LogUtil.getLogger(RecentlyUsedSQLContentPersistUtils.class);
	private static final int MAXSQLCOUNT = 30;
	public static final String[] SUPPORTSQL = {"select", "select * from", "update", "insert into", "delete from", "with"};
	
	/**
	 * get recently used SQL list by id 
	 * @param id IP + PROT + DBNAME
	 * @return SQL list
	 */
	public static LinkedList<String> getRecentlyUsedSQLContentsById(String id) {
		LinkedList<String> recentlyUsedSQLContentsList = recentlyUsedSQLContent.get(id);
		if (null == recentlyUsedSQLContentsList) {
			recentlyUsedSQLContentsList = new LinkedList<String>();
			recentlyUsedSQLContent.put(id, recentlyUsedSQLContentsList);
			String recentlyUsedSQLContents = 
				PersistUtils.getPreferenceValue(RecentSQLContentAssistProcessor.ID, id);
			if (recentlyUsedSQLContents == null || "".equals(recentlyUsedSQLContents)) {
				return recentlyUsedSQLContentsList;
			}
			try {
				for (String sql : recentlyUsedSQLContents.split(SEPARATOR)) {
					recentlyUsedSQLContentsList.add(sql);
				}
			} catch (Exception e) {
				LOGGER.error("parse recently used SQL error : ", e);;
			} 
		}
		
		return recentlyUsedSQLContentsList;
	}
	
	/**
	 * get recently used SQL list by CubridDatabase
	 * @param database
	 * @return
	 */
	public static LinkedList<String> getRecentlyUsedSQLContentsById(CubridDatabase database) {
		return getRecentlyUsedSQLContentsById(getId(database));
	}
	/**
	 * add recently used SQL list by id 
	 * @param id IP + PROT + DBNAME
	 * @param sql sql;
	 */
	public static void addRecentlyUsedSQLContentsById(String id, String sql) {
		
		if (id == null || sql == null || "".equals(sql.trim())) {
			return;
		}
		sql = StringUtil.cutAnnotationBeforeSQL(sql);
		if (!checkSupported(sql)) {
			return;
		}
		
		LinkedList<String> recentlyUsedSQLContentList = getRecentlyUsedSQLContentsById(id);
		//check whether the new sql is existed in old sql list
		//if yes ,set it to the latest sql
		boolean repeatFlag = false;
		int repeatIndex = 0;
		for(int i = 0; i < recentlyUsedSQLContentList.size(); i++) {
			String oldSQL = recentlyUsedSQLContentList.get(i);
			if (oldSQL == null) {
				continue;
			}
			if (oldSQL.equals(sql)) {
				repeatFlag = true;
				repeatIndex = i;
				break;
			}
		}
		//if repeat remove it from the old index , otherwise check the max value
		if (repeatFlag) {
			recentlyUsedSQLContentList.remove(repeatIndex);
		} else {
			if (recentlyUsedSQLContentList.size() > MAXSQLCOUNT - 1) {
				recentlyUsedSQLContentList.removeLast();
			}
		}
		recentlyUsedSQLContentList.addFirst(sql);
		saveRecentlyUsedSQLContentsById(id, recentlyUsedSQLContentList);
	}
	
	/**
	 * save the sql to preference
	 * @param id
	 * @param recentlyUsedSQLContentList
	 */
	public static void saveRecentlyUsedSQLContentsById(String id, LinkedList<String> recentlyUsedSQLContentList) {
		StringBuffer savedString = new StringBuffer();
		for (Iterator<String> it = recentlyUsedSQLContentList.iterator();it.hasNext();) {
			Object o = it.next();
			if (o == null) {
				continue;
			}
			String sql = (String)o;
			savedString.append(sql);
			if (it.hasNext()) {
				savedString.append(SEPARATOR);
			}
		}
		PersistUtils.setPreferenceValue(RecentSQLContentAssistProcessor.ID,
				id, savedString.toString());
	}
	/**
	 * addRecentlyUsedSQLContentsById
	 * @param database
	 * @param sql
	 */
	public static void addRecentlyUsedSQLContentsById(CubridDatabase database, String sql) {
		addRecentlyUsedSQLContentsById(getId(database) , sql);
	}
	
	/**
	 * check whether the sql is a supported statement
	 * @param sql
	 * @return boolean
	 */
	public static boolean checkSupported(String sql) {
		for (String statement :SUPPORTSQL) {
			if (StringUtil.startsWithIgnoreCase(statement,sql)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * get id by CubridDatabase
	 * @param database
	 * @return id
	 */
	public static String getId(CubridDatabase database) {
		DatabaseInfo dbInfo = database.getDatabaseInfo();
		if (dbInfo == null) {
			return "";
		}
		String id = dbInfo.getBrokerIP() + ":" +
		dbInfo.getBrokerPort() + ":" + dbInfo.getDbName() ;
		return id;
	}
}
