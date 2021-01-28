package com.ksh.crfi.app.zookeeper.dao;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import lombok.extern.log4j.Log4j;

@Log4j	
public class ZookeeperDataMonitor implements Watcher, StatCallback{
	
	ZooKeeper zk;
	String znode;
	Watcher chainedWatcher;
	boolean dead;
	DataMonitorListener listener;

	
	
	public ZookeeperDataMonitor(ZooKeeper zk,String znode, Watcher chainedWatcher, DataMonitorListener listener) {
		this.zk= zk;
		this.znode=znode;
		this.chainedWatcher=chainedWatcher;
		this.listener=listener;
		zk.exists(znode, true,this,null);
	}
	
	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		Code code = Code.get(rc);
		doProcessResult(code,rc);
		
	}
	
	public void doProcessResult(Code code, int rc) {
		boolean exists;
		switch(code) {
		case OK:
			exists=true;
			break;
		case NONODE:
			exists =false;
			break;
		case SESSIONEXPIRED:
			dead=true;
			listener.closing(rc);
			return;
		default:
			zk.exists(znode, true,this,null);
			return;
		}
		if(exists) {
			refreshNode();
		}
	}

	public void refreshNode() {
		byte[] data =null;
		try {
			data= zk.getData(znode, false, null);
		}catch(KeeperException e) {
			log.error("failed to get data from zookeeper node : "+ znode, e);
		}catch(InterruptedException e) {
			log.error("Zookeeper get data thread interrupted : "+ znode, e);
		}
	}
	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		if(event.getType()==Event.EventType.None) {
			switch (event.getState()) {
				case Expired:
					dead =true;
					listener.closing(KeeperException.Code.SESSIONEXPIRED.intValue());
					break;
				case SyncConnected:
					
				default:
						break;
			}
		} else {
			if(path!=null && path.equals(znode)) {
				zk.exists(znode, true,this, null);
			}
		}
		if(chainedWatcher != null) {
			chainedWatcher.process(event);
			
		}
		
	}
	public interface DataMonitorListener {
		void exists(byte[] data);
		void closing(int rc);
	}
	
	public boolean getDead() {
		return dead;
	}

	

}
