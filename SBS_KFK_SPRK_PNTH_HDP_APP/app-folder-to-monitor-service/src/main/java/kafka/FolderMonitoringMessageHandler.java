package kafka;

import org.springframework.beans.factory.annotation.Autowired;

import com.ksh.crfi.app.kafka.dao.MessageHandler;

public class FolderMonitoringMessageHandler implements MessageHandler {

	@Autowired
	private FolderMonitoringService folderMonitoringService;
	
	@Override 
	public boolean handleMessage(String topic, String key, String message) throws Exception {
		try {
			folderMonitoringService.monitor(message, null);
			return Boolean.TRUE;
		}catch(Exception e) {
			log.error("Exception occured while handling message in foldermonitoring", exp);
			return Boolean.FALSE;
		}
	}
}
