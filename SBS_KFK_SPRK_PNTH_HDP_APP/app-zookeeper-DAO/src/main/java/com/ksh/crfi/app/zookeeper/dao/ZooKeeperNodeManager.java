package com.ksh.crfi.app.zookeeper.dao;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.extern.log4j.Log4j;

@Log4j
public class ZooKeeperNodeManager {

	@Autowired
	private ZooKeeper zooKeeper;
	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private ZookeeperConfigurationManager zkConfig;
	private final String rootNode;
	
	@Autowired
	public ZooKeeperNodeManager(LocalConfigurationManager localConfig) {
		rootNode = localConfig.get(PropertyName.ZOOKEEPER_ROOT);
	}
	
	public String registerService(PropertyName serviceNode, String value) {
		String path = String.format("%s%s", rootNode,serviceNode);
		String parentNode = getParentPath(path);
		if(parentNode != null) {
			createPersistentNode(parentNode);
		}
		return createEphemeralNode(path, value, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	public List<String> getRegisteredServices(PropertyName serviceNode){
		List<String> processes = Lists.newArrayList();
		try {
			String path = String.format("%s%s", rootNode, serviceNode);
			List<String> nodes = zooKeeper.getChildren(path,false);
			for(String node : nodes) {
				String fullPath = String.format("%s%s", serviceNode,node);
				processes.add(zkConfig.get(fullPath));
			}
		}catch(Exception e) {
			log.error("Failed to get registered service nodes in zookeeper: "+serviceNode, e);
		}
		return processes;
	}
	
	public List<String> getChildrenNodes(PropertyName serviceNode, WatcherAction watcherAction){
		String path = String.format("%s%s", rootNode,serviceNode);
		try {
				return zooKeeper.getChildren(path,appContext.getBean(ChildrenNodeWatcher.class, zooKeeper, path, watcherAction));
		}catch(Exception e) {
			log.error("Failed to get chidren nodes in zookeeper "+serviceNode, e);
			return Lists.newArrayList();
		}
	}
	
	public NodeUpdatedWatcher getNewNodeUpdatedWatcher(PropertyName propertyName, WatcherAction watcherAction) {
		String path = String.format("%s%s", rootNode, propertyName);
		return new NodeUpdatedWatcher(zooKeeper, path, watcherAction);
	}
	
	public void createPersistentNode(String path) {
		try {
			if(zooKeeper.exists(path, true)== null) {
				String parentPath = getParentPath(path);
				if(parentPath != null && zooKeeper.exists(parentPath, true)==null) {
					createPersistentNode(getParentPath(path));
				}
				zooKeeper.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		}catch(Exception e) {
			log.error("Failed to create persistent zookeepr node" + path , e);
		}
	}
	
	public String createEphemeralNode(String path, String data, CreateMode createMode) {
		String node = null;
		try {
			if(zooKeeper.exists(path, false)== null) {
				node = zooKeeper.create(path, data.getBytes(), Ids.READ_ACL_UNSAFE, createMode);
			}
		}catch(Exception e) {
			log.error("Failed to create ephemeral zookeeper ndoe" + path, e);
		}
		return node;
	}
	
	private String getParentPath(String nodepath) {
		int p = nodepath.lastIndexOf('/');
		if(p <= 0) {
			return null;
		}else {
			return nodepath.substring(0, p);
		}
	}
}
