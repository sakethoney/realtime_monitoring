package com.ksh.crfi.app.common.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_DEFAULT)
public class LogBean {
	
	private String text;
	
	private String application;
	
	private String logLevel;
	
	private String logMessageTimeStamp;
	
	private String clientId;

}
