package com.ksh.crfi.app.common.entities;

import javax.persistence.Column;

public class ClientParameter extends EntityBase{
	private static final long serialVersionUID = 1L;
	@Column(name="parameterType")
	private String parameterType;
	@Column(name="ParameterValue")
	private String parameterValue;
	@Column(name="ClientId")
	private String clientId;

	@Override
	public String getKey() {
		return String.format("%s_%s", clientId,parameterType);
	}
}
