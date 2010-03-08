package com.mvwsolutions.common.exceptions;

/**
 * 
 * @author SMineyev
 *
 */
public class AlreadyExistsException extends IllegalStateException {

	private static final long serialVersionUID = 6296208287733855096L;

	public AlreadyExistsException() {
	}

	public AlreadyExistsException(String s) {
		super(s);
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause);
	}

	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getErrorCode() {
		return "duplicate";
	}

}
