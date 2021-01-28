package com.ksh.crfi.windows.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WaffleConfig {
	@Bean
	public WindowsAuthProviderImpl waffleWindowsAuthProvider() {
		
		return new WindowsAuthProviderImpl();
	}
}
