package com.ksh.crfi.app.hadoop.dao.config;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.google.common.io.Files;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.extern.log4j.Log4j;

@org.springframework.context.annotation.Configuration
@Log4j
public class HadoopConfig {
	@Autowired
	protected ZookeeperConfigurationManager zkConfig;
	
	public static void addResource(Configuration conf, String filePath, String fileDescription, String tempDirPath)
	throws IOException {
		File targetFile = Utils.getFile(filePath);
		if(!targetFile.exists()) {
			log.error(fileDescription+" does not exists");
			return;
		}
		if(Path.WINDOWS) {
			File tempDir = new File(tempDirPath);
			tempDir.mkdirs();
			String tempFilePath = tempDirPath+"/"+Utils.getFileNameFromPath(filePath);
			File tempFile = new File(tempFilePath);
			Files.copy(targetFile, tempFile);
			conf.addResource(new Path(tempFile.getAbsolutePath()));
		}else {
			conf.addResource(new Path(targetFile.getAbsolutePath()));
		}
		log.info("Use" + fileDescription + ": "+ targetFile.getAbsolutePath());
	}
	@Bean
	public Configuration getHadoopConfiguration() throws IOException {
		Configuration conf = new Configuration();
		addResource(conf, zkConfig.get(PropertyName.HDFS_CONFIG_PATH)+"/core-site.xml", "core-site XML",
				zkConfig.get(PropertyName.LOCAL_TEMP_FILE_LOCATION));
		
		addResource(conf, zkConfig.get(PropertyName.HDFS_CONFIG_PATH)+"/hdfs-site.xml", "hdfs-site XML",
				zkConfig.get(PropertyName.LOCAL_TEMP_FILE_LOCATION));
		
		return conf;
	}
	
	@Bean
	public UserGroupInformation getUserGroupInformation(@Qualifier("getHadoopConfiguration") final Configuration conf) 
	throws IOException{
		return createUserGroupInformation(conf, zkConfig.get(PropertyName.SPARK_YARN_SASL_KEYTAB));
		
	}
	
	private UserGroupInformation createUserGroupInformation(final Configuration conf, String keytabPath)
	throws IOException{
		File file = Utils.getFile(keytabPath);
		if(!file.exists()) {
			UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
			log.info("Use current user gor UGI: "+ugi.getUserName());
			return ugi;
		}
		UserGroupInformation.setConfiguration(conf);
		log.info("use keytab for ugi: "+file.getAbsolutePath());
		return UserGroupInformation.loginUserFromKeytabAndReturnUGI(zkConfig.get(PropertyName.HDFS_USER), file.getAbsolutePath());
	}
}
