package com.ksh.crfi.app.servicewatcher;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.ksh.crfi.app.kafka.dao.log4j.KafkaLogAppender;
import com.ksh.crfi.app.zookeeper.dao.LocalConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.ZooKeeperNodeManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.mutual.utils.Utils;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ksh.crfi.app.servicewatcher","com.ksh.crfi.app.zookeeper.dao"},
							   excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
							   value= ServiceStatusUpdater.class)})
public class ServiceWatcherApplication implements ApplicationRunner{
	
	@Autowired
	ZooKeeperNodeManager zkNodeManager;
	
	@Autowired
	private LocalConfigurationManager localConfigManager;
	
	@Autowired
	private ServicesStatusManager serviceStatusManager;
	
	@Autowired
	private ServiceControlCommander serviceControlCommander;
	
	@Autowired
	private ZookeeperConfigurationManager zkConfig;

	
	public static void main(String[] args )throws Exception{
		System.setProperty("spring.application.name","app Service Watcher");
		Utils.setSaslConfiguration();
		Utils.customizeLog4j();
		
		ApplicationContext context = SpringApplication.run(ServiceWatcherApplication.class, args);
		KafkaLogAppender.setApplicationContext(context);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		String currentHostPort = String.format("%s:%s", Utils.getIpAddress(),
				zkConfig.get(PropertyName.SERVICE_MONITOR_PORT));
		
	}
}
