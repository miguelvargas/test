package com.mvwsolutions.common.test.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.testing.HttpTester;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mvwsolutions.common.Utils;
import com.mvwsolutions.common.web.xml.Response;

/**
 * 
 * @author smineyev
 * 
 */
public class SpringServletTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(SpringServletTestCase.class);

    protected static SpringServletTester tester;

    protected HttpTester httpRequest;

    protected HttpTester httpResponse;

    @BeforeClass
    public static void startServletTester() throws Exception {
        tester = new SpringServletTester();
        tester.start();
    }

    @Before
    public final void beforeRequest() {
        this.httpRequest = new HttpTester();
        this.httpResponse = new HttpTester();
    }

    protected final HttpTester sendRequest(String uri) throws Exception {

        final boolean post = this.httpRequest.getContent() != null;

        this.httpRequest.setMethod(post ? "POST" : "GET");
        this.httpRequest.setURI(uri);
        this.httpRequest.setVersion("HTTP/1.1");
        this.httpRequest.setHeader("Host", "tester");
        if (post) {
            this.httpRequest.setHeader("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
        }

        final String request = this.httpRequest.generate();

        logger.debug("(!) request: " + request);
        String rawHttpResponse = tester.getResponses(request);
        logger.debug("(!) response: " + rawHttpResponse);
        this.httpResponse.parse(rawHttpResponse);

        return this.httpResponse;
    }

    public void postViaRemoteConnector(String url, String body, String cookies) {
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(url);
            try {
                if (body != null) {
                    StringRequestEntity sre = new StringRequestEntity(body, "",
                            "UTF-8");
                    postMethod.setRequestEntity(sre);
                }
                // for (Map.Entry<String, String> entry : cookies.entrySet()) {
                // postMethod.addRequestHeader("Cookie", entry.getKey() + "="
                // + entry.getValue());
                // }
                if (cookies != null && cookies.length() > 0) {
                    postMethod.addRequestHeader("Cookie", cookies);
                }
                httpResponse.setStatus(httpClient.executeMethod(postMethod));
                httpResponse.setContent(new String(
                        postMethod.getResponseBody(), "UTF-8"));
            } finally {
                postMethod.releaseConnection();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getResponseCookie(String cName) {
        Map<String, String> cookieMap = getCockies();
        return cookieMap.get(cName);
    }

    protected Map<String, String> getCockies() {
        Map<String, String> cookieMap = new HashMap<String, String>();
        String cookiesStr = httpResponse.getHeader("Set-Cookie");
        List<String> cookies = Utils.tokenize(cookiesStr, ";");
        for (String cockie : cookies) {
            int p = cockie.indexOf('=');
            if (p != -1) {
                String name = cockie.substring(0, p);
                String value = cockie.substring(p + 1);
                cookieMap.put(name, value);
            }
        }
        return cookieMap;
    }

    protected final <T> T getResponse(Class<T> expectedResponseClass,
            Class... classesToBeBound) {
    	Assert.assertEquals("Bad HTTP response code: " + this.httpResponse.getStatus()
                + ", content: " + this.httpResponse.getReason(), 200,
                this.httpResponse.getStatus());

    	Assert.assertNotNull("Response is empty: " + this.httpResponse.getStatus()
                + " " + this.httpResponse.getReason(), this.httpResponse
                .getContent());

        final Class[] classes = new Class[classesToBeBound.length + 2];

        classes[0] = Response.class;
        classes[1] = expectedResponseClass;
        System.arraycopy(classesToBeBound, 0, classes, 2,
                classesToBeBound.length);

        final String content = this.httpResponse.getContent();

        Object responseObject;
        try {
            final Serializer unmarshaller = new Persister();
            responseObject = unmarshaller.read(Response.class, content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseObject.getClass() == expectedResponseClass) {
            return expectedResponseClass.cast(responseObject);
        } else {
            if (responseObject.getClass() != Response.class) {
                Assert.fail("Actual response is neither instance of "
                        + Response.class.getName() + " nor "
                        + expectedResponseClass.getName());
            }
        }

        Response response = (Response) responseObject;

        if (expectedResponseClass == Response.class) {
            return expectedResponseClass.cast(responseObject);
        }

        // to move on and extract result object we need to make sure that
        // request is successful
        Assert.assertNotNull("Result object is null", response);
        Assert.assertTrue("HTTP request failed, the cause: " + response.getError(),
                response.isSuccess());

        Assert.assertTrue("Response is " + response.getResultObject() + ", but "
                + expectedResponseClass.getName() + " expected",
                expectedResponseClass.isInstance(response.getResultObject()));

        return expectedResponseClass.cast(response.getResultObject());
    }

    @After
    public final void afterResponse() {
        this.httpRequest = null;
        this.httpResponse = null;
    }

    @AfterClass
    public static void stopServletTester() throws Exception {
        if (tester != null) {
            tester.stop();
            tester = null;
        }
    }

}
