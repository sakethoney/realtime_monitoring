package com.ksh.crfi.app.kafka.dao.utils;

import java.io.File;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;

import com.ksh.crfi.app.kafka.dao.constants.KafkaConst;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.extern.log4j.Log4j;

@Log4j
public class KafkaUtils {

	private KafkaUtils() {
		
	}
	
	public static void configAuthentication(Map<String, Object> properties, boolean saslEnabled) {
		if(saslEnabled) {
			properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
			properties.put(KafkaConst.SASL_KERBEROS_SERVICE_NAME, "kafka");
			
			File file= Utils.getFile(KafkaConst.TRUSTSTORE_FILE);
			if(file.exists()) {
				properties.put(KafkaConst.SSL_TRUSTSTORE_LOCATION, file.getAbsolutePath());
				log.info("set Kafka trustore as : "+ file.getAbsolutePath());
			}
		}
	}
}
