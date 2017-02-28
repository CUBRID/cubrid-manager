package com.cubrid.cubridmanager.core.cubrid.dbspace.model;

public class FileSpaceDescription {
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