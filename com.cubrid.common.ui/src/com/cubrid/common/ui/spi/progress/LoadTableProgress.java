/*
 * Copyright (C) 2018 CUBRID Co., Ltd. All rights reserved by CUBRID Co., Ltd.
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

package com.cubrid.common.ui.spi.progress;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.TableDetailInfo;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.spi.Messages;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;

/**
 * An abstract class that must be inherited
 * to implement the Action to be used in the TableDashboard.
 *
 * @author hun-a
 *
 */
public abstract class LoadTableProgress implements IRunnableWithProgress {
	protected final Logger LOGGER;
	protected final CubridDatabase database;
	protected final List<TableDetailInfo> tableList;
	protected final String taskName;
	protected final String subTaskName;
	protected boolean success = false;

	public LoadTableProgress(CubridDatabase database,
			List<TableDetailInfo> tableList,
			String taskName, String subTaskName) {
		this.LOGGER = LogUtil.getLogger(this.getClass());
		this.database = database;
		this.tableList = tableList;
		this.taskName = taskName;
		this.subTaskName = subTaskName;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(taskName, tableList.size());
		Connection conn = null;
		try {
			conn = JDBCConnectionManager.getConnection(
					database.getDatabaseInfo(), true);

			for (TableDetailInfo tablesDetailInfo : tableList) {
				monitor.subTask(Messages.bind(subTaskName, tablesDetailInfo.getTableName()));

				int count = count(conn,
						tablesDetailInfo.getTableName());
				setCount(tablesDetailInfo, count);

				monitor.worked(1);
				if (monitor.isCanceled()) {
					break;
				}
			}
			success = true;
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn);
			monitor.done();
		}
	}

	/**
	 * You must implement this method
	 * to get the information you want from the table.
	 *
	 * @param conn
	 * @param tableName
	 * @return count
	 */
	protected abstract int count(Connection conn, String tableName);

	/**
	 * You must implement this method to put the specific value
	 * you want to set into <code>TableDetailInfo</code>. 
	 * <p>
	 * The specific values are like column count, records count, keys count and record size value.
	 *
	 * @param tablesDetailInfo
	 * @param count
	 */
	protected abstract void setCount(TableDetailInfo tablesDetailInfo, int count);

	/**
	 * loadTablesInfo
	 *
	 * @return Catalog
	 */
	public void getCount() {
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				try {
					new ProgressMonitorDialog(null).run(true, true, LoadTableProgress.this);
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		});
	}

	public boolean isSuccess() {
		return success;
	}
}
