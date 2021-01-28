package com.ksh.crfi.app.common.entities;

import javax.persistence.Column;

public class DeleteMarkEntity extends EntityBase{

	private static final long serialVersionUID =1L;
	@Column
	private Boolean deleted;
}
