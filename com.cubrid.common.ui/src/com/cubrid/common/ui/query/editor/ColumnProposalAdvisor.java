/*
 * Copyright (C) 2013 Search Solution Corporation. All rights reserved by Search Solution.
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
package com.cubrid.common.ui.query.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;

import com.cubrid.common.core.common.model.DBAttribute;
import com.cubrid.common.core.common.model.SchemaInfo;
import com.cubrid.common.core.schemacomment.SchemaCommentHandler;
import com.cubrid.common.core.schemacomment.model.SchemaComment;
import com.cubrid.common.core.util.ConstantsUtil;
import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.QueryUtil;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.event.ICubridNodeChangedListener;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.NodeType;
import com.cubrid.cubridmanager.core.common.jdbc.JDBCConnectionManager;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetAllSchemaTask;
import com.cubrid.cubridmanager.core.cubrid.table.task.GetSchemaTask;

/**
 * 
 * ColumnProposalHandler Description
 * 
 * @author Kevin.Wang
 * @version 1.0 - 2013-4-8 created by Kevin.Wang
 */
public class ColumnProposalAdvisor implements
		ICubridNodeChangedListener {
	private static final Logger LOGGER = LogUtil.getLogger(ColumnProposalAdvisor.class);
	
	private static ColumnProposalAdvisor instance = null;
	
	private static final Map<String, ColumnProposal> cachedMap = new HashMap<String, ColumnProposal>();
	private static final Set<String> collectingKeys = new HashSet<String>();

	/**
	 * Get the instance
	 * 
	 * @return
	 */
	public static ColumnProposalAdvisor getInstance() {
		synchronized (ColumnProposalAdvisor.class) {
			if (instance == null) {
				instance = new ColumnProposalAdvisor();
			}
		}
		return instance;
	}

	/**
	 * The constructor
	 */
	private ColumnProposalAdvisor() {
		CubridNodeManager.getInstance().addCubridNodeChangeListener(this);
	}

	public ColumnProposal findProposal(final DatabaseInfo dbInfo) {
		String key = makeKey(dbInfo);
		ColumnProposal proposal = null;

		synchronized (ColumnProposalAdvisor.class) {
			// TOOLS-4290, Performance issue
			if (isMoreThan500Tables(dbInfo)) {
				return null;
			}
			/*Judge whether loading*/
			if (collectingKeys.contains(key)) {
				return null;
			}
			proposal = cachedMap.get(key);
		}
		/*Load the data*/
		if (proposal == null) {
			loadProposal(dbInfo);
		}

		return proposal;
	}

	private boolean isMoreThan500Tables(DatabaseInfo dbInfo) {
		int count = 0;
		Connection conn = null;
		try {
			conn = JDBCConnectionManager.getConnection(dbInfo, true);
			count = (int) QueryUtil.countRecords(conn, "db_class");
		} catch (SQLException e) {
			LOGGER.error("", e);
		} finally {
			QueryUtil.freeQuery(conn);
		}
		return count > 500;
	}

	/**
	 * load proposal for database
	 * 
	 * @param databaseInfo
	 */
	private void loadProposal(final DatabaseInfo databaseInfo) {
		final String key = makeKey(databaseInfo);
		Job job = new Job("Load database schema information job") {
			protected IStatus run(IProgressMonitor monitor) {
				List<String> tableNames = new ArrayList<String>();
				Map<String, List<ColumnProposalDetailInfo>> columns = new HashMap<String, List<ColumnProposalDetailInfo>>();
				GetAllSchemaTask task = null;
				try {
					task = new GetAllSchemaTask(databaseInfo, monitor);
					task.setNeedCollationInfo(false);
					task.execute();

					/*Check is canceled*/
					if (task.isCancel()) {
						return Status.CANCEL_STATUS;
					}

					if (task.isSuccess()) {
						Map<String, SchemaInfo> schemas = task.getSchemas();
						Map<String, SchemaComment> descriptions = task.getComments();
						List<String> fetchedTableNames = new ArrayList<String>();

						for (SchemaInfo schemaInfo : schemas.values()) {
							if (schemaInfo.isSystemClass()) {
								continue;
							}

							String tableName = schemaInfo.getClassname();
							if (ConstantsUtil.isExtensionalSystemTable(tableName)) {
								continue;
							}
							fetchedTableNames.add(tableName);
						}

						Collections.sort(fetchedTableNames);

						for (String tableName : fetchedTableNames) {
							if (!tableNames.contains(tableName)) {
								tableNames.add(tableName);
							}
							if (columns.containsKey(tableName)) {
								continue;
							}
							SchemaInfo schemaInfo = schemas.get(tableName);
							if (schemaInfo == null) {
								continue;
							}
							if (descriptions != null) {
								SchemaComment schemaComment = SchemaCommentHandler.find(
										descriptions, tableName, null);
								if (schemaComment != null) {
									String description = schemaComment.getDescription();
									schemaInfo.setDescription(description);
								}
							}

							List<ColumnProposalDetailInfo> colInfoList = new ArrayList<ColumnProposalDetailInfo>();
							columns.put(tableName, colInfoList);

							List<DBAttribute> dbClassAttrList = schemaInfo.getClassAttributes();
							for (DBAttribute attr : dbClassAttrList) {
								ColumnProposalDetailInfo colInfo = new ColumnProposalDetailInfo(
										schemaInfo, attr);
								colInfoList.add(colInfo);
							}

							List<DBAttribute> attrList = schemaInfo.getAttributes();
							for (DBAttribute attr : attrList) {
								ColumnProposalDetailInfo colInfo = new ColumnProposalDetailInfo(
										schemaInfo, attr);
								colInfoList.add(colInfo);
							}
							columns.put(schemaInfo.getClassname(), colInfoList);
						}
						/*Cache the data*/
						ColumnProposal proposal = new ColumnProposal();
						proposal.setTableNames(tableNames);
						proposal.setColumns(columns);
						synchronized (ColumnProposalAdvisor.class) {
							cachedMap.put(key, proposal);
						}
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					synchronized (ColumnProposalAdvisor.class) {
						collectingKeys.remove(key);
					}
					task.finish();
				}

				return Status.OK_STATUS;
			}
		};
		
		/*Record collecting key*/
		synchronized (ColumnProposalAdvisor.class) {
			collectingKeys.add(key);
			job.schedule();
		}
	}

	/**
	 * Load proposal for table
	 * 
	 * @param databaseInfo
	 * @param tableName
	 * @param proposal
	 */
	private void loadProposal(final DatabaseInfo databaseInfo,
			final String tableName, final ColumnProposal proposal) {
		Job job = new Job("Load schema information job") {
			protected IStatus run(IProgressMonitor monitor) {
				LOGGER.info("Load table info in ColumnProposalHandler");
				GetSchemaTask getSchemaTask = null;
				try {
					getSchemaTask = new GetSchemaTask(databaseInfo, tableName, monitor);
					getSchemaTask.setNeedCollationInfo(false);
					getSchemaTask.execute();

					if (getSchemaTask.isSuccess()) {
						SchemaInfo schemaInfo = getSchemaTask.getSchema();
						if (schemaInfo != null) {
							List<ColumnProposalDetailInfo> columnList = new ArrayList<ColumnProposalDetailInfo>();
							for (DBAttribute attr : schemaInfo.getAttributes()) {
								columnList.add(new ColumnProposalDetailInfo(schemaInfo, attr));
							}
							proposal.addSchemaInfo(tableName, schemaInfo, columnList);
						}
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					getSchemaTask.finish();
				}
				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}
	
	/**
	 * Remove proposal for database
	 * 
	 * @param dbInfo
	 */
	public void removeProposal(DatabaseInfo dbInfo) {
		String key = makeKey(dbInfo);
		cachedMap.remove(key);
		collectingKeys.remove(key);
	}

	/**
	 * Get database key
	 * 
	 * @param dbInfo
	 * @return
	 */
	private String makeKey(DatabaseInfo dbInfo) {
		String key = dbInfo.getBrokerIP() + ":" + dbInfo.getBrokerPort() + ":"
				+ dbInfo.getDbName();
		return key;
	}

	/**
	 * Remove the table from catch
	 * 
	 * @param databaseInfo
	 * @param tableName
	 */
	private void removeTable(DatabaseInfo databaseInfo, String tableName) {
		String key = makeKey(databaseInfo);

		ColumnProposal proposal = cachedMap.get(key);
		if (proposal != null) {
			proposal.removeSchemaInfo(tableName);
		}
	}

	/**
	 * Refresh the table
	 * 
	 * @param databaseInfo
	 * @param tableName
	 */
	private void refreshTable(final DatabaseInfo databaseInfo,
			final String tableName) {
		removeTable(databaseInfo, tableName);
		String key = makeKey(databaseInfo);
		final ColumnProposal proposal = cachedMap.get(key);
		if (proposal == null) {
			return;
		}
		loadProposal(databaseInfo, tableName, proposal);
	}

	/**
	 * Perform node changed event
	 */
	public void nodeChanged(CubridNodeChangedEvent event) {
		ICubridNode cubridNode = event.getCubridNode();
		CubridNodeChangedEventType eventType = event.getType();
		if (cubridNode == null || eventType == null) {
			return;
		}

		if ((NodeType.USER_TABLE.equals(cubridNode.getType()) || NodeType.USER_VIEW.equals(cubridNode.getType()))
				&& cubridNode instanceof DefaultSchemaNode) {

			DefaultSchemaNode schemaNode = (DefaultSchemaNode) cubridNode;
			DatabaseInfo databaseInfo = schemaNode.getDatabase().getDatabaseInfo();
			if (CubridNodeChangedEventType.NODE_REMOVE.equals(eventType)) {
				removeTable(databaseInfo, schemaNode.getName());
			} else if (CubridNodeChangedEventType.NODE_ADD.equals(eventType)
					|| CubridNodeChangedEventType.NODE_REFRESH.equals(eventType)) {
				refreshTable(databaseInfo, schemaNode.getName());
			}
		}
		if(NodeType.DATABASE.equals(cubridNode.getType()) && cubridNode instanceof CubridDatabase) {
			CubridDatabase database = (CubridDatabase)cubridNode;
			if(CubridNodeChangedEventType.DATABASE_LOGIN.equals(eventType))  {
				removeProposal(database.getDatabaseInfo());
				findProposal(database.getDatabaseInfo());
			}
			
			if(CubridNodeChangedEventType.DATABASE_LOGOUT.equals(eventType)) {
				removeProposal(database.getDatabaseInfo());
			}
			
			if(CubridNodeChangedEventType.CONTAINER_NODE_REFRESH.equals(eventType)) {
				removeProposal(database.getDatabaseInfo());
				findProposal(database.getDatabaseInfo());
			}
		}
		
		if (NodeType.TABLE_FOLDER.equals(cubridNode.getType())
				|| NodeType.VIEW_FOLDER.equals(cubridNode.getType())
				&& CubridNodeChangedEventType.CONTAINER_NODE_REFRESH.equals(eventType)
				&& cubridNode instanceof DefaultSchemaNode) {
			DefaultSchemaNode schemaNode = (DefaultSchemaNode)cubridNode;
			removeProposal(schemaNode.getDatabase().getDatabaseInfo());
			findProposal(schemaNode.getDatabase().getDatabaseInfo());
		}
	}
}
