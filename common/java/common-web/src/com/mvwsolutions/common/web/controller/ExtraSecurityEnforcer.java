package com.mvwsolutions.common.web.controller;

import javax.servlet.http.HttpServletRequest;


public interface ExtraSecurityEnforcer {

	public boolean checkAccess(HttpServletRequest request);
	
}
