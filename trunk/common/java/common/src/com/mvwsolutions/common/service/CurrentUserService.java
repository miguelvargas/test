package com.mvwsolutions.common.service;

import java.math.BigInteger;

/**
 * 
 * @author smineyev
 *
 */
public interface CurrentUserService {

    /**
     * checks if sso session id exists and if so set corresponding userId into thread context and returns the userId
     * @param sessionId sso session id
     * @return userId
     */
    public Long setCurrentSsoSession(BigInteger sessionId);
    
    public void setCurrentUserIdNoSsoCheck(Long userId);
    
    public Long getCurrentUserId();
    
}
