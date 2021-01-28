package com.ksh.crfi.app.kafka.dao.constants;

public class KafkaConst {

	public static final String MAX_FETCH_BYTES = "1000000000";
	public static final String MAX_REQUEST_SIZE_VALUE = "1000000000";
	public static final String RETRIES = "10";
	public static final String AUTO_OFFSET_RESET = "latest";
	public static final String SASL_KERBEROS_SERVICE_NAME	= "sasl.kerberos.service.name";
	public static final String SSL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
	public static final String TRUSTSTORE_FILE = "truststore.jks";
	
	private KafkaConst() {
		
	}
}
