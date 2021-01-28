package com.ksh.crfi.app.zookeeper.dao.lock;

public interface LockListener {

	public void lockAcquired(String id);
	
	public void lockReleased(String id);
}
