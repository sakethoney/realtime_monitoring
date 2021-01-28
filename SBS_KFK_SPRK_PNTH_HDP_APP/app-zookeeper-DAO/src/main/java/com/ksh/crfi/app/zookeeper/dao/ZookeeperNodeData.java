package com.ksh.crfi.app.zookeeper.dao;

import java.util.List;

public class ZookeeperNodeData {

	String znodePath;
	String znodeData;
	List<String> zchildnodeList;
	public String getZnodePath() {
		return znodePath;
	}
	public void setZnodePath(String znodePath) {
		this.znodePath = znodePath;
	}
	public String getZnodeData() {
		return znodeData;
	}
	public void setZnodeData(String znodeData) {
		this.znodeData = znodeData;
	}
	public List<String> getZchildnodeList() {
		return zchildnodeList;
	}
	public void setZchildnodeList(List<String> zchildnodeList) {
		this.zchildnodeList = zchildnodeList;
	}
}
