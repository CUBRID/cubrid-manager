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
package com.cubrid.cubridmanager.ui.spi.model.loader;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.cubrid.common.core.task.ITask;
import com.cubrid.common.ui.spi.CubridNodeManager;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEvent;
import com.cubrid.common.ui.spi.event.CubridNodeChangedEventType;
import com.cubrid.common.ui.spi.model.CubridDatabase;
import com.cubrid.common.ui.spi.model.CubridNodeLoader;
import com.cubrid.common.ui.spi.model.DefaultSchemaNode;
import com.cubrid.common.ui.spi.model.ICubridNode;
import com.cubrid.common.ui.spi.model.ISchemaNode;
import com.cubrid.common.ui.spi.util.CommonUITool;
import com.cubrid.cubridmanager.core.common.task.CommonQueryTask;
import com.cubrid.cubridmanager.core.common.task.CommonSendMsg;
import com.cubrid.cubridmanager.core.cubrid.database.model.DatabaseInfo;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListNew;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoListOld;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.VolumeType;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.editor.VolumeFolderInfoEditor;
import com.cubrid.cubridmanager.ui.cubrid.dbspace.editor.VolumeInformationEditor;
import com.cubrid.cubridmanager.ui.spi.Messages;
import com.cubrid.cubridmanager.ui.spi.model.CubridNodeType;

/**
 * 
 * This class is responsible to load all children of CUBRID database space
 * folder,these children include Generic volume,Data volume,Index volume,Temp
 * volume,Log volume(including Active log folder and Archive log folder) folder
 * and all volume.
 * 
 * @author pangqiren
 * @version 1.0 - 2009-6-4 created by pangqiren
 */
