package com.ksh.crfi.app.kafka.dao;

@FunctionalInterface
public interface MessageHandler {

	boolean handleMessage(String topic, String key, String message)throws Exception;
}
