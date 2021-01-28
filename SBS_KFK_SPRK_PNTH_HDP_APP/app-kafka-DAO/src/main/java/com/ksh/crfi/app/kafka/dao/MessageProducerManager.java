package com.ksh.crfi.app.kafka.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.ksh.crfi.app.kafka.dao.constants.KafkaConst;
import com.ksh.crfi.app.kafka.dao.utils.KafkaUtils;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class MessageProducerManager {
	@Getter
	private final String topic;
	private Producer<String, String> producer;
	private Map<String, Object> properties;
	private final String bootStrapServerList;
	
	
	public MessageProducerManager(String bootStrapServerList, String topic, boolean saslEnabled) {
		log.info("Starting Prodcuder Manager");
		this.bootStrapServerList=bootStrapServerList;
		this.topic=topic;
		getBasicProperties(saslEnabled);
	}
	
	private void getBasicProperties(boolean saslEnabled) {
		properties = new HashMap<>();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServerList);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, KafkaConst.MAX_REQUEST_SIZE_VALUE);
		properties.put(ProducerConfig.RETRIES_CONFIG, KafkaConst.RETRIES);
		KafkaUtils.configAuthentication(properties, saslEnabled);
	}
	
	public void start() {
		producer = new KafkaProducer<>(properties);
	}
	
	public void close() {
		producer.close();
	}
	
	public void produceSync(String value) throws InterruptedException, ExecutionException {
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
		producer.send(record).get();
	}
	
	public void produceSync(String key, String value) throws InterruptedException, ExecutionException {
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
		producer.send(record).get();
	}
	
	public void produceAsync(String value) {
		ProducerRecord<String, String> record = new ProducerRecord<>(topic, value);
		producer.send(record,getErrorCallbackInstance());
	}
	
	private ErrorCallback getErrorCallbackInstance() {
		return new ErrorCallback();
	}
	
	public ErrorCallback produceAsync(String key, String value) {
		return new ErrorCallback();
	}
	
	public ErrorCallback produceAsync(String value, Callback callback) {
		return new ErrorCallback();
	}
	
	private class ErrorCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata recordMetadata, Exception exp) {
			if(exp != null) {
				log.error(String.format("Error producing message to topic : %s", topic), exp);
			}
		}
	}
	
}
