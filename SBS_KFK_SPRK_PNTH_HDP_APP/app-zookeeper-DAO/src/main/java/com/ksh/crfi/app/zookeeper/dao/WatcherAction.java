package com.ksh.crfi.app.zookeeper.dao;

@FunctionalInterface
public interface WatcherAction {

	void call(String path);
}
