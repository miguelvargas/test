package com.mvwsolutions.common.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.View;
import org.springframework.xml.transform.StringResult;

import com.mvwsolutions.common.web.xml.Response;

public class GenericXmlView implements View {
    public static final String VIEW_NAME = "generic-xml-view";

    public static final String OBJECT_MODEL = "OBJECT_MODEL";

    public static final String EXCEPTION_MODEL = "EXCEPTION_MODEL";

    public static final String REQUEST_DURATION_MODEL = "REQUEST_DURATION_MODEL";

    public static final String REQUEST_AUTHENTICATED_MODEL = "REQUEST_AUTHENTICATED_MODEL";

    public static final String AUTHENTICATION_FAILED_MODEL = "AUTHENTICATION_FAILED_MODEL";

    private Marshaller marshaller;

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public String getContentType() {
        return ("text/xml; charset=UTF-8");
    }

    @SuppressWarnings("unchecked")
	public void render(Map model, HttpServletRequest htppRequest,
            HttpServletResponse httpResponse) throws Exception {
        httpResponse.setContentType("text/xml; charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
 
        Response response = new Response();
        if (model != null) {
            Object resultObj = model.get(OBJECT_MODEL);
//            if (resultObj != null) {
//                if (resultObj instanceof Collection) {
//                    response.setResultObject(new CollectionHolder(
//                            (Collection) resultObj));
//                } else {
                    response.setResultObject(resultObj);
//                }
//            } else {
//                response.setResultObject(NullObject.INSTANCE);
//            }
        }
        Long requestDuration = (Long) model.get(REQUEST_DURATION_MODEL);
        if (requestDuration != null) {
            response.setRequestDuration(requestDuration);
        }
        Boolean requestAuthenticated = (Boolean) model
                .get(REQUEST_AUTHENTICATED_MODEL);
        if (requestAuthenticated != null) {
            response.setRequestAuthenticated(requestAuthenticated);
        }
        Boolean authenticationFailed = (Boolean) model
                .get(AUTHENTICATION_FAILED_MODEL);
        if (authenticationFailed != null) {
            response.setAuthenticationFailed(authenticationFailed);
        }
        StringResult sr = new StringResult();
        marshaller.marshal(response, sr);
        httpResponse.getWriter().write(sr.toString());
    }

}
