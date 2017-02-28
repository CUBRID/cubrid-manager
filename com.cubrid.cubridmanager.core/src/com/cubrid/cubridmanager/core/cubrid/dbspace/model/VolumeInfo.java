package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

public class VolumeInfo {
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
