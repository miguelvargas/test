package com.mvwsolutions.common.exceptions;

@SuppressWarnings("serial")
public class AuthorizationException extends SecurityException {
	
	public AuthorizationException() {
	}
	
	public AuthorizationException(String msg) {
		super(msg);
	}
	

}
