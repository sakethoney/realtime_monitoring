package com.ksh.crfi.app.common.entities.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileProfile {

	private Integer id;
	private String fileBusinessType;
	private Integer minFileCount;
	private String fileNamePattern;
	private String folderToMonitor;
	private String clientId;
	private String fileType;
	private String separator;
	private String headerRowCount;
	private String footerRowCount;
	private String columnNameRowNumber;
	private String sheetName;
	private Boolean published;
	private Integer fileMetaDataId;
	
}
