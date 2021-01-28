package com.ksh.crfi.windows.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ksh.crfi.app.springboot.common.admin.EnableSpringBootAdminClient;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

@Configuration
@EnableSpringBootAdminClient
public class ApplicationConfig {
	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	@Bean
	public EmbeddedServletContainerFactory tomcatFactory() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		String portStr = zkConfig.get(PropertyName.WINDOWS_USER_SERVICE_PORT);
		int port = Integer.parseInt(portStr);
		factory.setPort(port);
		
		return factory;
	}
}
