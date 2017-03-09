package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.cubridmanager.core.cubrid.dbspace.model.DbSpaceInfoList.FreeTotalSizeSpacename;

public class DbSpaceInfoListNew extends DbSpaceInfoList{
	public static class DatabaseDescription{
		private int volume_count;
		private int used_size;
		private int total_size;
		private String purpose;
		private String type;
		private int free_size;
		
		public int getVolume_count() {
			return volume_count;
		}
		public void setVolume_count(int volume_count) {
			this.volume_count = volume_count;
		}
		public int getUsed_size() {
			return used_size;
		}
		public void setUsed_size(int used_size) {
			this.used_size = used_size;
		}
		public int getTotal_size() {
			return total_size;
		}
		public void setTotal_size(int total_size) {
			this.total_size = total_size;
		}
		public String getPurpose() {
			return purpose;
		}
		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getFree_size() {
			return free_size;
		}
		public void setFree_size(int free_size) {
			this.free_size = free_size;
		}
	}
	
	public static class FileSpaceDescription {
		private int used_size;
		private int total_size;
		private String data_type;
		private int file_count;
		private int reserved_size;
		private int file_table_size;
		
		public int getUsed_size() {
			return used_size;
		}
		public void setUsed_size(int used_size) {
			this.used_size = used_size;
		}
		public int getTotal_size() {
			return total_size;
		}
		public void setTotal_size(int total_size) {
			this.total_size = total_size;
		}
		public String getData_type() {
			return data_type;
		}
		public void setData_type(String data_type) {
			this.data_type = data_type;
		}
		public int getFile_count() {
			return file_count;
		}
		public void setFile_count(int file_count) {
			this.file_count = file_count;
		}
		public int getReserved_size() {
			return reserved_size;
		}
		public void setReserved_size(int reserved_size) {
			this.reserved_size = reserved_size;
		}
		public int getFile_table_size() {
			return file_table_size;
		}
		public void setFile_table_size(int file_table_size) {
			this.file_table_size = file_table_size;
		}
	}
	
	private static final Logger LOGGER = LogUtil.getLogger(DbSpaceInfoListNew.class);

	private List<DatabaseDescription> dbinfo = null;
	private List<FileSpaceDescription> fileinfo = null;
	
	public List<DatabaseDescription> getDbinfo() {
		synchronized (this) {
			return dbinfo;
		}
	}
	
	public List<FileSpaceDescription> getFileinfo(){
		synchronized (this){
			return fileinfo;
		}
	}
	
	public void setDbinfo(List<DatabaseDescription> databaseDescriptionList) {
		synchronized (this) {
			this.dbinfo = databaseDescriptionList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addDbinfo(DatabaseDescription info) {
		synchronized (this) {
			if (dbinfo == null) {
				dbinfo = new ArrayList<DatabaseDescription>();
			}
			if (!dbinfo.contains(info)) {
				dbinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeDbinfo(DatabaseDescription info) {
		synchronized (this) {
			if (dbinfo != null) {
				dbinfo.remove(info);
			}
		}
	}
	
	public void setFileinfo(List<FileSpaceDescription> fileSpaceDescriptionList) {
		synchronized (this) {
			this.fileinfo = fileSpaceDescriptionList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addFileinfo(FileSpaceDescription info) {
		synchronized (this) {
			if (fileinfo == null) {
				fileinfo = new ArrayList<FileSpaceDescription>();
			}
			if (!fileinfo.contains(info)) {
				fileinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeFileinfo(FileSpaceDescription info) {
		synchronized (this) {
			if (fileinfo != null) {
				fileinfo.remove(info);
			}
		}
	}

	
	public int getTotalSize(){
		int totalSize = 0;
		for (DbSpaceInfo bean : spaceinfo) {
			if (!bean.getType().equals("PERMANENT")
					&& !bean.getType().equals("TEMPORARY")) {
				continue;
			}
			totalSize += bean.getTotalpage();
		}
		
		return totalSize;
	}

	public int getFreeSize(){
		int freeSize = 0;
		for (DbSpaceInfo bean : spaceinfo) {
			if (!bean.getType().equals("PERMANENT")
					&& !bean.getType().equals("TEMPORARY")) {
				continue;
			}
			freeSize += bean.getFreepage();
		}
		
		return freeSize;
	}
	
	
	public void createDbSpaceDescriptionData(List<Map<String, String>> dbSpaceDescriptionData){
		dbSpaceDescriptionData.clear();
		for(DatabaseDescription d : dbinfo){
			Map<String, String> line = new HashMap<String, String>();
			line.put("0", d.getType());
			line.put("1", d.getPurpose() + " DATA");
			line.put("2", String.valueOf(d.getVolume_count()));
			line.put("3", String.valueOf(d.getUsed_size()));
			line.put("4", String.valueOf(d.getFree_size()));
			line.put("5", String.valueOf(d.getTotal_size()));
			dbSpaceDescriptionData.add(line);
		}
	}
	
	public void createFileSpaceDescriptionData(List<Map<String, String>> fileSpaceDescriptionData){
		for(FileSpaceDescription d : fileinfo){
			Map<String, String> line = new HashMap<String, String>();
			line.put("0", d.getData_type());
			line.put("1", String.valueOf(d.getFile_count()));
			line.put("2", String.valueOf(d.getUsed_size()));
			line.put("3", String.valueOf(d.getFile_table_size()));
			line.put("4", String.valueOf(d.getReserved_size()));
			line.put("5", String.valueOf(d.getTotal_size()));
			fileSpaceDescriptionData.add(line);
		}
	}
	
	public void createVolumeDescriptionData(List<Map<String, String>> volumeDescriptionData){
		for(DbSpaceInfo d : spaceinfo){
			Map<String, String> line = new HashMap<String, String>();
			line.put("0", String.valueOf(d.getVolid()));
			line.put("1", d.getType());
			if (d.getType().compareTo("Active_log") == 0 ||
				d.getType().compareTo("Archive_log") == 0){
				line.put("2", "ARCHIVE");
			}else{
				line.put("2", d.getPurpose() + " DATA");
			}
			line.put("3", String.valueOf(d.getUsedpage()));
			line.put("4", String.valueOf(d.getFreepage()));
			line.put("5", String.valueOf(d.getTotalpage()));
			line.put("6", d.getSpacename());
			volumeDescriptionData.add(line);
		}
	}
	
}
