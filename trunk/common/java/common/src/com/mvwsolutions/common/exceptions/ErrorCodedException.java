package com.mvwsolutions.common.exceptions;


/**
 * 
 * @author smineyev
 * 
 */
public class ErrorCodedException extends ApplicationException {

    private static final long serialVersionUID = 9089228133262951666L;

    private String errorCode;

    public ErrorCodedException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
    
    public ErrorCodedException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
