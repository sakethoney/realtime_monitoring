package com.ksh.crfi.app.hadoop.dao.aop;

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.security.UserGroupInformation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Log4j
public class UserGroupInformationAspect {
	@Autowired
	@Qualifier("getUserGroupInformation")
	private UserGroupInformation ugi;
	
	@Around("@annotation(userGroupInformational)")
	public Object runWithUserGroupInformation(ProceedingJoinPoint joinPoint) throws Exception {
		ugi.checkTGTAndReloginFromKeytab();
		return ugi.doAs((PrivilegedExceptionAction<Object>) () -> {
			Object innerResult = null;
			try {
				innerResult = joinPoint.proceed();
			}catch(Throwable e) {
				log.error("An error happens during UGI operation: ",e);
			}
			return innerResult;
		});
	}
}
