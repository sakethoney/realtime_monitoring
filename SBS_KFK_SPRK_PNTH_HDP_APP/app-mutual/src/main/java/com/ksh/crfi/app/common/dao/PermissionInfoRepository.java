package com.ksh.crfi.app.common.dao;

import java.util.List;

import com.ksh.crfi.app.common.entities.acl.AccessControlList;
import com.ksh.crfi.app.common.entities.acl.Client;
import com.ksh.crfi.app.common.entities.acl.Role;
import com.ksh.crfi.app.common.entities.acl.User;
import com.ksh.crfi.app.common.entities.acl.UserPermission;
import com.ksh.crfi.app.common.entities.profile.ProfileException;

public interface PermissionInfoRepository {

	UserPermission getUserPermission(String userName);
	
	boolean hasPermission(String userName, String clientId);
	AccessControlList getAccessControlList() throws ProfileException;
	
	void saveClient(Client client) throws ProfileException;	
	void deleteClient(Client client) throws ProfileException;
	void delteRole(Role role)throws ProfileException;
	void saveRole(Role role) throws ProfileException;
	void saveUser(User user)throws ProfileException;
	void deleteUser(User user)throws ProfileException;
	
	List<Client> getClientsSetup() throws ProfileException;
	
}
