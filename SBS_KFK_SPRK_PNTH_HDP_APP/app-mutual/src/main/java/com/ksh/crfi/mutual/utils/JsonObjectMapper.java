package com.ksh.crfi.mutual.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.ksh.crfi.mutual.constants.ApplicationConstants;

import lombok.extern.log4j.Log4j;

@Log4j
public class JsonObjectMapper {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Map<String, String> jsonStringcache = new ConcurrentLinkedHashMap.Builder<String, String>()
			.maximumWeightedCapacity(100).build();
	
	
	private static final String ERROR_WHEN_CREATING ="Error when creating ";
	
	private JsonObjectMapper() {
		
	}
	
	static {
		objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES	, true);
	}
	
	public static <T> T readValue(String content, Class<T> valueType){
		try {
			return readValueThrowing(content, valueType);
		}catch(IOException e) {
			log.error(ERROR_WHEN_CREATING+valueType.getName()+" object from json: "+content, e);
			return null;
		}
	}
	
	public static <T> T readValue(String content, TypeReference<T> valueType) {
		try {
			 return readValueThrowing(content, valueType);
		}catch(IOException e) {
			log.error(ERROR_WHEN_CREATING+ valueType + " object from json: "+content, e);
			return null;
		}
	}
	
	public static <T> T readValueThrowing(String content, Class<T> valueType) throws IOException{
		return objectMapper.readValue(content, valueType);
	}
	
	
	public static <T> T readValueThrowing(String content, TypeReference<T> valueType) throws IOException{
		return objectMapper.readValue(content, valueType);
	}
	
	public static String writeValueAsString(Object value) {
		try {
			 return objectMapper.writeValueAsString(value);
		}catch(JsonProcessingException e) {
			log.error("Error when converting "+ value+" object to Json ",e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T cloneValue(T value) {
		if(value == null) {
			return null;
		}
		return (T) readValue(writeValueAsString(value), value.getClass());
	}
	
	public static JsonNode readTree(String content) {
		try {
			return objectMapper.readTree(content);
		}catch(IOException e) {
			log.error(ERROR_WHEN_CREATING+" JsonNode object from json: "+ content, e);
			return null;
		}
	}
	
	public static <T> T treeToValue(JsonNode jsonNode, Class<T> valueType) {
		if(jsonNode == null) {
			return null;
		}
		try {
				return objectMapper.treeToValue(jsonNode, valueType);
		}catch(JsonProcessingException e) {
			log.error(ERROR_WHEN_CREATING+valueType.getName()+" object from json Node", e);
			return null;
		}
	}
	
	public static <T> T readFromFile(String fileName, Class<T> valueType)	{
		String fileFullPath = null;
		
		if(new File(fileName).exists()) {
			fileFullPath = fileName;
		}
		if(new File(ApplicationConstants.RESOURCES_PATH + fileName).exists()) {
			fileFullPath = ApplicationConstants.RESOURCES_PATH + fileName;
		}
		if(new File(ApplicationConstants.TEST_DATA_PATH + fileName).exists()) {
			fileFullPath = ApplicationConstants.TEST_DATA_PATH + fileName;
		}
		if(fileFullPath == null){
			return null;
		}
		
		String json= jsonStringcache.computeIfAbsent(fileFullPath, p ->{
			try {
				return Utils.getfileContent(p);
			}catch(IOException e) {
				log.error(ERROR_WHEN_CREATING + valueType.getName() + " object from path: "+ p, e);
			return null;	
			}
		});
		return readValue(json,valueType);
	}
}
