package com.ksh.crfi.app.springboot.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfigBase {
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
