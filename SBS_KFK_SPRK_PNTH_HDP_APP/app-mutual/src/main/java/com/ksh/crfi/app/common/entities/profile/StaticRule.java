package com.ksh.crfi.app.common.entities.profile;

import com.ksh.crfi.app.common.entities.EntityBase;

public class StaticRule extends EntityBase{

	private static final long serialVerisonUID =1L;
	
	private Float expectedFileSize;
	private Boolean usePreviousDayFileSize;
	private Float fileSizeWarning;
	private Float fileSizeError;
	private Integer expectedRowNum;
	private Boolean usePreviousDayRowNum;
	private Float rowNumError;
	private String expectedFileArriveTime;
	private Boolean usePreviousDayFileArriveTime;
	private Float fileArriveTimeWarning;
	private Float fileArriveTimeError;
}
