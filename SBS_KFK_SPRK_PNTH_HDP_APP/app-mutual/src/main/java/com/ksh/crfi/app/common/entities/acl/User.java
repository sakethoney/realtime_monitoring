package com.ksh.crfi.app.common.entities.acl;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksh.crfi.app.common.entities.DeleteMarkEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="User")
public class User extends DeleteMarkEntity {

	private static final long serialVersionUID = 1L;
	@JsonProperty("roleGroup")
	@Column
	private Set<String> roleList;
	@Column
	private String userName;
	@Column
	private Boolean isAdmin;
}
