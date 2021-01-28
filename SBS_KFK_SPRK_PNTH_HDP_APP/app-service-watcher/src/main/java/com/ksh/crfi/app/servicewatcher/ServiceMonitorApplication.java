package com.ksh.crfi.app.servicewatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.ksh.crfi.app.kafka.dao.log4j.KafkaLogAppender;
import com.ksh.crfi.app.servicewatcher.client.ServiceStatusUpdater;
import com.ksh.crfi.app.servicewatcher.utils.ServiceControlCommander;
import com.ksh.crfi.app.servicewatcher.utils.ServiceStatusManager;
import com.ksh.crfi.app.zookeeper.dao.LocalConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.ZooKeeperNodeManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.mutual.utils.Utils;

@SpringBootApplication
@ComponentScan(basePackages= {"com.ksh.crfi.servicewatcher","com.ksh.crfi.zookeeper.dao"}, excludeFilters= {
		@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value = ServiceStatusUpdater.class)})
public class ServiceMonitorApplication {

	@Autowired
	ZookeeperConfigurationManager zkConfig;
	
	@Autowired
	ZooKeeperNodeManager zookeeperNodeManager;
	
	@Autowired
	private LocalConfigurationManager localConfig;
	
	@Autowired
	private ServiceStatusManager serviceStatusManager;
	
	@Autowired
	private ServiceControlCommander serviceControlCommander;
	
	
	public static void main(String[] args) throws Exception {
		System.setProperty("Spring.application.name", "CRFI APP Monitor");
		Utils.setSaslConfiguration();
		Utils.customizeLog4j();
		
		ApplicationContext context  = SpringApplication.run(ServiceMonitorApplication.class, args);
		
		KafkaLogAppender.setApplicationContext(context);
		
	}
	
	@Override
	public void run() {
		String currentHostPort = String.format("%s:%s", Utils.getIpAddress(),zkConfig.get(PropertyName.SERVICE_MONITOR_PORT),
				);
		
	}
}
