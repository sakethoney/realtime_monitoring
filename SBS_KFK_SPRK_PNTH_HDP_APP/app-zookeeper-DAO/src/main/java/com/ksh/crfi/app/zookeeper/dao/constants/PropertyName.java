package com.ksh.crfi.app.zookeeper.dao.constants;

public enum PropertyName {
		ZOOKEEPER_URL("zookeeper.url"),
	    ZOOKEEPER_ROOT("zookeeper.root"),
	    HDFS_CONFIG_PATH("/hdfs/config.path"),
	    LOCAL_TEMP_FILE_LOCATION("/fileValidaton/tempFileLocation"),
	    SPARK_YARN_SASL_KEYTAB("/spark/yarn.keytab"),
	    HDFS_USER("/hdfs/user"),
	    SPRINGBOOT_ADMIN_HOST("/springbootadmin/host"),
	    SPRINGBOOT_ADMIN_PORT("/springbootadmin/port"),
	    KAFKA_REQUEST_TIMEOUT("/kafka/request.timeout"),
	    KAFKA_SASL_ENALBLED("/kafka/SASL.enabled"),
	    SERVICE_MONITOR_PORT("/servicemonitor/port"),
	    SERVICE_MONITOR_INSTANCES("/servicemonitor/instances"),
	    SERVICE_MONITOR_LOGFILE("/servicemonitor/logfile"),
	    APP_AUTHENTICATOR_SERVICE("/appauthenticator/servicename"),
	    APP_AUTHENTICATOR_SERVICE_LOGFILE("/appauthenticator/logfile"),
	    APP_AUTHENTICATOR_SERVICE_PORT("/appauthenticator/port"),
	    LOCKER_EXPIRED_TIME("/lockers/expired.time"),
	    WINDOWS_USER_SERVICE_PORT("/windowsServiceUser/port"),
	    PENTAHO_APP_DATA_CLIENTS("/pentaho/app.data.clients"),
	    
	    TIMEZONE("/global/timezone");
		
		private String name;
		
		private PropertyName(String name) {
			this.name=name;
		}
		
		@Override
		public String toString() {
			return name;
		}
}
