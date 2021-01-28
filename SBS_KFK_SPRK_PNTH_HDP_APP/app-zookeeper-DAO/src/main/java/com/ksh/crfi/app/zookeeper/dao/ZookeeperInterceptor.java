package com.ksh.crfi.app.zookeeper.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.extern.log4j.Log4j;
@Component
@Log4j
public class ZookeeperInterceptor implements MethodInterceptor {
	private volatile ZooKeeper zookeeper;
	private static final int RETRY_COUNT = 3;
	
	@Autowired
	private LocalConfigurationManager localConfig;
	String zookeeperURL;
	
	@Autowired
	public void init() {
		zookeeperURL = localConfig.get(PropertyName.ZOOKEEPER_URL);
	}

	
	
	private ZooKeeper getZooKeeper() {
		if(!isZooKeeperValid()) {
			synchronized(this) {
				if(!isZooKeeperValid()) {
					zookeeper = createZooKeeper(zookeeperURL);
				}
			}
		}
		return zookeeper;
	}
	
	private boolean isZooKeeperValid() {
		return zookeeper != null && zookeeper.getState() != States.CLOSED;
	}
	
	public static ZooKeeper createZooKeeper(String zooKeeperUrl) {
		ZooKeeper zk = null;
		log.info(String.format("Connected to ZooKeeper @ %s", zooKeeperUrl));
		
		try {
			
		}catch(Exception e) {
			log.error("Failed to connect to Zookeeper:" + zooKeeperUrl, e);
		}
		return zk;
	}
	
	public Object intercept(Object obj, Method method, Object[] objectCollection, MethodProxy methodProxy) throws Throwable {
		
		if("closeProxy".equals(method.getName())) {
			return methodProxy.invokeSuper(obj, objectCollection);
		}
		Throwable exception = null;
		int retry =0;
		while(retry ++ < RETRY_COUNT) {
			ZooKeeper zk = getZooKeeper();
			try {
					return method.invoke(zk, objectCollection);
			}catch(InvocationTargetException e) {
				Throwable ex =e.getTargetException();
				if(ex instanceof KeeperException.SessionExpiredException) {
					log.warn(String.format("Zookeeper session exprired current seesion 0x%x  retry times %d", zk.getSessionId(),retry));
					exception = ex;
				}else {
					throw ex;
				}
			}
		}
		if(exception != null) {
			throw exception;
		}
		return null;
	}
}
