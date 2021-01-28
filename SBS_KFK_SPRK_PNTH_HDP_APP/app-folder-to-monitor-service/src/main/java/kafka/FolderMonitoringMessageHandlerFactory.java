package kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ksh.crfi.app.kafka.dao.MessageHandler;
import com.ksh.crfi.app.kafka.dao.MessageHandlerFactory;

public class FolderMonitoringMessageHandlerFactory implements MessageHandlerFactory {
		@Autowired
		private ApplicationContext context;
		
		@Override
		public MessageHandler getMessageHandler(String topic) {
			return context.getBean(FolderMonitoringMessageHandler.class);
		}
}