public class CubridDbSpaceFolderLoader extends
		CubridNodeLoader {
	private static final String GENERIC_VOLUME_FOLDER_NAME = Messages.msgGenerialVolumeFolderName;
	private static final String DATA_VOLUME_FOLDER_NAME = Messages.msgDataVolumeFolderName;
	private static final String INDEX_VOLUME_FOLDER_NAME = Messages.msgIndexVolumeFolderName;
	private static final String TEMP_VOLUME_FOLDER_NAME = Messages.msgTempVolumeFolderName;
	private static final String LOG_VOLUME_FOLDER_NAME = Messages.msgLogVolumeFolderName;
	private static final String ACTIVE_LOG_FOLDER_NAME = Messages.msgActiveLogFolderName;
	private static final String ARCHIVE_LOG_FOLDER_NAME = Messages.msgArchiveLogFolderName;
	
	private static final String PERMANENT_PERMANENT_DATA_FOLDER_NAME = "Permanent_PermanentData";
	private static final String PERMANENT_TEMPORARY_DATA_FOLDER_NAME = "Permanent_TemporaryData";
	private static final String TEMPORARY_TEMPORARY_DATA_FOLDER_NAME = "Temporary_TemporaryData";

	public static final String GENERIC_VOLUME_FOLDER_ID = "Generic";
	public static final String DATA_VOLUME_FOLDER_ID = "Data";
	public static final String INDEX_VOLUME_FOLDER_ID = "Index";
	public static final String TEMP_VOLUME_FOLDER_ID = "Temp";
	public static final String LOG_VOLUME_FOLDER_ID = "Log";
	public static final String ACTIVE_LOG_FOLDER_ID = "Active";
	public static final String ARCHIVE_LOG_FOLDER_ID = "Archive";
	
	public static final String PERMANENT_PERMANENT_DATA_FOLDER_ID = "PERMANENT PERMANENT DATA";
	public static final String PERMANENT_TEMPORARY_DATA_FOLDER_ID = "PERMANENT TEMPORARY DATA";
	public static final String TEMPORARY_TEMPORARY_DATA_FOLDER_ID = "TEMPORARY TEMPORARY DATA";
	
	class Folder{
		private ICubridNode node;
		private String type;

		public Folder(ICubridNode parent, String volumeFolderId, String volumeFolderName, String volumeFolder, String type){
			String indexVolumeFolderId = parent.getId() + NODE_SEPARATOR
					+ volumeFolderId;
			node = parent.getChild(indexVolumeFolderId);
			if (node == null) {
				node = new DefaultSchemaNode(indexVolumeFolderId,
						volumeFolderName, "icons/navigator/folder.png");
				node.setType(volumeFolder);
				node.setContainer(true);
				if (volumeFolder.compareTo(CubridNodeType.LOG_VOLUEM_FOLDER) != 0) {
					node.setEditorId(VolumeFolderInfoEditor.ID);
				}
				parent.addChild(node);
			}

			this.type = type;
		}

		public ICubridNode getICubridNode(){
			return node;
		}

		public String getType(){
			return type;
		}

	};

	/**
	 * 
	 * Load children object for parent
	 * 
	 * @param parent the parent node
	 * @param monitor the IProgressMonitor object
	 */
	public void load(ICubridNode parent, final IProgressMonitor monitor) {
		synchronized (this) {
			if (isLoaded()) {
				return;
			}
			
			CubridDatabase database = ((ISchemaNode) parent).getDatabase();
			
			if (DbSpaceInfoList.useOld(database.getDatabaseInfo().getServerInfo().getEnvInfo())) {
				
				HashMap<String, Folder> folders = new HashMap<String, Folder>();
				Folder genericVolumeFolder = new Folder(parent, 
														GENERIC_VOLUME_FOLDER_ID, 
														GENERIC_VOLUME_FOLDER_NAME, 
														CubridNodeType.GENERIC_VOLUME_FOLDER,
														CubridNodeType.GENERIC_VOLUME);
				Folder dataVolumeFolder = new Folder(parent, 
													 DATA_VOLUME_FOLDER_ID, 
													 DATA_VOLUME_FOLDER_NAME, 
													 CubridNodeType.DATA_VOLUME_FOLDER,
													 CubridNodeType.DATA_VOLUME);
				Folder indexVolumeFolder = new Folder(parent, 
													  INDEX_VOLUME_FOLDER_ID, 
													  INDEX_VOLUME_FOLDER_NAME, 
													  CubridNodeType.INDEX_VOLUME_FOLDER,
													  CubridNodeType.INDEX_VOLUME);
				Folder tempVolumeFolder = new Folder(parent, 
													 TEMP_VOLUME_FOLDER_ID, 
													 TEMP_VOLUME_FOLDER_NAME, 
													 CubridNodeType.TEMP_VOLUME_FOLDER,
													 CubridNodeType.TEMP_VOLUME);
				Folder logVolumeFolder = new Folder(parent, 
													LOG_VOLUME_FOLDER_ID, 
													LOG_VOLUME_FOLDER_NAME, 
													CubridNodeType.LOG_VOLUEM_FOLDER,
													CubridNodeType.LOG_VOLUEM_FOLDER);
				Folder activeLogFolder = new Folder(logVolumeFolder.getICubridNode(), 
													ACTIVE_LOG_FOLDER_ID, 
													ACTIVE_LOG_FOLDER_NAME, 
													CubridNodeType.ACTIVE_LOG_FOLDER,
													CubridNodeType.ACTIVE_LOG);
				Folder archiveLogFolder = new Folder(logVolumeFolder.getICubridNode(), 
													 ARCHIVE_LOG_FOLDER_ID, 
													 ARCHIVE_LOG_FOLDER_NAME, 
													 CubridNodeType.ARCHIVE_LOG_FOLDER,
													 CubridNodeType.ARCHIVE_LOG);
				folders.put(VolumeType.GENERIC.getText(), genericVolumeFolder);
				folders.put(VolumeType.DATA.getText(), dataVolumeFolder);
				folders.put(VolumeType.INDEX.getText(), indexVolumeFolder);
				folders.put(VolumeType.TEMP.getText(), tempVolumeFolder);
				folders.put(VolumeType.ACTIVE_LOG.getText(), activeLogFolder);
				folders.put(VolumeType.ARCHIVE_LOG.getText(), archiveLogFolder);
	
				DbSpaceInfoListOld dbSpaceInfo = new DbSpaceInfoListOld();
				DatabaseInfo databaseInfo = database.getDatabaseInfo();
				final CommonQueryTask<DbSpaceInfoListOld> task = new CommonQueryTask<DbSpaceInfoListOld>(
							parent.getServer().getServerInfo(),
							CommonSendMsg.getCommonDatabaseSendMsg(), dbSpaceInfo);
				task.setDbName(database.getLabel());
				monitorCancel(monitor, new ITask[]{task });
				task.execute();
				final String errorMsg = task.getErrorMsg();
				if (!monitor.isCanceled() && errorMsg != null
						&& errorMsg.trim().length() > 0) {
					genericVolumeFolder.getICubridNode().removeAllChild();
					dataVolumeFolder.getICubridNode().removeAllChild();
					indexVolumeFolder.getICubridNode().removeAllChild();
					tempVolumeFolder.getICubridNode().removeAllChild();
					archiveLogFolder.getICubridNode().removeAllChild();
					activeLogFolder.getICubridNode().removeAllChild();
	
					Display display = Display.getDefault();
					display.syncExec(new Runnable() {
						public void run() {
							CommonUITool.openErrorBox(null, errorMsg);
						}
					});
					setLoaded(true);
					return;
				}
				if (monitor.isCanceled()) {
					setLoaded(true);
					return;
				}
	
				genericVolumeFolder.getICubridNode().removeAllChild();
				dataVolumeFolder.getICubridNode().removeAllChild();
				indexVolumeFolder.getICubridNode().removeAllChild();
				tempVolumeFolder.getICubridNode().removeAllChild();
				archiveLogFolder.getICubridNode().removeAllChild();
				activeLogFolder.getICubridNode().removeAllChild();
	
				dbSpaceInfo = task.getResultModel();
				List<DbSpaceInfoList.DbSpaceInfo> spaceInfoList = dbSpaceInfo == null ? null
						: dbSpaceInfo.getSpaceinfo();
				for (int i = 0; spaceInfoList != null && i < spaceInfoList.size(); i++) {
					DbSpaceInfoList.DbSpaceInfo spaceInfo = spaceInfoList.get(i);
					ICubridNode volumeNode = new DefaultSchemaNode("",
							spaceInfo.getSpacename(), "");
					volumeNode.setContainer(false);
					volumeNode.setModelObj(spaceInfo);
					volumeNode.setEditorId(VolumeInformationEditor.ID);
					String type = spaceInfo.getType();
					if (type == null) {
						continue;
					}
					
					Folder folder = folders.get(type);
					if (folder != null) {
						String id = folder.getICubridNode().getId() + NODE_SEPARATOR + spaceInfo.getSpacename();
						volumeNode.setId(id);
						volumeNode.setType(folder.getType());
						volumeNode.setIconPath("icons/navigator/volume_item.png");
						folder.getICubridNode().addChild(volumeNode);
					}
				}
				if (spaceInfoList != null && !spaceInfoList.isEmpty()) {
					Collections.sort(genericVolumeFolder.getICubridNode().getChildren());
					Collections.sort(dataVolumeFolder.getICubridNode().getChildren());
					Collections.sort(indexVolumeFolder.getICubridNode().getChildren());
					Collections.sort(tempVolumeFolder.getICubridNode().getChildren());
					Collections.sort(archiveLogFolder.getICubridNode().getChildren());
					Collections.sort(activeLogFolder.getICubridNode().getChildren());
				}
				databaseInfo.setDbSpaceInfoList(dbSpaceInfo);
				setLoaded(true);
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent((ICubridNode) parent,
								CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
			} else {				
				HashMap<String, Folder> folders = new HashMap<String, Folder>();
				
				Folder PP_volume_folder = new Folder(parent, 
													 PERMANENT_PERMANENT_DATA_FOLDER_ID,
													 PERMANENT_PERMANENT_DATA_FOLDER_NAME,
													 CubridNodeType.PP_VOLUME_FOLDER,
													 CubridNodeType.PP_VOLUME
													 );
				Folder PT_volume_folder = new Folder(parent,
													 PERMANENT_TEMPORARY_DATA_FOLDER_ID,
													 PERMANENT_TEMPORARY_DATA_FOLDER_NAME,
													 CubridNodeType.PT_VOLUME_FOLDER,
													 CubridNodeType.PT_VOLUME);
				Folder TT_volume_folder = new Folder(parent, 
													 TEMPORARY_TEMPORARY_DATA_FOLDER_ID,
													 TEMPORARY_TEMPORARY_DATA_FOLDER_NAME,
													 CubridNodeType.TT_VOLUME_FOLDER,
													 CubridNodeType.TT_VOLUME);
				folders.put("PERMANENT_PERMANENT", PP_volume_folder);
				folders.put("PERMANENT_TEMPORARY", PT_volume_folder);
				folders.put("TEMPORARY_TEMPORARY", TT_volume_folder);	
				
				DbSpaceInfoListNew dbSpaceInfo = new DbSpaceInfoListNew();
				DatabaseInfo databaseInfo = database.getDatabaseInfo();
				final CommonQueryTask<DbSpaceInfoListNew> task = new CommonQueryTask<DbSpaceInfoListNew>(
						parent.getServer().getServerInfo(),
						CommonSendMsg.getCommonDatabaseSendMsg(), dbSpaceInfo);
				task.setDbName(database.getLabel());
				monitorCancel(monitor, new ITask[]{task });
				task.execute();
				final String errorMsg = task.getErrorMsg();
				if (!monitor.isCanceled() && errorMsg != null
						&& errorMsg.trim().length() > 0) {
					PP_volume_folder.getICubridNode().removeAllChild();
					PT_volume_folder.getICubridNode().removeAllChild();
					TT_volume_folder.getICubridNode().removeAllChild();
	
					Display display = Display.getDefault();
					display.syncExec(new Runnable() {
						public void run() {
							CommonUITool.openErrorBox(null, errorMsg);
						}
					});
					setLoaded(true);
					return;
				}
				if (monitor.isCanceled()) {
					setLoaded(true);
					return;
				}
	
				PP_volume_folder.getICubridNode().removeAllChild();
				PT_volume_folder.getICubridNode().removeAllChild();
				TT_volume_folder.getICubridNode().removeAllChild();
				
				dbSpaceInfo = task.getResultModel();
				List<DbSpaceInfoList.DbSpaceInfo> volumeInfoList = dbSpaceInfo == null ? null
						: dbSpaceInfo.getSpaceinfo();
				for (int i = 0; volumeInfoList != null && i < volumeInfoList.size(); i++) {
					DbSpaceInfoList.DbSpaceInfo volumeInfo = volumeInfoList.get(i);
					ICubridNode volumeNode = new DefaultSchemaNode("",
							volumeInfo.getShortVolumeName(), "");
					volumeNode.setContainer(false);
					volumeNode.setModelObj(volumeInfo);
					volumeNode.setEditorId(VolumeInformationEditor.ID);
					String type = volumeInfo.getType();
					String purpose = volumeInfo.getPurpose();
					if (type == null) {
						continue;
					}
					
					Folder folder = folders.get(type + "_" + purpose);
					if (folder != null) {
						String id = folder.getICubridNode().getId() + NODE_SEPARATOR + volumeInfo.getSpacename();
						volumeNode.setId(id);
						volumeNode.setType(folder.getType());
						volumeNode.setIconPath("icons/navigator/volume_item.png");
						folder.getICubridNode().addChild(volumeNode);
					}
					
				}
				databaseInfo.setDbSpaceInfoList(dbSpaceInfo);
				setLoaded(true);
				CubridNodeManager.getInstance().fireCubridNodeChanged(
						new CubridNodeChangedEvent((ICubridNode) parent,
								CubridNodeChangedEventType.CONTAINER_NODE_REFRESH));
			}
		}
	}
}
