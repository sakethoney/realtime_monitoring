package com.ksh.crfi.app.zookeeper.dao;

import java.io.Closeable;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class NodeUpdatedWatcher implements Watcher, Closeable{
	private ZooKeeper zookeeper;
	private WatcherAction eventHandler;
	private final String path;
	@Getter
	private String data;
	
	public NodeUpdatedWatcher(ZooKeeper zookeeper, String path, WatcherAction eventHandler) {
		this.zookeeper=zookeeper;
		this.path= path;
		this.eventHandler=eventHandler;
		watch();
	}
	private void watch() {
		try {
				data = new String(zookeeper.getData(path, this, null));
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
		watch();
		if(event.getType()==Watcher.Event.EventType.NodeDataChanged && eventHandler != null) {
				eventHandler.call(data);
		}
	}
}
