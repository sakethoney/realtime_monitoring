package com.ksh.crfi.app.springboot.common.admin;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.actuate.endpoint.ShutdownEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.LogFileMvcEndpoint;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.ksh.crfi.app.zookeeper.dao.NodeUpdatedWatcher;
import com.ksh.crfi.app.zookeeper.dao.ZooKeeperNodeManager;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import de.codecentric.boot.admin.client.config.AdminClientProperties;
import de.codecentric.boot.admin.client.config.AdminProperties;
import de.codecentric.boot.admin.client.registration.ApplicationFactory;
import de.codecentric.boot.admin.client.registration.ApplicationRegistrator;
import de.codecentric.boot.admin.client.registration.DefaultApplicationFactory;
import de.codecentric.boot.admin.client.registration.RegistrationApplicationListener;
import lombok.extern.log4j.Log4j;

@Configuration
@EnableConfigurationProperties({AdminProperties.class, AdminClientProperties.class})
@Log4j
public class SpringBootAdminClientConfig {

	@Autowired
	SpringBootAdminClientProperties adminProps;
	
	@Autowired
	private ZookeeperConfigurationManager zookeeperConfigManager;
	
	@Autowired
	private ZooKeeperNodeManager zkNodeManager;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean
	public ShutdownEndpoint shutdownEndPoint() {
		
		ShutdownEndpoint ep = new ShutdownEndpoint();
		ep.setEnabled(true);
		return ep;
	}
	
	@Bean
	public LogFileMvcEndpoint logfileMvcEndpoint() {
		LogFileMvcEndpoint endpoint = new LogFileMvcEndpoint();
		if(adminProps.getLogFilePath()!=null) {
			File logFile = new File(adminProps.getLogFilePath());
			if(logFile.exists()) {
				endpoint.setExternalFile(logFile);
			}
		}
		return endpoint;
	}
	
	private String getAdminUrl() {
		NodeUpdatedWatcher watcher = zkNodeManager.getNewNodeUpdatedWatcher(PropertyName.SPRINGBOOT_ADMIN_HOST,
				id -> springBootUrlUpdatedCallback(getAdminUrl(id)));
		return getAdminUrl(watcher.getData());
			
		}
	private String getAdminUrl(String server) {
		return String.format("http://%s:%s", server, zookeeperConfigManager.get(PropertyName.SPRINGBOOT_ADMIN_PORT));		
	}
	
	@Bean
	public ApplicationRegistrator registrator(AdminProperties admin, ApplicationFactory applicationFactory,
			RestTemplateBuilder restTempBuilder) {
		
		RestTemplateBuilder builder = restTempBuilder
										.messageConverters(new HttpMessageConverter[] {new MappingJackson2HttpMessageConverter()})
										.requestFactory(SimpleClientHttpRequestFactory.class);
		if(admin.getUsername()!=null) {
			builder = builder.basicAuthorization(admin.getUsername(), admin.getApiPath());
		}
		admin.setUrl(new String[] {getAdminUrl()});
		return new ApplicationRegistrator(builder.build(), admin, applicationFactory);	
	}
	
	private void springBootUrlUpdatedCallback(String adminUrl) {
		try {
			log.info("Detected spring boot admin url changed to "+ adminUrl);
			ApplicationRegistrator registrator = applicationContext.getBean(ApplicationRegistrator.class);
			registrator.deregister();
			AdminProperties adminProp = applicationContext.getBean(AdminProperties.class);
			adminProp.setUrl(new String[] {adminUrl});
			registrator.register();
			log.info("Register spring boot admin to "+ adminUrl);
		}catch(Exception e) {
			log.error("Failed to register spring boot admin to "+adminUrl, e);
		}
	}
	
	@Bean
	public ApplicationFactory applicationFactory(AdminClientProperties client, ManagementServerProperties management,
			ServerProperties server, @Value("${endpoints.health.path:/${endpoints.health.id:health}}") String healthEndpoint,
			ServletContext servletContext) {
		
			return new DefaultApplicationFactory(client,management,server,servletContext, healthEndpoint);
	}
	
	@Bean
	public RegistrationApplicationListener registrationListener(AdminProperties admin, ApplicationRegistrator registrator) {
		RegistrationApplicationListener listener = new RegistrationApplicationListener(registrator,registrationTaskScheduler());
		
		listener.setAutoRegister(admin.isAutoRegistration());
		listener.setAutoDeregister(admin.isAutoDeregistration());
		listener.setRegisterPeriod(admin.getPeriod());
		return listener;
	}
	
	@Bean
	@Qualifier("registrationTaskScheduler")
	public TaskScheduler registrationTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(1);
		taskScheduler.setRemoveOnCancelPolicy(true);
		taskScheduler.setThreadNamePrefix("registrationTask");
		return taskScheduler;
	}
}
