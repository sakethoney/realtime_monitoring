package com.ksh.crfi.app.zookeeper.dao.lock;

import org.apache.zookeeper.KeeperException;

public class WriteLock extends ProtocolSupport{

	private final String dir;
	private String id;
	private ZNodeName idName;
	private String ownerId;
	private String lastChildId;
	private byte[] data = {0x12,0x34};
	private LockListener callbackListener;
	private LockZooKeeperOperation zop;
	private final LockerManager lockerManager;
	
	private class LockZooKeeperOperation implements ZooKeeperOperation {

		@Override
		public boolean execute() throws KeeperException, InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
