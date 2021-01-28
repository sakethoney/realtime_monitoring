package com.ksh.crfi.app.zookeeper.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import lombok.extern.log4j.Log4j;

@Log4j
public class ZookeeperWatcher implements Watcher,ZookeeperDataMonitor.DataMonitorListener, Runnable {

	
	String znode;
	
	ZookeeperDataMonitor dm;
	
	ZooKeeper zk;
	
	ZookeeperListener externalListener;
	
	
	public ZookeeperWatcher(String hostPort, String znode) throws IOException {
		zk = new ZooKeeper(hostPort, 3000, this);
		dm = new ZookeeperDataMonitor(zk, znode, null, this);
	}
	
	public ZookeeperWatcher(ZooKeeper zkeeper, ZookeeperDataMonitor dataMonitor) throws IOException {
		zk = zkeeper;
		dm = dataMonitor;
	}
	
	@Override
	public void run() {
		try {
			synchronized (this) {
				while(!dm.dead) {
					wait();
				}
			}
		}catch(InterruptedException e) {
			log.error("Zookeeper watcher thread interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void exists(byte[] data) {
		String nodeValueNew = new String(data, StandardCharsets.UTF_8);
		log.info(nodeValueNew);
		if(externalListener != null) {
			externalListener.nodeChanged(znode, nodeValueNew);
		}
	}

	@Override
	public void closing(int rc) {
	synchronized (this) {
		notifyAll();
	}
	}

	@Override
	public void process(WatchedEvent event) {
		dm.process(event);
	}
	
	public interface ZookeeperListener {
		
		void nodeChanged(String nodeName, String nodeValue);
	}
	
	public void registerExternalListener(ZookeeperListener listener) {
		externalListener = listener;
	}

}
