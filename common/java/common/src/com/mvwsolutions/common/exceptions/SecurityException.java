package com.mvwsolutions.common.exceptions;

@SuppressWarnings("serial")
public class SecurityException extends RuntimeException {
	
	public SecurityException() {
	}
	
	public SecurityException(String msg) {
		super(msg);
	}

}
