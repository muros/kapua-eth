/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereEventLifecycle;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.gwt.server.GwtAtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereHandler extends AtmosphereGwtHandler {
    private static final Logger s_logger = LoggerFactory.getLogger(AtmosphereHandler.class);

    @Override
    public void init(ServletConfig servletConfig)
    throws ServletException {
        super.init(servletConfig);
        super.setHeartbeat(20000);
    }

    @Override
    public int doComet(GwtAtmosphereResource resource)
    throws ServletException, IOException {
        // logger.info("Atmosphere connection ID: " + resource.getConnectionID());

        HttpSession session = resource.getAtmosphereResource().getRequest().getSession(false);
        if (session != null) {
            s_logger.debug("Got session with id: " + session.getId());

            String atmosphereClientId = getAtmosphereClientId(resource.getAtmosphereResource().getRequest().getQueryString());
            s_logger.debug("setting Atmosphere ID to " + atmosphereClientId);
            resource.getBroadcaster().setID("GWT_COMET");
            // resource.getBroadcaster().setID(value);
            AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
            atmosphereManager.setBroadcaster(atmosphereClientId, resource);

            AtmosphereResource atmosphereResource = resource.getAtmosphereResource();
            if (atmosphereResource instanceof AtmosphereEventLifecycle) {
                AtmosphereEventLifecycle ael = (AtmosphereEventLifecycle) atmosphereResource;
                ael.addEventListener(eventListener);
            }
        } else {
            s_logger.warn("No session");
        }
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Url: " + resource.getAtmosphereResource().getRequest().getRequestURL() + "?" + resource.getAtmosphereResource().getRequest().getQueryString());
        }

        String agent = resource.getRequest().getHeader("user-agent");
        s_logger.info(agent);
        return NO_TIMEOUT;
    }

    @Override
    public void cometTerminated(GwtAtmosphereResource cometResponse, boolean serverInitiated) {
        super.cometTerminated(cometResponse, serverInitiated);
        s_logger.debug("COMET DISCONNECTED!!!");
    }

    @Override
    public void doPost(HttpServletRequest postRequest, HttpServletResponse postResponse, List<Serializable> messages, GwtAtmosphereResource cometResource) {
        HttpSession session = postRequest.getSession(false);
        if (session != null) {
            logger.info("Post has session with id: " + session.getId());
        } else {
            logger.info("Post has no session");
        }
        super.doPost(postRequest, postResponse, messages, cometResource);
    }

    @Override
    public void disconnect(GwtAtmosphereResource resource) {

        // clean disconnect - not always called in the event of a browser close
        s_logger.info("clean disconnect: " + resource.getAtmosphereResource().getRequest().getRequestURL() + "?" + resource.getAtmosphereResource().getRequest().getQueryString());

        // clean up the broadcaster
        String atmosphereClientId = getAtmosphereClientId(resource.getAtmosphereResource().getRequest().getQueryString());
        AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
        atmosphereManager.setBroadcaster(atmosphereClientId, null);

        super.disconnect(resource);
    }

    AtmosphereResourceEventListener eventListener = new AtmosphereResourceEventListener() {
        public void onBroadcast(AtmosphereResourceEvent event) {
            s_logger.debug("Lifecycle: broadcast " + event.getResource().getRequest().getQueryString());
            s_logger.debug("Lifecycle: broadcast " + event.getResource().getResponse());
        }

        public void onDisconnect(AtmosphereResourceEvent event) {
            s_logger.debug("Lifecycle: disconnect " + event.getResource().getRequest().getQueryString());
        }

        public void onResume(AtmosphereResourceEvent event) {
            s_logger.debug("Lifecycle: resume " + event.getResource().getRequest().getQueryString());
        }

        public void onSuspend(AtmosphereResourceEvent event) {
            s_logger.debug("Lifecycle: suspend " + event.getResource().getRequest().getQueryString());
        }

        public void onThrowable(AtmosphereResourceEvent event) {
            s_logger.debug("Lifecycle: throwable " + event.getResource().getRequest().getQueryString());

            // clean up the broadcaster
            String atmosphereClientId = getAtmosphereClientId(event.getResource().getRequest().getQueryString());
            AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
            atmosphereManager.setBroadcaster(atmosphereClientId, null);
        }
    };

    @Override
    public void onStateChange(AtmosphereResourceEvent event)
    throws IOException {

        // logger.debug("onStateChange " + event);

        Object message = event.getMessage();
        if (message == null || event.isCancelled())
            return;

        // used to check if our broadcaster stopped working (i.e. the Atmosphere client just 'went away')
        if (event.getResource().getSerializer() != null) {
            try {
                event.getResource().getSerializer().write(event.getResource().getResponse().getOutputStream(), message);
            } catch (Throwable ex) {
                // clean up the broadcaster
                s_logger.info("client has disconnected - removing it from the AtmosphereManager");
                String atmosphereClientId = getAtmosphereClientId(event.getResource().getRequest().getQueryString());
                AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
                atmosphereManager.setBroadcaster(atmosphereClientId, null);
            }
        } else {
            super.onStateChange(event);
        }
    }

    private String getAtmosphereClientId(String queryString) {
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            StringTokenizer st2 = new StringTokenizer(token, "=");
            String key = st2.nextToken();
            String value = st2.nextToken();

            if (key.equals("clientId")) {
                return value;
            }
        }

        return null;
    }
}
