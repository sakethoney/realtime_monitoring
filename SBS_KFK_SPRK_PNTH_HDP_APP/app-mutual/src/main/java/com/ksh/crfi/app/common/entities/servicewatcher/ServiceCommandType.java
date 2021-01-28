package com.ksh.crfi.app.common.entities.servicewatcher;

public enum ServiceCommandType {
	
	START("start"), STOP("stop"), RESTART("restart");
	
	private String commandType;
	
	private ServiceCommandType(String commandType) {
		this.commandType = commandType;
	}
	
	public String getCommandType() {
		return commandType;
	}
	
	public static ServiceCommandType fromString(String text) {
		for(ServiceCommandType sc : ServiceCommandType.values()){
			if(sc.commandType.equalsIgnoreCase(text)) {
				return sc;
			}
		}
		return null;
	}
}
