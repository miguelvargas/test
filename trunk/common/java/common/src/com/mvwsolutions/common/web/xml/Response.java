package com.mvwsolutions.common.web.xml;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 * @author smineyev
 *
 */
@Root
public class Response implements Serializable {
    private static final long serialVersionUID = -56270744398174964L;

    @Attribute
    private boolean success = true;

    @Attribute (required=false)
    private String errorCode;
    
    @Element (required=false)
	private String error;

    @Element (required=false)
	private Object resultObject;

    @Attribute
	private boolean authenticationFailed = false;
	
    @Attribute
	private boolean requestAuthenticated = false;
	
    @Attribute (required=false)
	private long requestDuration;
	
	
	public boolean isRequestAuthenticated() {
		return requestAuthenticated;
	}

	public void setRequestAuthenticated(boolean requestAuthenticated) {
		this.requestAuthenticated = requestAuthenticated;
	}

	public long getRequestDuration() {
		return requestDuration;
	}

	public void setRequestDuration(long requestDuration) {
		this.requestDuration = requestDuration;
	}

	public boolean isAuthenticationFailed() {
		return authenticationFailed;
	}

	public void setAuthenticationFailed(boolean authenticationFailed) {
		this.authenticationFailed = authenticationFailed;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
}
