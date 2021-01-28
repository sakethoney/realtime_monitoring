package com.ksh.crfi.app.springboot.common.admin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;


@Target({ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@ComponentScan(basePackages = {"com.ksh.app.springboot.common.admin"})
public @interface EnableSpringBootAdminClient {

}
