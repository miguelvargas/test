package com.mvwsolutions.common.service;

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
    public String setCurrentSsoSession(String sessionId);
    
    public void setCurrentUserIdNoSsoCheck(String userId);
    
    public String getCurrentUserId();
    
}
