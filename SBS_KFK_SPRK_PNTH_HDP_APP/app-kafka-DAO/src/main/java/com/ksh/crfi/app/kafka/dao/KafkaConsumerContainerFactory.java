package com.ksh.crfi.app.kafka.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;

import com.ksh.crfi.app.kafka.dao.constants.KafkaConst;
import com.ksh.crfi.app.kafka.dao.utils.KafkaUtils;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.extern.log4j.Log4j;

@Component
@EnableKafka
@Log4j
public class KafkaConsumerContainerFactory {

	private static final String CRFI= "crfi";
	
	@Autowired
	private KafkaMessageListener listener;
	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	
	private List<ConcurrentMessageListenerContainer<String,String>> containerList = new ArrayList<>();
	
	public List<ConcurrentMessageListenerContainer<String,String>> createContainer(List<String> topics,
			String bootstrapServers, String groupId, String councurrencyNum, Map<String, String> additionalProperties){
		for(String topic :topics) {
			ContainerProperties containerProps = new ContainerProperties(topic);
			containerProps.setMessageListener(listener);
			Map<String,Object> props = consumerProps(bootstrapServers, groupId, additionalProperties);
			DefaultKafkaConsumerFactory<String, String> cf= new DefaultKafkaConsumerFactory<>(props);
			ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(cf,
					containerProps);
			int threadCount = Runtime.getRuntime().availableProcessors();
			if(null != councurrencyNum) {
				try {
					threadCount = Integer.parseInt(councurrencyNum);
				}catch(Exception e) {
					log.error(String.format("Kafka concurrency num %s is inavlid use cpu core num instead", councurrencyNum));
				}
				
			}else {
				log.info("kafka concurrency num is not set use cpu core num instead");
			}
			container.setConcurrency(threadCount);
			container.setBeanName(CRFI);
			container.getContainerProperties().setAckMode(AckMode.MANUAL);
			container.getContainerProperties().setSyncCommits(true);
			container.setAutoStartup(false);
			
			containerList.add(container);
			if(null!= zkConfig.get(PropertyName.KAFKA_REQUEST_TIMEOUT)) {
				container.getContainerProperties().setShutdownTimeout(Integer.parseInt((zkConfig.get(PropertyName.KAFKA_REQUEST_TIMEOUT))));
			}
		}
		return containerList;
	}
	
	private Map<String, Object> consumerProps(String bootstrapServers, String groupId, Map<String,String> additionalProperties){
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,KafkaConst.MAX_FETCH_BYTES);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConst.AUTO_OFFSET_RESET);
		
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG	,StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		
		if(null!=zkConfig.get(PropertyName.KAFKA_REQUEST_TIMEOUT)) {
			props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, zkConfig.get(PropertyName.KAFKA_REQUEST_TIMEOUT));
			props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, zkConfig.get(PropertyName.KAFKA_REQUEST_TIMEOUT));			
		}
		
		boolean saslEnabled = Boolean.parseBoolean(zkConfig.get(PropertyName.KAFKA_SASL_ENALBLED));
		KafkaUtils.configAuthentication(props, saslEnabled);
		if(additionalProperties != null) {
			props.putAll(additionalProperties);
		}
		return props;
	}
	
	public KafkaMessageListener getListener() {
		return listener;
	}
	public void setListener(KafkaMessageListener listener) {
		this.listener=listener;
	}
	public List<ConcurrentMessageListenerContainer<String,String>> getContainer(){
		return containerList;
	}
	public void setContainer(List<ConcurrentMessageListenerContainer<String,String>> containerList) {
		this.containerList=containerList;
	}
}
