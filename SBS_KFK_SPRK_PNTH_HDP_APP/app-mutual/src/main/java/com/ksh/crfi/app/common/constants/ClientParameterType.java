package com.ksh.crfi.app.common.constants;

public enum ClientParameterType {

	RUN_TYPE("Run_Type");
	private String parameterType;
	private ClientParameterType(String parameterType) {
		this.parameterType=parameterType;
	}
	@Override
	public String toString() {
		return parameterType;
	}
}
