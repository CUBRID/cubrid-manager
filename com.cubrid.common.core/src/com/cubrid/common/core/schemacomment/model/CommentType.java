package com.cubrid.common.core.schemacomment.model;

public enum CommentType {
	INDEX("INDEX"),
	VIEW("VIEW"),
	SP("SP"),
	TRIGGER("TRIGGER"),
	SERIAL("SERIAL"),
	USER("USER"),
	PARTITION("PARTITION");
	
	final private String type;
	
	private CommentType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return type;
	}
}
