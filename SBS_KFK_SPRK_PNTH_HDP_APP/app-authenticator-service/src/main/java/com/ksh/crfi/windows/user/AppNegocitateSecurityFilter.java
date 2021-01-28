package com.ksh.crfi.windows.user;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.extern.log4j.Log4j;
@Log4j
class AppNegocitateSecurityFilter extends NegotiateSecurityFilter {

	public AppNegocitateSecurityFilter() {
		super();
		log.debug("Waffle.spring.negotiateSecurityFilter loaded");
	}
	@Override
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
		super.doFilter(req, res, chain);
	}
}
