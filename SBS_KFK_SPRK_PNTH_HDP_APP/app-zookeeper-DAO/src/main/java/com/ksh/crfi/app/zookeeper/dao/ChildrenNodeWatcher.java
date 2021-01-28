package com.ksh.crfi.app.zookeeper.dao;

import java.io.Closeable;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import lombok.extern.log4j.Log4j;

@Log4j
public class ChildrenNodeWatcher implements Closeable, Watcher{
	
	private ZooKeeper zookeeper;
	private WatcherAction eventHandler;
	private final String path;
	
	public ChildrenNodeWatcher(ZooKeeper zookeeper, String path, WatcherAction eventHandler) {
		this.zookeeper=zookeeper;
		this.path= path;
		this.eventHandler=eventHandler;
		watch();
	}
	private void watch() {
		try {
				zookeeper.exists(path, this);
		}catch(Exception e) {
			log.error("Failed to watch Zookeeper node:"+ path, e);
		}
	}
	
	@Override
	public void close() {
		eventHandler = null;
	}
	
	@Override
	public void process(WatchedEvent event) {
		switch(event.getState()) {
		case Disconnected:
		case AuthFailed:
		case Expired:
			close();
			return;
		default:
			break;
		}
		if(event.getType()==Watcher.Event.EventType.NodeChildrenChanged) {
			if(eventHandler !=null) {
				eventHandler.call(event.getPath());
			}
		}else {
			watch();
		}
	}

}
