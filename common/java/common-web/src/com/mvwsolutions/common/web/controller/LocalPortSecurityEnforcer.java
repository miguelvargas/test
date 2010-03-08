package com.mvwsolutions.common.web.controller;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;


public class LocalPortSecurityEnforcer implements ExtraSecurityEnforcer {

	private Set<Integer> allowedPorts = new HashSet<Integer>();

	public Set<Integer> getAllowedPorts() {
		return this.allowedPorts;
	}

	public void setAllowedPorts(Set<Integer> allowedPorts) {
		this.allowedPorts = allowedPorts;
	}

	public boolean checkAccess(HttpServletRequest request) {
		return getAllowedPorts().contains(request.getLocalPort());
	}

}
