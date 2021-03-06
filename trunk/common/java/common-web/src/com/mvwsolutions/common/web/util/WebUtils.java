package com.mvwsolutions.common.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author SMineyev
 *
 */
public class WebUtils {

	public static String getCookieValue(HttpServletRequest httpRequest,
			String cookieName) {

		Cookie cookie = getCookie(httpRequest, cookieName);
		if (cookie != null)
			return cookie.getValue();
		return null;
	}

	public static Cookie getCookie(HttpServletRequest httpRequest,
			String cookieName) {

		final Cookie[] cookies = httpRequest.getCookies();

		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookieName.equals(cookie.getName())) {
				return cookie;
			}
		}

		return null;
	}

	public static Cookie setCookie(String name, String value,
			HttpServletRequest request, HttpServletResponse response,
			int expiration) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(expiration);
		cookie.setDomain(getDomain(request));
		response.addCookie(cookie);
		return cookie;
	}

	public static String getDomain(HttpServletRequest httpRequest) {
		String domain = httpRequest.getServerName();
		String[] parts = domain.split("\\.");
		if (parts.length < 2) {
			return domain;
		} else {
			domain = '.' + parts[parts.length - 2] + '.'
					+ parts[parts.length - 1];
			return domain;
		}

	}

}
