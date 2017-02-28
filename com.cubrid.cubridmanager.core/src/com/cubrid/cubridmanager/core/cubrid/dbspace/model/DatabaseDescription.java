package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

import com.cubrid.cubridmanager.core.common.model.IModel;

public class DatabaseDescription{
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
