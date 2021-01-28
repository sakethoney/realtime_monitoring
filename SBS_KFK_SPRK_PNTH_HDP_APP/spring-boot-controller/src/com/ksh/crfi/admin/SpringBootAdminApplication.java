package com.ksh.crfi.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(ApplicationConfig.class)
@ComponentScan(basePackages="com.ksh.crfi.zookeeper.dao")
public class SpringBootAdminApplication implements ApplicationRunner{

	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	public static void main(String[] args) {
		System.setProperty("spring.application.name", "KSH Spring Boot Admin");
		//Utils
		PrimaryServer primaryServer = PrimaryServer.
	}
}
