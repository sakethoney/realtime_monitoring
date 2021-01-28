package com.ksh.crfi.app.servicewatcher.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import com.ksh.crfi.app.springboot.common.admin.EnableSpringBootAdminClient;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

@org.springframework.context.annotation.Configuration
@EnableSpringBootAdminClient
public class ApplicationConfig {

	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	@Bean
	public EmbeddedServletContainerFactory tomcatFactory() {
		
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.setPort(Integer.parseInt(zkConfig.get(PropertyName.SERVICE_MONITOR_PORT)));
		return factory;
	}
}
