package com.ksh.crfi.app.springboot.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.log4j.Log4j;

@Log4j
public class WebServiceCallInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,Object obj, Exception exp)
	throws Exception{
		//lifecycle method
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HandlerMethod hm = (HandlerMethod)handler;
		log.info("Calling Function '"+hm.getMethod().getName()+" ' in class '"
				+hm.getBean().getClass().getSimpleName()+" ' at uri '"+request.getRequestURI()+" '");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerMethod hm = (HandlerMethod)handler;
		log.info("Finished function '"+hm.getMethod().getName()+" ' in class '"
				+hm.getBean().getClass().getSimpleName()+" ' at uri '"+request.getRequestURI()+" '");
		
	}

}
