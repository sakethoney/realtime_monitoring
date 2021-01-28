package com.ksh.crfi.app.zookeeper.dao;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class ZooKeeperProxy extends ZooKeeper {
	
	private static CountDownLatch connectedSignal = new CountDownLatch(1);
	private static String connetionString;
	
	public ZooKeeperProxy() throws IOException {
		super(connetionString, 5000, event -> {
			if(event.getState()== KeeperState.SyncConnected) {
				connectedSignal.countDown();
			}
		});
	}
	
	public static void setConnectionString(String conString) {
		connetionString = conString;
	}
	public static void waitConnected() throws InterruptedException{
		connectedSignal.await();
	}

	public void closeProxy() throws InterruptedException{
		super.close();
	}
}
