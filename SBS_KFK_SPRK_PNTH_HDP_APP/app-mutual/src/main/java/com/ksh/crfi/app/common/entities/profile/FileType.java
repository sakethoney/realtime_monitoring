package com.ksh.crfi.app.common.entities.profile;

public enum FileType {

	CSV("csv"),
	EXCEL("excel"),
	FIXED_LENGTH("fixedlenght");
	private String fileType;
	private FileType(String fileType) {
		this.fileType=fileType;
	}
	public String getFileType() {
		return fileType;
	}
}
