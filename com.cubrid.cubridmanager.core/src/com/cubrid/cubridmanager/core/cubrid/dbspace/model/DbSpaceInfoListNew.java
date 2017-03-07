package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public static class VolumeInfo {
		private int volid;
		private  int used_size;
		private int total_size;
		private String volume_name;
		private String purpose;
		private String type;
		private int free_size;
		
		public int getVolid() {
			return volid;
		}
		public void setVolid(int volid) {
			this.volid = volid;
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
		public String getVolume_name() {
			return volume_name;
		}
		public void setVolume_name(String volume_name) {
			this.volume_name = volume_name;
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
		public String getShortVolumeName(){
			return volume_name.substring(volume_name.lastIndexOf('/')+1);
		}
	}
	
	private static final Logger LOGGER = LogUtil.getLogger(DbSpaceInfoListNew.class);

	private List<DatabaseDescription> dbinfo = null;
	private List<FileSpaceDescription> fileinfo = null;
	private List<VolumeInfo> volumeinfo = null;
	
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
	
	public List<VolumeInfo> getVolumeinfo(){
		synchronized (this){
			return volumeinfo;
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
	
	public void setVolumeinfo(List<DbSpaceInfoListNew.VolumeInfo> volumeInfoList) {
		synchronized (this) {
			this.volumeinfo = volumeInfoList;
		}
	}

	/**
	 * Add a instance of DbSpaceInfo into the spaceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void addVolumeinfo(DbSpaceInfoListNew.VolumeInfo info) {
		synchronized (this) {
			if (volumeinfo == null) {
				volumeinfo = new ArrayList<VolumeInfo>();
			}
			if (!volumeinfo.contains(info)) {
				volumeinfo.add(info);
			}
		}
	}

	/**
	 * Remove a instance of DbSpaceInfo from sapceinfo list in the current
	 * instance
	 *
	 * @param info DbSpaceInfo A instance of DbSpaceInfo
	 */
	public void removeVolumeinfo(DbSpaceInfoListNew.VolumeInfo info) {
		synchronized (this) {
			if (volumeinfo != null) {
				volumeinfo.remove(info);
			}
		}
	}
	
	public int getTotalSize(){
		int totalSize = 0;
		for (VolumeInfo bean : volumeinfo) {
			if (!bean.getType().equals("PERMANENT")
					&& !bean.getType().equals("TEMPORARY")) {
				continue;
			}
			totalSize += bean.getTotal_size();
		}
		
		return totalSize;
	}

	public int getFreeSize(){
		int freeSize = 0;
		for (VolumeInfo bean : volumeinfo) {
			if (!bean.getType().equals("PERMANENT")
					&& !bean.getType().equals("TEMPORARY")) {
				continue;
			}
			freeSize += bean.getFree_size();
		}
		
		return freeSize;
	}
	
	public ArrayList<FreeTotalSizeSpacename> getVolumesInfoByType(String fullType){
		ArrayList<FreeTotalSizeSpacename> info = new ArrayList<FreeTotalSizeSpacename>();
		
		String type = fullType.substring(0, fullType.indexOf(" "));
		String purpose = fullType.substring(fullType.indexOf(" ")+1, fullType.lastIndexOf(" "));
		
		for (VolumeInfo bean : volumeinfo) {
			if (bean.getType().equals(type) && bean.getPurpose().equals(purpose)) {
				info.add(new FreeTotalSizeSpacename(bean.getShortVolumeName(), bean.getFree_size(), bean.getTotal_size()));
			}
		}
		return info;
	}
	
}
