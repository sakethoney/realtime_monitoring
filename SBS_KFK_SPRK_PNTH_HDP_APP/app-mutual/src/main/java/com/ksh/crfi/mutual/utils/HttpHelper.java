package com.ksh.crfi.mutual.utils;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import lombok.extern.log4j.Log4j;

@Log4j
public class HttpHelper {

	private static final HttpHelper instance = new HttpHelper();
	
	private HttpHelper() {
		
	}
	
	public static HttpHelper getInstance() {
		return instance;
	}
	
	public String get(String urlString) {
		String result="";
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			Utils.convertStreamToString(conn.getInputStream());
		}catch(Exception e) {
			log.error("Get method failed: "+urlString, e);
		}
		return result;
	}
	
	public String post(String urlString, String postContent) {
		try {
			 URL url = new URL(urlString);
			 HttpURLConnection conn =(HttpURLConnection) url.openConnection();
			 conn.setRequestMethod("POST");
			 conn.setRequestProperty("Content-Type", "application/json");
			 conn.setRequestProperty("Accept", "application/json");
			 conn.setRequestProperty("Content-Length", String.valueOf(postContent.length()));
			 conn.setDoOutput(true);
			 
			 try(OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),StandardCharsets.UTF_8)){
				 out.write(postContent);
				 out.flush();
				 out.close();
			 }
			 return Utils.convertStreamToString(conn.getInputStream());
		}catch(Exception e) {
			return "Post method failed: "+urlString +e.getMessage();
		}
	}
	
	public String decodeContent(String content) {
		String decodeContent = content;
		try {
			decodeContent = URLDecoder.decode(content, StandardCharsets.UTF_8.toString());
		}catch(UnsupportedEncodingException e) {
			log.error(String.format("Decode the content '%1s' failed", content), e);
		}
		return decodeContent;
	}
}
