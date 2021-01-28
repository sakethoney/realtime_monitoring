package com.ksh.crfi.app.zookeeper.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.ksh.crfi.app.zookeeper.dao.ZookeeperWatcher.ZookeeperListener;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;
import com.ksh.crfi.app.zookeeper.dao.exception.ZookeeperConfigurationException;
import com.ksh.crfi.mutual.constants.ApplicationConstants;
import com.ksh.crfi.mutual.utils.Utils;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class ZookeeperConfigurationManager implements ZookeeperListener {

	@Autowired
	private LocalConfigurationManager localConfig;
	private Map<String, String> configItems;
	private boolean configItemsLoaded = false;
	@Autowired
	private ZooKeeper zookeeper;
	private int retryTimes = 30;
	private static final int RECONNECT_SLEEPTIME = 5000;

	public Map<String, String> getConfigurationCache() throws ZookeeperConfigurationException {
		if (configItemsLoaded) {
			return configItems;
		}
		synchronized (ZookeeperConfigurationManager.class) {
			if (configItemsLoaded) {
				return configItems;
			}
			configItems = new HashMap<>();
			readZookeeperConfigUntilSucceed();
			configItems.forEach((k, v) -> log.info(String.format("Key:[%s] value : [%s]", k, v)));
			configItemsLoaded = true;
		}
		return configItems;
	}

	private void readZookeeperConfigUntilSucceed() throws ZookeeperConfigurationException {
		while (!readConfigurationFromZookeeper() && retryTimes-- > 0) {
			try {
				Thread.sleep(RECONNECT_SLEEPTIME);
			} catch (InterruptedException e) {
				log.error("Read configurations from Zookeeper thread interrupted", e);
				Thread.currentThread().interrupt();
			}
		}
		if (retryTimes <= 0) {
			throw new ZookeeperConfigurationException("Failed to read configurations from zookeeper");
		}
	}

	public void fetchConfigurationsFromZookeeper(Boolean watch) throws ZookeeperConfigurationException {
		getConfigurationCache();
		if (watch) {
			watchChanges();
		}
	}

	private boolean readConfigurationFromZookeeper() {
		String rootNode = localConfig.get(PropertyName.ZOOKEEPER_ROOT);
		try {
			configItems.clear();
			if (zookeeper.exists(rootNode, false) != null) {
				if ((new String(zookeeper.getData(rootNode, null, null))).compareTo("1") != 0) {
					log.info("Configurations are not available on zookeeper, wait to retry");
					return false;
				}
				readAllPathValues(rootNode);
			}
			appendNewConfigFromDefaultConfigFile(rootNode);
			return true;
		} catch (Exception e) {
			log.error("Failed to read configuration from Zookeeper", e);
			return false;
		}
	}

	private void readAllPathValues(String startingPath) throws Exception {

		try {
			byte[] valueBytes = zookeeper.getData(startingPath, null, null);
			String value = valueBytes == null ? "" : new String(valueBytes);

			configItems.put(startingPath, value);
			if (startingPath.indexOf(PropertyName.TIMEZONE.toString()) != -1) {
				String timeZone = new String(zookeeper.getData(startingPath, null, null));
				//setupTimeZone(timeZone);
			}
			for (String subPath : zookeeper.getChildren(startingPath, false)) {
				readAllPathValues(startingPath + "/" + subPath);

			}
		} catch (Exception e) {
			log.error("Failed to read all sub nodes in zookeeper:" + startingPath, e);
		}
	}

	public List<String> getSubNodeNames(PropertyName parentNode) {
		return getSubNodeNames(parentNode.toString());
	}
	public List<String> getSubNodeNames(String parentNode) {
		String startingPath = localConfig.get(PropertyName.ZOOKEEPER_ROOT) + parentNode;
		ArrayList<String> nodeNames = new ArrayList<>();
		try {
			if(zookeeper.exists(startingPath, false)==null) {
				return nodeNames;
			}
			for (String subPath : zookeeper.getChildren(startingPath, false)) {
				nodeNames.add(subPath);

			}
		} catch (Exception e) {
			log.error("Failed to read all sub nodes in zookeeper :" + startingPath, e);
		}
		return nodeNames;
	}

	private void setupTimezone(String timezone) {
		/*if (!Strings.isNullOrEmpty(timezone)) {
			TimeZone.setDefault(TimeZone.getTimeZone(timezone.trim()));
			@SuppressWarnings("unchecked")
			Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = appenders.nextElement();
				PatternLayout layout = (PatternLayout) appender.getLayout();
				String oldPattern = layout.getContentType();
				int dateIndex = oldPattern.indexOf("%d{");
				if (dateIndex != -1) {
					int endIndex = oldPattern.indexOf("}", dateIndex);
					String oldTime = oldPattern.substring(dateIndex, endIndex + 1);
					layout.setConversionPattern(oldPattern.replace(oldTime, oldTime + "{" + timezone + "}"));
				}
			}
		}*/
	}

	public String get(String configName) {
		try {
			String value = getConfigurationCache().get(localConfig.get(PropertyName.ZOOKEEPER_ROOT) + configName);
			if (configName.contains("password")) {
				value = Utils.decryptValue(value);
			}
			return value;
		} catch (ZookeeperConfigurationException e) {
			log.error("Failed to get configuration value : " + configName, e);
			return null;
		}
	}

	public String get(PropertyName configName) {
		return get(configName.toString());
	}

	public String getFromServer(PropertyName configName) {
		try {
			String path = String.format("%s%s", localConfig.get(PropertyName.ZOOKEEPER_ROOT), configName);
			String value;
			value = new String(zookeeper.getData(path, false, null));
			if (path.contains("password")) {
				value = Utils.decryptValue(value);
			}
			return value;
		} catch (Exception e) {
			log.error("Failed to get zookeeper ndoe from server." + e);
			return null;
		}

	}

	public String get(PropertyName configName, String defaultValue) {
		String result = get(configName);
		if (Strings.isNullOrEmpty(result)) {
			return defaultValue;
		}
		return result;
	}

	public void delete(String zNode) throws KeeperException {
		try {
			for (String subZNode : zookeeper.getChildren(zNode, true)) {
				delete(zNode + "/" + subZNode);
			}
		} catch (InterruptedException e) {
			log.error("Zookeeper delete data thread interrupted", e);
			Thread.currentThread().interrupt();
		}
		try {
			zookeeper.delete(zNode, -1);
		} catch (InterruptedException e) {
			log.error("Zookeeper delete data thread interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void nodeChanged(String nodeName, String nodeValue) {
		log.info("Node" + nodeName + "changed, new value is " + nodeValue);
	}

	protected void watchChanges() {
		try {
			ZookeeperWatcher zkWatcher = new ZookeeperWatcher(localConfig.get(PropertyName.ZOOKEEPER_URL),
					localConfig.get(PropertyName.ZOOKEEPER_ROOT));
			zkWatcher.registerExternalListener(this);
			Thread zookeeperWatchTread = new Thread(zkWatcher);
			zookeeperWatchTread.start();
		} catch (IOException e) {
			log.error("Failed to watch changes in Zookeeper", e);
		}
	}

	public LocalConfigurationManager getLocalConfig() throws ZookeeperConfigurationException {
		return localConfig;
	}

	public void setLocalConfig(LocalConfigurationManager localConfig) throws ZookeeperConfigurationException {
		this.localConfig = localConfig;
	}

	private void appendNewConfigFromDefaultConfigFile(String rootNode) {
		Properties defaultConfig = loadDefaultConfig();
		if (defaultConfig == null) {
			return;
		}
		List<String> newConfigItemList = defaultConfig.keySet().stream().filter(k -> !configItems.containsKey(k))
				.map(p -> p.toString()).collect(Collectors.toList());

		for (String key : newConfigItemList) {
			if (!rootNode.equals(getRootNodeName(key))) {
				log.warn(String.format(
						"Failed to create new config node : %s, root name is not match with properties file", key));
				continue;
			}
			createOrUpdateConfigNode(key, defaultConfig.get(key).toString());
		}

	}

	private String getRootNodeName(String nodePath) {
		if (nodePath == null) {
			return null;
		}
		String[] stringArray = nodePath.split("/");
		if (stringArray == null || stringArray.length < 2) {
			return null;
		}

		return "/" + stringArray[1];
	}

	public Properties loadDefaultConfig() {

		File file = Utils.getFile(ApplicationConstants.ZOOKEEPER_DEFAULT_CONFIG_FILE);
		if (!file.exists()) {
			log.info(ApplicationConstants.ZOOKEEPER_DEFAULT_CONFIG_FILE + "file not found");
			return null;
		}
		try {
			InputStream inStream = new FileInputStream(file.getAbsolutePath());
			Properties defaultConfig = new Properties();
			defaultConfig.load(inStream);
			return defaultConfig;
		} catch (Exception e) {
			log.error("Failed to load Zookeeper default configurations from file:"
					+ ApplicationConstants.ZOOKEEPER_DEFAULT_CONFIG_FILE, e);
		}
		return null;
	}

	public void set(PropertyName propertyName, String value) {
		String path = String.format("%s%s", localConfig.get(propertyName.ZOOKEEPER_ROOT), propertyName);
		createOrUpdateConfigNode(path, value);
	}

	private void createOrUpdateConfigNode(String path, String value) {
		try {
			if (zookeeper.exists(path, true) == null) {
				String parentPath = Utils.getParentPath(path);
				if (parentPath != null && zookeeper.exists(parentPath, true) == null) {
					createOrUpdateConfigNode(Utils.getParentPath(path), "");
				}
				zookeeper.create(path, value.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				configItems.put(path, value);
			} else if (!value.isEmpty()) {
				zookeeper.setData(path, value.getBytes(), -1);
			}
		} catch (Exception e) {
			log.error("Failed to create or update Zookeeper node: " + path, e);
		}
	}

	/*private void setupTimeZone(String timezone) {
		if (!Strings.isNullOrEmpty(timezone)) {
			TimeZone.setDefault(TimeZone.getTimeZone(timezone.trim()));
			@SuppressWarnings("unchecked")
			Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
			while (appenders.hasMoreElements()) {
				Appender appender = appenders.nextElement();
				PatternLayout layout = (PatternLayout) appender.getLayout();
				String oldPattern = layout.getConversionPattern();
				int dateIndex = oldPattern.indexOf("%d{");
				if (dateIndex != -1) {
					int endIndex = oldPattern.indexOf('}', dateIndex);
					String oldTime = oldPattern.substring(dateIndex, endIndex + 1);
					layout.setConversionPattern(oldPattern.replace(oldTime, oldTime + "{" + timezone + "}"));
				}

			}
		}
	}*/
}
