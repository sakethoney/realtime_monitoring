package com.ksh.crfi.app.zookeeper.dao;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import com.ksh.crfi.mutual.utils.Utils;

import lombok.extern.log4j.Log4j;

@Log4j
public class ExpectedValueWatcher implements Watcher{
	private long expectedValue;
	private CountDownLatch connectedSingnal;
	private ZooKeeper zk;
	

	public ExpectedValueWatcher(ZooKeeper zk, CountDownLatch connectedSignal, long expectedValue) {
		this.zk=zk;
		this.connectedSingnal=connectedSignal;
		this.expectedValue= expectedValue;

	}
	
	@Override
	public void process(WatchedEvent event) {
		if(event.getType()==EventType.NodeChildrenChanged) {
			try {
					String path = event.getPath();
					
					byte[] out = zk.getData(path, false, null);
					long actualResult =Utils.convertByteArrayToLong(out);
					
					if(expectedValue != actualResult) {
						byte[] inner = zk.getData(path, this, null);
						actualResult = Utils.convertByteArrayToLong(inner);
					}
					
					if(expectedValue == actualResult) {
						connectedSingnal.countDown();
					}
			}catch(Exception e) {
				log.error("Failed to handle Zookeeper watch event", e);
			}
		}
	}
}
