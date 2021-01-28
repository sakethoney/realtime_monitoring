package com.ksh.crfi.app.zookeeper.dao.lock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class LockerManager {

	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	private volatile Long lokerTimeout;
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	public void queue(WriteLock writelock, String id) {
		executor.schedule(()->{
			if(writelock.unlock(id)) {
				log.warn("Locker Expired"+id);
			}
		},
		getLockerTimeout(), TimeUnit.SECONDS))
	}
	
}
