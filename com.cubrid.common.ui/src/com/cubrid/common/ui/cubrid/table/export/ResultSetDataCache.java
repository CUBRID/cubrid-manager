/*
 * Copyright (C) 2014 Search Solution Corporation. All rights reserved by Search Solution. 
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
package com.cubrid.common.ui.cubrid.table.export;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.ui.query.control.ColumnInfo;
import com.cubrid.jdbc.proxy.driver.CUBRIDBlobProxy;
import com.cubrid.jdbc.proxy.driver.CUBRIDClobProxy;

/**
 * 
 * Cache a querying result data from ResultSet
 * 
 * @author Yu Guojia
 * @version 1.0 - 2014-11-6 created by Yu Guojia
 */
public class ResultSetDataCache {
	private static final Logger LOGGER = LogUtil.getLogger(ResultSetDataCache.class);
	private ArrayList<ColumnInfo> columnInfos;
	private LinkedList<ArrayList<Object>> datas = new LinkedList<ArrayList<Object>>();

	public void AddColumn(ColumnInfo column) {
		if (columnInfos == null) {
			columnInfos = new ArrayList<ColumnInfo>();
		}
		columnInfos.add(column);
	}

	public void AddData(ArrayList<Object> oneRowData) {
		if (datas == null) {
			datas = new LinkedList<ArrayList<Object>>();
		}
		datas.add(oneRowData);
	}

	public ArrayList<ColumnInfo> getColumnInfos() {
		return columnInfos;
	}

	public void setColumnInfos(ArrayList<ColumnInfo> columnInfos) {
		this.columnInfos = columnInfos;
	}

	public List<ArrayList<Object>> getDatas() {
		return datas;
	}

	public void setDatas(LinkedList<ArrayList<Object>> datas) {
		this.datas = datas;
	}

	public void free(){
		for(ArrayList<Object> rowData : datas){
			for(Object value : rowData){
				if(value instanceof Blob){
					Blob blob = (Blob)value;
					try {
						if (blob != null && ((CUBRIDBlobProxy)blob).getProxyObj() != null) {
							blob.free();
							blob = null;
						}
					} catch (SQLException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}else if(value instanceof Clob){
					Clob clob = (Clob)value;
					try {
						if (clob != null && ((CUBRIDClobProxy)clob).getProxyObj() != null) {
							clob.free();
							clob = null;
						}
					} catch (SQLException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	}
}
