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
package org.eclipse.kapua.app.api.v1.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;

@Path("/devices")
public class Devices extends AbstractKapuaResource {
    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
    private final DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);

    /**
     * Returns the list of all the Devices visible to the currently connected user.
     *
     * @return The list of requested Device objects.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DeviceListResult getDevices() {
        DeviceListResult devicesResult = deviceFactory.newDeviceListResult();
        try {
            DeviceQuery query = deviceFactory.newQuery(KapuaSecurityUtils.getSession().getScopeId());
            devicesResult = (DeviceListResult)deviceRegistryService.query(query);
        } catch (Throwable t) {
            handleException(t);
        }
        return devicesResult;
    }
}
