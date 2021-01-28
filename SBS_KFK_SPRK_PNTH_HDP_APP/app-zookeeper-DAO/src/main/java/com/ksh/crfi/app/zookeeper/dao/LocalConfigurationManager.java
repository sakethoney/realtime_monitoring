package com.ksh.crfi.app.zookeeper.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.app.zookeeper.dao.constants.ZookeeperConst;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class LocalConfigurationManager {
	private static final String FAILED_TO_LOAD = "Failed to load";
	private Properties properties = new Properties();
	
	public LocalConfigurationManager() {
		properties = new Properties();
		boolean propLoaded = false;
		if (new File(ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME).exists()) {
			try {
				@Cleanup
				InputStream inStream = new FileInputStream(ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME);
				properties.load(inStream);
				propLoaded = true;
			}catch(Exception e) {
				log.info(FAILED_TO_LOAD+ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME);
			}
		}
		if(!propLoaded) {
			File file = Utils.getFile(ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME);
			if(file.exists()) {
				try {
					@Cleanup
					InputStream inStream = new FileInputStream(file.getAbsolutePath());
					properties.load(inStream);
					propLoaded = true;
				}catch(Exception e) {
					log.info(FAILED_TO_LOAD+ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME + e);
				}
			}
		}
		if(!propLoaded) {
			try {
				@Cleanup
				InputStream inStream = LocalConfigurationManager.class.getClassLoader().getResourceAsStream(ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME);
				properties.load(inStream);
				propLoaded = true;
			}catch(Exception e) {
				log.info(FAILED_TO_LOAD+ZookeeperConst.APPLICATION_PROPERTIES_FILE_NAME + e);
			}
		}
	}
	
	public String get(PropertyName propertyName) {
		return get(propertyName,"");
	}
	
	public String get(PropertyName propertyName , String defaultValue) {
		String value = properties.getProperty(propertyName.toString());
		if(value==null) {
			value=getPropertyFromEnvVariable(propertyName);
		}
		return value !=null ? value : defaultValue;
	}
	
	public String get(String propertyName , String defaultValue) {
		String value = properties.getProperty(propertyName);
		if(value==null) {
			value=getPropertyFromEnvVariable(propertyName);
		}
		return value !=null ? value : defaultValue;
	}
	private String getPropertyFromEnvVariable(PropertyName propertyName) {
		String name = propertyName.toString();
		String value = System.getenv(name);
		if(value != null) {
			properties.setProperty(name, value);
		}
		return value;
	}
	
	private String getPropertyFromEnvVariable(String propertyName) {
		String name = propertyName;
		String value = System.getenv(name);
		if(value !=null) {
			properties.setProperty(name, value);
		}
		return value;
	}
	
	public void set(PropertyName propertyName,String value) {
		properties.setProperty(propertyName.toString(), value);
	}
}
