package com.mvwsolutions.common.test.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.LocalConnector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.DispatcherServlet;

import com.mvwsolutions.common.ComponentManager;

/**
 * 
 * @author smineyev
 * 
 */
public class SpringServletTester {
    private final static Log logger = LogFactory
            .getLog(SpringServletTester.class);


    private Server server = new Server();

    private LocalConnector localConnector = new LocalConnector();

    private int usageCounter;
    
    private List<ServletContextListener> listeners = new ArrayList<ServletContextListener>();;

    
    public SpringServletTester(ServletContextListener... extraListeners) throws IOException {
        this.listeners.add(new ContextLoaderListener());
        if (extraListeners != null) {
            this.listeners.addAll(Arrays.asList(extraListeners));
        }
        
        try {
            this.server.setSendServerVersion(false);
            this.server.addConnector(localConnector);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final String baseDir = System.getProperty("basedir");
        String contextResourceBase = (baseDir == null ? "." : baseDir) + "/web";
        File contentResourceBaseFile = new File(contextResourceBase);
        if (contentResourceBaseFile.exists()) {
            addContext(contextResourceBase, "/");
        } else {
            throw new FileNotFoundException("Web content folder ("
                    + contentResourceBaseFile.getCanonicalPath()
                    + ") doesn't exist in current project "
                    + "so no default web context is created");
        }
    }

    public void enableRemoteConnectors(int... ports) {
        if (usageCounter > 0) {
            throw new IllegalStateException(
                    "servlet container currently is in use");
        }
        for (int port : ports) {
            if (checkIfRemoteConnectorExists(port)) {
                logger.warn("Connector on port (" + port
                        + ") has already been enabled");
                continue;
            }

            SocketConnector remoteConnector = new SocketConnector();
            // remoteConnector.setHost("localhost");
            remoteConnector.setPort(port);
            server.addConnector(remoteConnector);
            logger.debug("Connector on port (" + port + ") added");
        }
    }

    private boolean checkIfRemoteConnectorExists(int port) {
        for (Connector c : server.getConnectors()) {
            if (c instanceof SocketConnector) {
                if (((SocketConnector) c).getPort() == port) {
                    return true;
                }
            }
        }
        return false;
    }

    public void disableRemoteConnectors() {
        if (usageCounter > 0) {
            throw new IllegalStateException(
                    "servlet container currently is in use");
        }
        for (Connector c : server.getConnectors()) {
            if (c instanceof SocketConnector) {
                server.removeConnector(c);
            }
        }
    }

    public Context addContext(String contextResourceBase, String contextPath) {
        if (this.usageCounter > 0) {
            throw new IllegalStateException(
                    "servlet container currently is in use");
        }
        Context context = new Context(Context.SESSIONS | Context.SECURITY);

        this.server.addHandler(context);

        context.setContextPath(contextPath);

        context.setResourceBase(contextResourceBase);

        setInitParameter(context, "contextConfigLocation", "");
        setInitParameter(context, "parentContextKey",
                ComponentManager.ROOT_BEAN_FACTORY_NAME);
        setInitParameter(context, "locatorFactorySelector",
                ComponentManager.BEAN_REF_CONTEXT_XML);

        for (ServletContextListener listener: listeners) {
            context.addEventListener(listener);
        }

        final ServletHolder servletHolder = context.getServletHandler()
                .newServletHolder(DispatcherServlet.class);

        servletHolder.setInitOrder(1);
        servletHolder.setName("spring");

        context.addServlet(servletHolder, "/spring/*");

        return context;
    }

    public void start() throws Exception {
        if (this.usageCounter == 0) {
            this.server.start();
        }
        ++this.usageCounter;
    }

    public void stop() throws Exception {
        if (--this.usageCounter == 0) {
            this.server.stop();
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * Get raw HTTP responses from raw HTTP requests. Multiple requests and
     * responses may be handled, but only if persistent connections conditions
     * apply.
     * 
     * @param rawRequests
     *            String of raw HTTP requests
     * @return String of raw HTTP responses
     * @throws Exception
     */
    public String getResponses(String rawRequests) throws Exception {
        localConnector.reopen();
        String responses = localConnector.getResponses(rawRequests);
        return responses;
    }

    @SuppressWarnings("unchecked")
    public static void setInitParameter(Context context, String name,
            Object value) {
        context.getInitParams().put(name, value);
    }
}
