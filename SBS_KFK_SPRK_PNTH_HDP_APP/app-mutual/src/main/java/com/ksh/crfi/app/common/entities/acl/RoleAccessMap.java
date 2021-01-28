package com.ksh.crfi.app.common.entities.acl;

import javax.persistence.Column;
import javax.persistence.Table;

import com.ksh.crfi.app.common.entities.DeleteMarkEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="RoleAccessMap")
public class RoleAccessMap extends DeleteMarkEntity{
	private static final long serialVersionUID =1L;
	@Column
	private String clientId;
}
