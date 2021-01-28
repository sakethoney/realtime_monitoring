package com.ksh.crfi.mutual.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.ksh.crfi.mutual.constants.ApplicationConstants;

import lombok.extern.log4j.Log4j;

@Log4j
public class Utils {

	static String domainName;
	
	private Utils() {

	}

	public static String convertStreamToString(InputStream inputStream) {
		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

	public static String convertStreamToQuotedString(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public static long convertByteArrayToLong(byte[] bts) {
		long result = 0;
		if (bts != null) {
			ByteBuffer bb = ByteBuffer.wrap(bts);
			result = bb.getLong();
		}
		return result;
	}

	public static byte[] convertLongToByteArray(long value) {

		ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
		bb.putLong(0, value);
		return bb.array();
	}

	public static String getDomainName() throws UnknownHostException {
		if (domainName == null) {
			InetAddress addr = InetAddress.getLocalHost();
			String canonicalHostName = addr.getCanonicalHostName();
			int index;
			if ((index = canonicalHostName.indexOf('.')) != -1) {
				domainName = canonicalHostName.substring(index + 1);
			}
		}
		return domainName;
	}

	public static boolean compareDoubleEquals(double violationA, double violationB, long maxUlps) {
		long expectedBits = Double.doubleToLongBits(violationA) < 0
				? 0x8000000000000000L - Double.doubleToLongBits(violationA)
				: Double.doubleToLongBits(violationA);

		long actualBits = Double.doubleToLongBits(violationB) < 0
				? 0x8000000000000000L - Double.doubleToLongBits(violationB)
				: Double.doubleToLongBits(violationB);

		long diff = expectedBits > actualBits ? expectedBits - actualBits : actualBits - expectedBits;

		Double gaps = Math.max(Math.abs(violationA), Math.abs(violationB)) * 1E-15;

		return !Double.isNaN(violationA) && !Double.isNaN(violationB)

				&& (diff <= maxUlps || Math.abs(violationA - violationB) < gaps);

	}

	public static boolean compareDoubleEquals(double violationA, double violationB) {
		return compareDoubleEquals(violationA, violationB, 48);
	}

	public static File getFile(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return new File("NotExistFile");
		}
		File file = new File(path);
		if (file.exists()) {
			return file;
		}

		String fileName = getFileNameFromPath(path);
		file = new File(fileName);
		if (file.exists()) {
			return file;
		}

		file = getLookupFile(getApplicationPath(), fileName);
		if (file != null && file.exists()) {
			return file;
		}

		file = getLookupFile(getDeploymentPath(), fileName);
		if (file != null && file.exists()) {
			return file;
		}

		String pathInConfig = "../config/" + fileName;
		file = new File(pathInConfig);
		return file;
	}

	public static String getFileNameFromPath(String path) {
		String fileName = path;

		if (fileName.lastIndexOf('\\') > 0) {
			fileName = fileName.substring(fileName.lastIndexOf('\\') + 1);
		}
		if (fileName.lastIndexOf('/') > 0) {
			fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
		}
		return fileName;
	}

	public static File getApplicationPath() {
		String path = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (file.isFile()) {
			file = file.getParentFile();
		}
		return file.getParentFile();
	}

	public static File getDeploymentPath() {
		return getApplicationPath().getParentFile();
	}

	public static File getLookupFile(File basePath, String filenName) {
		if (basePath.isFile()) {
			basePath = basePath.getParentFile();
		}
		if (basePath == null) {
			return null;
		}
		File[] files = basePath.listFiles();
		if (files == null) {
			return null;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				return getLookupFile(file, filenName);
			} else if (file.isFile()) {
				if (file.getName().equalsIgnoreCase(filenName)) {
					return file;
				}
			}
		}
		return null;
	}

	public static long convertDateToLong(String date, DateTimeFormatter dtf) {
		LocalDate ld = LocalDate.parse(date, dtf);
		return ld.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String getParentPath(String nodepath) {
		int p = nodepath.lastIndexOf('/');
		if (p <= 0) {
			return null;
		} else {
			return nodepath.substring(0, p);
		}
	}

	public static String decryptValue(String value) {
		if(Strings.isNullOrEmpty(value)) {
			return value;
		}
		try {
				IvParameterSpec iv = new IvParameterSpec(
						ApplicationConstants.DEFAULT_AES_VECTOR.getBytes(ApplicationConstants.UTF_8));
				SecretKeySpec skeySpec = new SecretKeySpec(
						ApplicationConstants.DEFAULT_AES_KEY.getBytes(ApplicationConstants.UTF_8),"AES");
				Cipher  cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);
				byte[] original = cipher.doFinal(Base64.getDecoder().decode(value));
				return new String(original);
		}catch(Exception e) {
			log.warn("Cannot decrypt value");
		}
		return value;
	}
	
	public static String paddingLongValue(Long l) {
		long v = l==null ?0l : l;
		return String.format("%019d", v);
	}
	
	public static SimpleDateFormat getISODateTimeWithTimeZoneFormat() {
		return new SimpleDateFormat(ApplicationConstants.ISO_DATETIME_TIMEZONE_FORMATTER);
	}
	
	public static String getfileContent(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path	)));
	}
	
	public static void setSaslConfiguration() {
		File file = getFile(ApplicationConstants.JAAS_CONF_FILE);
		if(file.exists()) {
			System.setProperty(ApplicationConstants.JAAS_CONF_PROPERTY,file.getAbsolutePath());
			log.info("Set JAAS conf as : "+file.getAbsolutePath());
		}
		
		file = getFile(ApplicationConstants.KRB5_CONF_FILE);
		if(file.exists()) {
			System.setProperty(ApplicationConstants.KRB5_CONF_PROPERTY,file.getAbsolutePath());
			log.info("set KRB5 conf as :"+ file.getAbsolutePath());
		}
	}
	
	public static void customizeLog4j() {
		Logger.getLogger("org.apache.zookeeper.ClientCnxn").setLevel(Level.ERROR);
		Logger.getLogger("org.apache.zookeeper.Zookeeper").setLevel(Level.WARN);
		Logger.getLogger("org.apache.hadoop.hbase.xookeeper.RecoverableZookeeper").setLevel(Level.WARN);
		Logger.getLogger("org.apache.hadoop.hbase.client.ConnectionManager$HconnectionImplementation").setLevel(Level.WARN);
	}
	
	public static String getIpAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}catch(UnknownHostException e) {
			log.error("Failed to get IP address", e);
		}
		return null;
	}
	
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		}catch(UnknownHostException e) {
			log.error("Failed to get host name ",e);
		}
		return null;
	}
	
}
