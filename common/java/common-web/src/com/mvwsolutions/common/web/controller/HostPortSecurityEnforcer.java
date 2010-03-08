package com.mvwsolutions.common.web.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

public class HostPortSecurityEnforcer implements ExtraSecurityEnforcer {

	private Set<String> _remoteHostWhiteList = new HashSet<String>();
	private Set<Integer> _localPortWhiteList = new HashSet<Integer>();
	

	public void setLocalPortWhiteList(String localPortWhiteList) {
		if (localPortWhiteList != null) {
			StringTokenizer st = new StringTokenizer(localPortWhiteList, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				_localPortWhiteList.add(new Integer(s));
			}
		}
	}

	public void setRemoteHostWhiteList(String remoteHostWhiteList) {
		if (remoteHostWhiteList != null) {
			StringTokenizer st = new StringTokenizer(remoteHostWhiteList, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				_remoteHostWhiteList.add(s);
			}
		}	
	}

	public boolean checkAccess(HttpServletRequest request) {
		String remoteIp = request.getRemoteAddr();
		if (_remoteHostWhiteList.size() != 0) {
			if (!_remoteHostWhiteList.contains(remoteIp)) {
				return false;
			}
		}
		int localPort = request.getLocalPort();
		if (_localPortWhiteList.size() != 0 && !_localPortWhiteList.contains(localPort)) {
			return false;
		}
		return true;
	}
	

}
