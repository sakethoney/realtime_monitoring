package com.ksh.crfi.windows.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.ksh.crfi.app.kafka.dao.log4j.KafkaLogAppender;
import com.ksh.crfi.mutual.utils.Utils;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ksh.crfi.windows.user","com.ksh.crfi.app.zookeeper.dao",
		"com.ksh.crfi.app.zookeeper.dao.constants","com.ksh.crfi.zookeeper.dao.exception"})
//@Import({ServiceStatusUpdater.class})
public class Application {
	public static void main(String[] args) {
		System.setProperty("spring.application.name", "iNav Windows User Services");
		Utils.setSaslConfiguration();
		Utils.customizeLog4j();
		ApplicationContext appContext = SpringApplication.run(Application.class, args);
		KafkaLogAppender.setApplicationContext(appContext);
	}

}
