package com.ksh.crfi.app.servicewatcher.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class ServiceControlCommander {

	private Map<String, String>	serviceToScriptLinux;
	private Map<String, String> serviceToWindowsService;
	
	@Autowired
	private ServiceStatusManager serviceStatusManager;
	
	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	@Setter
	private String currentHostPort;
	
	public void initScriptOptions() {
		serviceToScriptLinux = new HashMap<>();
		serviceToScriptLinux.put("app Spark Launcher", "app-spark-launcher,app-spark-launcher-exec.jar");
		
		serviceToWindowsService.put("app Authenticator serivce", zkConfig.get(PropertyName.APP_AUTHENTICATOR_SERVICE));
	}
	
	public String sendCommandOnService(ServiceStatus serviceStatus, ServiceCommandType serviceCommandType serviceCommandType) {
		if(!serviceToScriptLinux.containsKey(serviceStatus.getServiceName())
				&& !serviceToWindowsService.containsKey(serviceStatus.getServiceName()){
			return String.format("{\"serviceName\":%s,\"Port\":%s,\"Reason\":\%s not found\"}",
					serviceStatus.getServiceName(), serviceStatus.getPort(), serviceStatus.getServiceName());
		}
		try {
			if(!servcieToscriptLinux.containsKey(serviceStatus.getServiceName())) {
				String targets = servicetoScriptLinux.get(serviceStatus.getServiceName()).split(",");
				Runttime.getRuntime.exec(String.format("bash ../bin/appLinuxServiceControl.sh %s %s %s", target[0], target[1],
						serviceCommandType.getCommandType()));
			}else {
				Runtime.getRuntime().exec(String.format(
						"powershell -File ../bin/INavWindowsServicesControl.ps1 -serviceName %s -action %s",
						serviceToWindowsService.get(servcieStatus.getServiceName()),
						serviceCommandType.getCommandType()));
			}
			if(serviceCommandType == ServiceCommandType.STOP) {
				serviceStatus.setServiceStatusType(ServiceStatusType.STOPPED);
			}else if(serviceCommandType == ServiceCommandType.START) {
				serviceStatus.setServiceStatusType(ServiceStatusType.RUNNING);
			}
			serviceStatusManager.addOrUpdateService(serviceStatus);
		}catch(Exception e) {
			return String.format("{\"ServiceName\":%s, \"Port\":%s, \"Reason\": \"%s\"}", serviceStatus.getServiceName(),
					serviceStatus.getPort(), e.getMessage());
		}
		return "";
	}
	
	public String sendCommandOnService(ServiceStatus[] serviceStatuses, ServiceCommandType serviceCommandType) {
		StringBuilder sb = new StringBuilder("{\"MonitorAddress\":\"" +currentHostPort+"\", Failed\":[");
		for(ServiceStatus servcieStatus : serviceStatuses) {
			String result = sendCommandOnService(serviceStatus, serviceCommandType);
			if(!Strings.isNullOrEmpty(result)) {
				sb.append(result).append(",");
			}
		}
		if(sb.charAt(sb.length()-1)== ',') {
			sb.deleteCharAt(sb.length()-1);		
		}
		sb.append("]}");
		return sb.toString();
	}
}
