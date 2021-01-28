package com.ksh.crfi.app.common.entities.profile;

import com.ksh.crfi.app.common.entities.EntityBase;

public class Content extends EntityBase {
	private static final long serialVersionUID =1L;
	private String name;
	private String type;
	private String format;
	private Integer position=0;
	private Integer length=0;
	private String description;
	private String error;
}
