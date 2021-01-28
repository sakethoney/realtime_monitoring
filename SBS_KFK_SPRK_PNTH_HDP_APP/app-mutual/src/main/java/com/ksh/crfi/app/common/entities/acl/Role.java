package com.ksh.crfi.app.common.entities.acl;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Table;

import com.ksh.crfi.app.common.entities.DeleteMarkEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name="ClientRole")
public class Role extends DeleteMarkEntity{
	private static final long serialVersionUID =1L;
	@Column
	private Set<String> clientList;

}
