package com.mvwsolutions.common.web.view;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simpleframework.xml.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.View;

import com.mvwsolutions.common.Utils;
import com.mvwsolutions.common.exceptions.ApplicationException;
import com.mvwsolutions.common.exceptions.AuthenticationException;
import com.mvwsolutions.common.exceptions.AuthorizationException;
import com.mvwsolutions.common.exceptions.ErrorCodedException;
import com.mvwsolutions.common.service.CurrentUserService;
import com.mvwsolutions.common.web.xml.Response;

/**
 * 
 * @author smineyev
 * 
 */
public class BaseExceptionXmlView implements View {
	private static Logger logger = LoggerFactory
			.getLogger(BaseExceptionXmlView.class);

	private Serializer serializer;

	private CurrentUserService currentUserService;

	public CurrentUserService getCurrentUserService() {
		return currentUserService;
	}

	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public String getContentType() {
		return ("text/xml; charset=UTF-8");
	}

	@SuppressWarnings("unchecked")
	public void render(Map models, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws Exception {
		httpResponse.setContentType("text/xml; charset=UTF-8");
		httpResponse.setCharacterEncoding("UTF-8");

		Exception e = (Exception) models.get("exception");
		String message;
		Response res = new Response();
		res.setSuccess(false);

		boolean debugLog = false;
		if (e instanceof ErrorCodedException) {
			res.setErrorCode(((ErrorCodedException) e).getErrorCode());
			message = ((ErrorCodedException) e).getMessage(); 
			debugLog = true;
		} else if (e instanceof BindException) {
			BindException be = (BindException) e;
			List<FieldError> lfe = be.getFieldErrors();
			StringBuilder sb = new StringBuilder();
			for (FieldError fe : lfe) {
				sb.append(fe.getCodes()[1]);
				sb.append(",");
			}
			message = sb.toString();
			debugLog = true;
		} else if (e instanceof ApplicationException) {
			message = e.getMessage();
		} else if (e instanceof AuthenticationException
				|| e instanceof AuthorizationException) {
			message = e.getMessage();
			res.setAuthenticationFailed(true);
			debugLog = true;
		} else {
			message = Utils.errorToString(e);
		}
		if (currentUserService != null) {
			res
					.setRequestAuthenticated(currentUserService
							.getCurrentUserId() != null);
		}
		res.setError(message);

		if (debugLog) {
			logger.debug("Exception occured during http request ("
					+ httpRequest.getRequestURI() + ")", e);
		} else {
			logger.error("Exception occured during http request ("
					+ httpRequest.getRequestURI() + ")", e);
		}

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		serializer.write(res, byteOut);
		httpResponse.getOutputStream().write(byteOut.toByteArray());
	}
}
