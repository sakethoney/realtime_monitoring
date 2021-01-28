package com.ksh.crfi.app.common.entities.profile;

public enum HeaderType {
	STRING("string"),
	BOOLEAN("boolean"),
	NUBMER("number"),
	DATE("date");

	private String type;
	
	private HeaderType(String type) {
		this.type =type;
	}
	public String getType() {
		return type;
	}
}
