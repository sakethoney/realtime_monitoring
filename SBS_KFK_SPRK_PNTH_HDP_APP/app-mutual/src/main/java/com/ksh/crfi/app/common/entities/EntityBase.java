package com.ksh.crfi.app.common.entities;

import java.io.Serializable;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ksh.crfi.mutual.constants.ApplicationConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EntityBase implements Serializable {

	private static final long serialVersionUID=1L;
	
	@Column(name = ApplicationConstants.ID)
	protected String id;
	
	@JsonIgnore
	@Column(name = ApplicationConstants.INSERT_TIME)
	protected Long insertTime;
	
	@JsonIgnore
	public String getKey() {
		return id;
	}
}
