package com.ksh.crfi.app.kafka.dao;

public interface MessageHandlerFactory {

	MessageHandler getMessageHandler(String topic);
}
