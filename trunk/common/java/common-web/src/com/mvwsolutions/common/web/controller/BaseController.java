package com.mvwsolutions.common.web.controller;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.mvwsolutions.common.exceptions.AuthorizationException;
import com.mvwsolutions.common.service.CurrentUserService;
import com.mvwsolutions.common.web.util.WebUtils;
import com.mvwsolutions.common.web.view.GenericXmlView;

public abstract class BaseController<C> extends AbstractCommandController {

    public static final String SESSID_COOKIE_NAME = "MVW_DROIDADTE_SESSION_ID";

    public static final String SESSID_QUERY_NAME = SESSID_COOKIE_NAME;

    protected boolean allowQuerySession = true;

    protected CurrentUserService currentUserService;
    private String             successView = GenericXmlView.VIEW_NAME ;
    private String             failureView;
    private String             formView;

    private Collection<ExtraSecurityEnforcer> extraSecurityEnforcers;

    protected BaseController(Class<C> cmdClass) {
        super(cmdClass);
        setCommandName(GenericXmlView.OBJECT_MODEL);
    }

    @SuppressWarnings("unchecked")
    public Class<C> getCmdClass() {
        return getCommandClass();
    }

    public Collection<ExtraSecurityEnforcer> getExtraSecurityEnforcers() {
        return extraSecurityEnforcers;
    }

    public void setExtraSecurityEnforcers(
            Collection<ExtraSecurityEnforcer> extraSecurityEnforcers) {
        this.extraSecurityEnforcers = extraSecurityEnforcers;
    }

    public CurrentUserService getCurrentUserService() {
        return currentUserService;
    }

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public boolean isAllowQuerySession() {
        return allowQuerySession;
    }

    public void setAllowQuerySession(boolean allowQuerySession) {
        this.allowQuerySession = allowQuerySession;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModelAndView handle(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse, Object command,
            BindException errors) throws Exception {

        final long startTime = System.currentTimeMillis();

        preventCaching(httpResponse);
        if (extraSecurityEnforcers != null) {
            for (ExtraSecurityEnforcer extraSecurityEnforcer : extraSecurityEnforcers) {
                if (!extraSecurityEnforcer.checkAccess(httpRequest)) {
                    logger.warn("Access denied (remoteHost"
                            + httpRequest.getRemoteAddr() + ":"
                            + httpRequest.getRemotePort() + ", enforced by: '"
                            + extraSecurityEnforcer.toString() + "')");
                    throw new AuthorizationException("Access denied");
                }
            }
        }

        if (errors.getErrorCount() > 0) {
            throw new BindException(errors);
        }

        final C cmd = getCmdClass().cast(command);
        boolean requestAuthenticated;
        boolean authenticationFailed = false;
        ModelAndView res;

        if (!authenticationRequired()) {
            requestAuthenticated = false;
            res = handleAuthenticatedRequest(httpRequest, httpResponse, cmd,
                    errors);
        } else if (currentUserService == null) {
            logger
                    .warn("currentUserService is not set for the controller. Request is considered unauthenticated.");
            requestAuthenticated = false;
            res = handleNotAuthenticatedRequest(httpRequest, httpResponse, cmd,
                    errors);
        } else {
            String sessionId = extractSessionId(httpRequest);

            Long userId = null;
            if( sessionId != null && !sessionId.equals("") )
                userId = currentUserService.setCurrentSsoSession(new BigInteger(sessionId));

            if (userId == null) {
                requestAuthenticated = false;
                res = handleNotAuthenticatedRequest(httpRequest, httpResponse,
                        cmd, errors);
            } else {
                requestAuthenticated = true;
                res = handleAuthenticatedRequest(httpRequest, httpResponse,
                        cmd, errors);
            }
        }

        if (res != null) {
	        final Map model = res.getModel();
	
	        model.put(GenericXmlView.REQUEST_DURATION_MODEL,
	                new Long(System.currentTimeMillis() - startTime));
	        model.put(GenericXmlView.REQUEST_AUTHENTICATED_MODEL,
	                requestAuthenticated);
	        model.put(GenericXmlView.AUTHENTICATION_FAILED_MODEL,
	                authenticationFailed);
        }
        
        return res;
    }

	protected String extractSessionId(HttpServletRequest httpRequest) {
		// attempt to get from cookie
		String sessionId = WebUtils.getCookieValue(httpRequest, SESSID_COOKIE_NAME);
		// if, null check query string as a last resort
		if (sessionId == null && allowQuerySession) {
		    sessionId = httpRequest.getParameter(SESSID_QUERY_NAME);
		}
		return sessionId;
	}

    @Override
    protected C getCommand(HttpServletRequest request) throws Exception {
        return getCmdClass().cast(super.getCommand(request));
    }

    protected boolean authenticationRequired() {
        return true;
    }

    protected ModelAndView handleNotAuthenticatedRequest(
            HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            C command, BindException errors) throws Exception {
    	
        throw new AuthorizationException(
                "Permission denied (user not-authenticated)");
    }

    protected abstract ModelAndView handleAuthenticatedRequest(
            HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            C command, BindException errors) throws Exception;


      
    
    public static String getRemoteIP(HttpServletRequest httpRequest) {
        String remoteAddr = httpRequest.getHeader("SS_CLIENT_IP");
        if (remoteAddr == null) {
            remoteAddr = httpRequest.getRemoteAddr();
        }
        return remoteAddr;
    }    
    public String getFormView() {
        return formView;
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    public String getFailureView() {
        return failureView;
    }

    public void setFailureView(String failureView) {
        this.failureView = failureView;
    }


}
