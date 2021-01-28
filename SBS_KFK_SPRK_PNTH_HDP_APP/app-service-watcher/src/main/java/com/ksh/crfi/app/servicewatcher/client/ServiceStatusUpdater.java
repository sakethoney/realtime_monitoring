package com.ksh.crfi.app.servicewatcher.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.mutual.utils.HttpHelper;
import com.ksh.crfi.mutual.utils.JsonObjectMapper;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.extern.log4j.Log4j;

@Configuration
@Log4j
public class ServiceStatusUpdater {

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	private String serviceWatcherHost;
	
	private int port;
	
	private boolean firstTime = true;
	
	@Bean
	public ScheduledExecutorService startUpdateServiceStatus() {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
		serviceWatcherHost = String.format("http://%s:%s/updateServiceStatus", Utils.getIpAddress(),
				zkConfig.get(PropertyName.SERVICE_MONITOR_PORT));
		try {
			TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory)context
					.getBean(EmbeddedServletContainerFactory.class);
			port = factory.getPort();
		}catch(Exception e) {
			log.info("Failed to get port of the application, not run in tomcat");
		}
		 scheduledExecutorService.scheduleWithFixedDelay(this::updateServiceStatus, 10, 30, TimeUnit.SECONDS);
		 return scheduledExecutorService;
 	}
	
	private void updateServiceStatus() {
		ServiceStatus serviceStatus  = new ServiceStatus();
		serviceStatus.setServerName(Utils.getHostName());
		serviceStatus.setIp(Utils.getIpAddress());
		serviceStatus.setServiceName(System.getProperty("spring.application.name"));
		serviceStatus.setPort(port);
		
		String postResult  = HttpHelper.getInstance().post(serviceWatcherHost, JsonObjectMapper.writeValueAsString(serviceStatus));
		if(postResult.startsWith("Post method failed:")) {
			if(firstTime) {
				log.warn("Update service status to "+ serviceWatcherHost+" failed : "+postResult);
				firstTime=false;
			}
		}else {
			firstTime  = true;
		}
	}
}
