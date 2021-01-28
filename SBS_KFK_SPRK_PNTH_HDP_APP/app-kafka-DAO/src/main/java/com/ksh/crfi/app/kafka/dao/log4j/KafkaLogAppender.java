package com.ksh.crfi.app.kafka.dao.log4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.context.ApplicationContext;

import com.ksh.crfi.app.common.entities.LogEntity;
import com.ksh.crfi.app.kafka.dao.MessageProducerManager;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.mutual.constants.ApplicationConstants;
import com.ksh.crfi.mutual.utils.JsonObjectMapper;

import lombok.extern.log4j.Log4j;

@Log4j
public class KafkaLogAppender extends AppenderSkeleton {
	
	public static final String  DEFAULT_LAYOUT_PATTERN = "%c{1}:%L - %m";
	
	public static final String DEFAULT_KEY_CLIENT_ID = "CLIENT_ID";
	
	private static ZookeeperConfigurationManager zkConfig;
	private static String brokerList;
	private String topic;
	private MessageProducerManager producerManager;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private volatile boolean isReady = false;
	
	private List<LogEntity> pendingMessages= Collections.synchronizedList(new ArrayList<>());
	private String application;
	
	public static KafkaLogAppender addToRootLogger(String appl) {
		KafkaLogAppender appender = new KafkaLogAppender();
		appender.setApplication(appl);
		appender.setLayout(new PatternLayout(DEFAULT_LAYOUT_PATTERN));
		return appender;
	}
	
	@Override 
	protected void append(LoggingEvent event) {
		LogEntity message = appendLog(event);
		if(!isReady()) {
			if(pendingMessages.size() < 100000) {
				pendingMessages.add(message);
			}
			return;
		}
		checkingPendingMessage();
		sendMessage(message);
	}
	
	private void checkingPendingMessage() {
		if(!pendingMessages.isEmpty()) {
			synchronized(pendingMessages) {
				pendingMessages.forEach(this::sendMessage);
				pendingMessages.clear();
			}
		}
	}
	
	private void sendMessage(LogEntity log) {
		String message = JsonObjectMapper.writeValueAsString(log);
		if(message != null) {
			producerManager.produceAsync(message, new MessagingCallback());
		}
	}

	public static ZookeeperConfigurationManager getZkConfig() {
		return zkConfig;
	}

	public static void setZkConfig(ZookeeperConfigurationManager zkConfig) {
		KafkaLogAppender.zkConfig = zkConfig;
	}

	public static String getBrokerList() {
		return brokerList;
	}

	public static void setBrokerList(String brokerList) {
		KafkaLogAppender.brokerList = brokerList;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public MessageProducerManager getProducerManager() {
		return producerManager;
	}

	public void setProducerManager(MessageProducerManager producerManager) {
		this.producerManager = producerManager;
	}

	public AtomicBoolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(AtomicBoolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	@Override
	public void close() {
		if(!closed) {
			closed =true;
			producerManager.close();
		}
		
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	private LogEntity appendLog(LoggingEvent event) {
		LogEntity log = new LogEntity();
		log.setApplication(application);
		
		String text;
		if(layout == null) {
			text = event.getRenderedMessage();
		}else {
			text = layout.format(event);
		}
		if(layout == null || layout.ignoresThrowable()) {
			String[] stackTrace = event.getThrowableStrRep();
			if(stackTrace != null) {
				text += Layout.LINE_SEP+String.join(Layout.LINE_SEP, stackTrace);
				
			}
		}
		
		log.setText(text);
		log.setLogLevel(event.getLevel().toString());
		log.setTimestamp(event.getTimeStamp());
		log.setClientId(event.getMDC(DEFAULT_KEY_CLIENT_ID)!= null ? event.getMDC(DEFAULT_KEY_CLIENT_ID).toString():ApplicationConstants.ALL_CLIENTS);
		return log;
	}
	
	protected static class MessagingCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata r, Exception e) {
			if(e!= null) {
				log.error("Failed to send log to kafka", e);
			}
		}
	}
	
	public static void setApplicationContext(ApplicationContext ctx) {
		zkConfig = ctx.getBean(ZookeeperConfigurationManager.class);
	}
}
