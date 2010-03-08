package com.mvwsolutions.common.exceptions;

@SuppressWarnings("serial")
public class AuthenticationException extends SecurityException {
	
	public AuthenticationException() {
	}
	
	public AuthenticationException(String msg) {
		super(msg);
	}
	

}
