package com.ksh.crfi.app.common.entities.servicewatcher;

public enum ServiceStatusType {
	RUNNING("running"),
	STOPPED("stopped"),
	OFFLINE("offline");
	
	String statusType;
	
	private ServiceStatusType(String statusType) {
		this.statusType=statusType;
	}
	
	public String getServiceStatusType() {
		return statusType;
	}
}
