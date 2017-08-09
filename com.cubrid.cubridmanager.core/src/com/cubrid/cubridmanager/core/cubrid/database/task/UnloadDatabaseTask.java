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
package com.cubrid.cubridmanager.core.cubrid.database.task;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cubrid.cubridmanager.core.common.model.ServerInfo;
import com.cubrid.cubridmanager.core.common.socket.SocketTask;
import com.cubrid.cubridmanager.core.common.socket.TreeNode;

/**
 * 
 * This task is responsible to unload database
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class UnloadDatabaseTask extends
		SocketTask {

	private static final String[] SEND_MSG_ITEMS = new String[] { "task", "token", "dbname",
			"dbuser", "dbpasswd", "targetdir", "usehash", "hashdir", "target", "open",
			"classname", "close", "ref", "classonly", "delimit", "estimate", "prefix", "cach",
			"lofile" };

	/**
	 * The constructor
	 * 
	 * @param serverInfo
	 */
	public UnloadDatabaseTask(ServerInfo serverInfo) {
		super("unloaddb", serverInfo, SEND_MSG_ITEMS);
	}

	/**
	 * The constructor
	 * 
	 * @param serverInfo
	 * @param charset
	 */
	public UnloadDatabaseTask(ServerInfo serverInfo, String charset) {
		super("unloaddb", serverInfo, SEND_MSG_ITEMS, charset, charset);
	}

	/**
	 * Set the database name
	 * 
	 * @param dbName String the database name
	 */
	public void setDbName(String dbName) {
		super.setMsgItem("dbname", dbName);
	}

	/**
	 * Set the user name
	 * 
	 * @param userName String the user name
	 */
	public void setUserName(String userName) {
		super.setMsgItem("dbuser", userName);
	}

	/**
	 * Set the password
	 * 
	 * @param password String the password
	 */
	public void setPassword(String password) {
		super.setMsgItem("dbpasswd", password);
	}

	/**
	 * 
	 * Set the unloaded dir
	 * 
	 * @param dir String the unload directory
	 */
	public void setUnloadDir(String dir) {
		super.setMsgItem("targetdir", dir);
	}

	/**
	 * 
	 * Set hash file
	 * 
	 * @param isUsed boolean whether is used
	 * @param hashDir String the hash directory
	 */
	public void setUsedHash(boolean isUsed, String hashDir) {
		if (isUsed) {
			super.setMsgItem("usehash", "yes");
			super.setMsgItem("hashdir", hashDir);
		} else {
			super.setMsgItem("usehash", "no");
			super.setMsgItem("hashdir", "none");
		}
	}

	/**
	 * 
	 * Set unload type,schema or data or both
	 * 
	 * @param type String the type of unload
	 */
	public void setUnloadType(String type) {
		if (!"both".equals(type) && !"schema".equals(type)
				&& !"object".equals(type)) {
			return;
		}
		super.setMsgItem("target", type);
	}

	/**
	 * 
	 * Set unloaded classes
	 * 
	 * @param classes String[] the classes
	 */
	public void setClasses(String[] classes) {
		super.setMsgItem("classname", classes);
	}

	/**
	 * 
	 * Set included ref
	 * 
	 * @param isIncluded boolean whether is included
	 */
	public void setIncludeRef(boolean isIncluded) {
		if (isIncluded) {
			super.setMsgItem("ref", "yes");
		} else {
			super.setMsgItem("ref", "no");
		}
	}

	/**
	 * 
	 * Set whether class only
	 * 
	 * @param isOnly boolean whether is only
	 */
	public void setClassOnly(boolean isOnly) {
		if (isOnly) {
			super.setMsgItem("classonly", "yes");
		} else {
			super.setMsgItem("classonly", "no");
		}
	}

	/**
	 * 
	 * Set whether use delimit
	 * 
	 * @param isUsed boolean whether is only
	 */
	public void setUsedDelimit(boolean isUsed) {
		if (isUsed) {
			super.setMsgItem("delimit", "yes");
		} else {
			super.setMsgItem("delimit", "no");
		}
	}

	/**
	 * 
	 * Set estimated number of instances
	 * 
	 * @param isUsed boolean whether is used
	 * @param estimateSize String the estimated size
	 */
	public void setUsedEstimate(boolean isUsed, String estimateSize) {
		if (isUsed) {
			super.setMsgItem("estimate", estimateSize);
		} else {
			super.setMsgItem("estimate", "none");
		}
	}

	/**
	 * 
	 * Set used prefix
	 * 
	 * @param isUsed boolean whether prefix is used
	 * @param prefix String the prefix
	 */
	public void setUsedPrefix(boolean isUsed, String prefix) {
		if (isUsed) {
			super.setMsgItem("prefix", prefix);
		} else {
			super.setMsgItem("prefix", "none");
		}
	}

	/**
	 * 
	 * Set used cached page number
	 * 
	 * @param isUsed whether cache is used
	 * @param cacheSize String the cahe size
	 */
	public void setUsedCache(boolean isUsed, String cacheSize) {
		if (isUsed) {
			super.setMsgItem("cach", cacheSize);
		} else {
			super.setMsgItem("cach", "none");
		}
	}

	/**
	 * 
	 * Set Lo file count
	 * 
	 * @param isUsed boolean whether Lo File is used
	 * @param loFileNum String the number of Lo file
	 */
	public void setUsedLoFile(boolean isUsed, String loFileNum) {
		if (isUsed) {
			super.setMsgItem("lofile", loFileNum);
		} else {
			super.setMsgItem("lofile", "none");
		}
	}

	/**
	 * 
	 * Get unloaded database result
	 * 
	 * @return List<String> the unload database result
	 */
	public List<String> getUnloadDbResult() {
		TreeNode response = getResponse();
		if (response == null
				|| (this.getErrorMsg() != null && getErrorMsg().trim().length() > 0)) {
			return null;
		}
		List<String> resultList = new ArrayList<String>();
		List<String> resList = response.getResponseMessage();
		Pattern pattern = Pattern.compile("^(\\d+)\\s*\\(\\d+%/(\\d+)%\\)$");
		for (String result : resList) {
			String[] values = result.split(":");
			if (values != null && values.length == 2 && values[1] != null) {
				Matcher m = pattern.matcher(values[1].trim());
				if (m.matches()) {
					resultList.add(result);
				}
			}
		}
		for (int i = 0; i < response.childrenSize(); i++) {
			TreeNode node = response.getChildren().get(i);
			if (node != null && node.getValue("open") != null
					&& node.getValue("open").equals("result")) {
				List<String> list = node.getResponseMessage();
				for (int j = 0; list != null && j < list.size(); j++) {
					String str = list.get(j);
					String[] keys = str.split(":");
					if (keys != null
							&& keys.length > 1
							&& (keys[0].equals("open") || keys[0].equals("close"))) {
						continue;
					}
					resultList.add(str);
				}
				return resultList;
			}
		}
		return resultList;
	}
}
