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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.internal.DeviceImpl;

@Path("/devices")
public class Devices extends AbstractKapuaResource {
    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
    private final DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);
    private final DeviceCommandManagementService commandService = locator.getService(DeviceCommandManagementService.class);
    private final DeviceCommandFactory commandFactory = locator.getFactory(DeviceCommandFactory.class);
    private final DeviceConfigurationManagementService configurationService = locator.getService(DeviceConfigurationManagementService.class);

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
    
    /**
     * Creates a new Device based on the information provided in DeviceCreator parameter.
     *
     * @param deviceCreator Provides the information for the new Device to be created.
     * @return The newly created Device object.
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Device postDevice(DeviceCreator deviceCreator) {
        Device device = null;
        try {
            deviceCreator.setScopeId(KapuaSecurityUtils.getSession().getScopeId());
            device = deviceRegistryService.create(deviceCreator);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(device);
    }
    
    /**
     * Returns the Device specified by the "id" path parameter.
     *
     * @param deviceId The id of the Device requested.
     * @return The requested Device object.
     */
    @GET
    @Path("{deviceId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Device getDevice(@PathParam("deviceId") String deviceId) {
        Device device = null;
        try {
            KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();
            KapuaId id = KapuaEid.parseShortId(deviceId);
            device = deviceRegistryService.find(scopeId, id);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(device);
    }
    
    /**
     * Updates a device based on the information provided in the Device parameter.
     *
     * @param device Provides the information to update the device.
     * @return The updated Device object.
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Device updateDevice(Device device) {
        try {
            ((DeviceImpl)device).setScopeId(KapuaSecurityUtils.getSession().getScopeId());
            device = deviceRegistryService.update(device);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(device);
    }
    
    /**
     * Deletes a device based on the id provided in deviceId parameter.
     *
     * @param deviceId Provides the id of the device to delete.
     */
    @DELETE
    @Path("{deviceId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response deleteDevice(@PathParam("deviceId") String deviceId) {
        try {
            KapuaId deviceKapuaId = KapuaEid.parseShortId(deviceId);
            KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();
            deviceRegistryService.delete(scopeId, deviceKapuaId);
        } catch (Throwable t) {
            handleException(t);
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("{deviceId}/command")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DeviceCommandOutput sendCommand(
            DeviceCommandInput commandInput, 
            @PathParam("deviceId") String deviceId, 
            @QueryParam("timeout") Long timeout) throws NumberFormatException, KapuaException {
        KapuaId deviceKapuaId = KapuaEid.parseShortId(deviceId);
        KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();
        DeviceCommandOutput output = commandService.exec(scopeId, deviceKapuaId, commandInput, timeout);
        return output;
    }
    
    /**
     * Returns the configuration of a device or the configuration of the OSGi component
     * identified with specified PID (service's persistent identity).
     * In the OSGi framework, the service's persistent identity is defined as the name attribute of the
     * Component Descriptor XML file; at runtime, the same value is also available
     * in the component.name and in the service.pid attributes of the Component Configuration.
     * @param deviceId The id of the device
     * @param @optional componentId
     */
    
    @GET
    @Path("{deviceId}/configurations")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<? extends DeviceConfiguration> getDevice(@PathParam("deviceId") String deviceId,
                                         @QueryParam("componentId") String componentId) {
        DeviceConfiguration deviceConfiguration = null;
        try {
            KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();
            KapuaId id = KapuaEid.parseShortId(deviceId);
            // FIXME actually not working
            deviceConfiguration = configurationService.get(scopeId, id, null, componentId, null);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(deviceConfiguration.getComponentConfigurations());
    }
}
