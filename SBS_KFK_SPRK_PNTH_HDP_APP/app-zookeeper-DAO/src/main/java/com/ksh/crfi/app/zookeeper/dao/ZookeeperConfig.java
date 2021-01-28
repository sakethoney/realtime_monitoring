package com.ksh.crfi.app.zookeeper.dao;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.extern.log4j.Log4j;

@Configuration
@Log4j
public class ZookeeperConfig {

	@Autowired
	private ZookeeperInterceptor zookeeperInterceptor;
	@Autowired
	private LocalConfigurationManager localConfig;
	
	String zookeeperUrl;
	
	@Bean
	public ZooKeeper getZookeeper() {
		try {
				ZooKeeperProxy.setConnectionString(localConfig.get(PropertyName.ZOOKEEPER_URL));
				ZooKeeperProxy proxy = (ZooKeeperProxy) Enhancer.create(ZooKeeperProxy.class, zookeeperInterceptor);
				ZooKeeperProxy.waitConnected();
				proxy.closeProxy();
				return proxy;
		}catch(Exception e) {
			log.error("Failed to create Zookeeper proxy", e);
		}
		return null;
	}
}
