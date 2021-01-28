package com.ksh.crfi.app.zookeeper.dao;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
public class NodeKeeper implements Watcher{

	private ZooKeeper zookeeper;
	private CreateMode createMode;
	private String value;
	private String node;
	private final String path;
	
	public NodeKeeper(ZooKeeper zookeeper, String path, String value, CreateMode createMode) {
		this.zookeeper=zookeeper;
		this.path= path;
		this.createMode=createMode;
		try {
			node =zookeeper.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE, createMode);
		}catch(KeeperException e) {
			if(!(KeeperException.Code.NODEEXISTS.equals(e.code()))) {
				log.error("Failed to Create node: "+ path, e);
				Thread.currentThread().interrupt();
			}else {
				node=path;
			}
		}catch(InterruptedException e) {
			log.error("Failed to create Node:"+ path,e);
			Thread.currentThread().interrupt();
		}
		watch();
	}
	private void watch() {
		try {
				if(!Strings.isNullOrEmpty(node)) {
					zookeeper.getData(node, this,null);
				}
				
		}catch(Exception e) {
			log.error("Failed to watch Zookeeper node:"+ path, e);
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
	
		try {
			if(zookeeper.exists(node, false)==null) {
				node = zookeeper.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE, createMode);
			}
		}catch(Exception e) {
			log.error("Failed to create node:"+path,e);
		}
			watch();
	}
}
