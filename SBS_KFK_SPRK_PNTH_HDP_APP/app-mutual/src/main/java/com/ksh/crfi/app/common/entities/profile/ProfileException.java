package com.ksh.crfi.app.common.entities.profile;

public class ProfileException extends Exception {

	private static final long serialVersionUID=1L;
	
	public ProfileException() {
		this(ExceptionConst.FAILED_TO_SAVE_ENTRY_TO_DB);
	}
	public ProfileException(String message) {
		super(message);
	}
	public ProfileException(Throwable cause) {
		super(cause);
	}
	
}
