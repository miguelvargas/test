package com.mvwsolutions.common.exceptions;

/**
 * 
 * @author SMineyev
 *
 */
public class NotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 4802254980321741556L;

	public NotFoundException() {
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String s) {
		super(s);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

	public String getErrorCode() {
		return "notFound";
	}

}
