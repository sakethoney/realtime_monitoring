package com.ksh.crfi.app.common.entities.acl;

import java.util.List;

import javax.persistence.Column;

import com.ksh.crfi.app.common.entities.ClientParameter;
import com.ksh.crfi.app.common.entities.DeleteMarkEntity;

public class Client extends DeleteMarkEntity{
	private static final long serialVersionUID =1L;
	@Column
	private String ClientName;
	@Column 
	private String tierSetup;
	
	private List<ClientParameter> clientParameters;
}
