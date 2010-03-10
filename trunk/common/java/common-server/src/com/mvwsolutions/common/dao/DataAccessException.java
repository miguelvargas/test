package com.mvwsolutions.common.dao;

/**
 * 
 * Data access exception for Dao classes
 * 
 * @author marcus
 *
 */

public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = -1439041917869717743L;
    
    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(Throwable e) {
        super(e);
    }
    
    public DataAccessException(String msg, Throwable e) {
        super(msg, e);
    }
}

