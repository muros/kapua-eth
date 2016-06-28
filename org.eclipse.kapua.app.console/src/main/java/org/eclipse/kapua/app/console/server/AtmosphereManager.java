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
