package com.mvwsolutions.common.exceptions;

@SuppressWarnings("serial")
public class ApplicationException extends RuntimeException {

	/**
	 * We can create this exception only if we can specify human-readable
	 * message since this message is intended to be displayed by UI to user
	 * 
	 * @param msg human-readabl message
	 */
	public ApplicationException(String msg) {
		super(msg);
	}
}
