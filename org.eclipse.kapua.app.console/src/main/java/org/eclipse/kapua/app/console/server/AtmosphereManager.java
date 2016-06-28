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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.gwt.server.GwtAtmosphereResource;

public class AtmosphereManager {
    private Map<String, GwtAtmosphereResource> clientResources;

    AtmosphereManager() {
        // init the client resources map
        clientResources = Collections.synchronizedMap(new HashMap<String, GwtAtmosphereResource>());
    }

    public GwtAtmosphereResource getGwtAtmosphereResource(String id) {
        return clientResources.get(id);
    }

    void setBroadcaster(String id, GwtAtmosphereResource resource) {
        clientResources.put(id, resource);
    }
}
