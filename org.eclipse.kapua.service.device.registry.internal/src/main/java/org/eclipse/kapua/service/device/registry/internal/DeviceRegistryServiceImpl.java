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
package org.eclipse.kapua.service.device.registry.internal;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.KapuaPredicate;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DevicePredicates;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;

public class DeviceRegistryServiceImpl implements DeviceRegistryService
{

    @Override
    public Device create(DeviceCreator deviceCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceCreator, "deviceCreator");
        ArgumentValidator.notNull(deviceCreator.getScopeId(), "deviceCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(deviceCreator.getClientId(), "deviceCreator.clientId");
        ArgumentValidator.notEmptyOrNull(deviceCreator.getClientId(), "deviceCreator.clientId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "create", deviceCreator.getScopeId()));

        //
        // Create the connection
        Device device = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            JpaUtils.beginTransaction(em);

            device = DeviceDAO.create(em, deviceCreator);
            JpaUtils.commit(em);

            device = DeviceDAO.find(em, device.getId());
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return device;
    }

    @Override
    public Device update(Device device)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(device, "device");
        ArgumentValidator.notNull(device.getId(), "device.id");
        ArgumentValidator.notNull(device.getScopeId(), "v.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "update", device.getScopeId()));

        //
        // Do update
        Device deviceUpdated = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            Device currentDevice = DeviceDAO.find(em, device.getId());
            if (currentDevice == null) {
                throw new KapuaEntityNotFoundException(Device.TYPE, device.getId());
            }

            // FIXME: check preferred userid consistency

            // Passing attributes
            device.setStatus(device.getStatus());
            device.setDisplayName(device.getDisplayName());
            device.setLastEventOn(device.getLastEventOn());
            device.setLastEventType(device.getLastEventType());
            device.setSerialNumber(device.getSerialNumber());
            device.setModelId(device.getModelId());
            device.setImei(device.getImei());
            device.setImsi(device.getImsi());
            device.setIccid(device.getIccid());
            device.setBiosVersion(device.getBiosVersion());
            device.setFirmwareVersion(device.getFirmwareVersion());
            device.setOsVersion(device.getOsVersion());
            device.setJvmVersion(device.getJvmVersion());
            device.setOsgiVersion(device.getOsVersion());
            device.setApplicationFrameworkVersion(device.getApplicationFrameworkVersion());
            device.setApplicationIdentifiers(device.getApplicationIdentifiers());
            device.setAcceptEncoding(device.getAcceptEncoding());
            device.setGpsLongitude(device.getGpsLongitude());
            device.setGpsLatitude(device.getGpsLatitude());
            device.setCustomAttribute1(device.getCustomAttribute1());
            device.setCustomAttribute2(device.getCustomAttribute2());
            device.setCustomAttribute3(device.getCustomAttribute3());
            device.setCustomAttribute4(device.getCustomAttribute4());
            device.setCustomAttribute5(device.getCustomAttribute5());
            device.setCredentialsMode(device.getCredentialsMode());
            device.setPreferredUserId(device.getPreferredUserId());

            // Update
            JpaUtils.beginTransaction(em);
            DeviceDAO.update(em, device);
            JpaUtils.commit(em);

            deviceUpdated = DeviceDAO.find(em, device.getId());
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return deviceUpdated;
    }

    @Override
    public Device find(KapuaId scopeId, KapuaId entityId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "entityId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "read", scopeId));

        //
        // Do find
        Device device = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            device = DeviceDAO.find(em, entityId);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return device;
    }

    @Override
    public DeviceListResult query(KapuaQuery<Device> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "read", query.getScopeId()));

        //
        // Do Query
        DeviceListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = DeviceDAO.query(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return result;
    }

    @Override
    public long count(KapuaQuery<Device> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "read", query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = DeviceDAO.count(em, query);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return count;
    }

    @Override
    public void delete(Device device)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(device, "device");
        ArgumentValidator.notNull(device.getId(), "device.id");
        ArgumentValidator.notNull(device.getScopeId(), "device.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device", "delete", device.getScopeId()));

        //
        // Do delete
        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId deviceId = device.getId();

            if (DeviceDAO.find(em, deviceId) == null) {
                throw new KapuaEntityNotFoundException(Device.TYPE, deviceId);
            }

            JpaUtils.beginTransaction(em);
            DeviceDAO.delete(em, deviceId);
            JpaUtils.commit(em);
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }
    }

    @Override
    public Device findByClientId(KapuaId scopeId, String clientId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notEmptyOrNull(clientId, "clientId");

        //
        // Build query
        DeviceQueryImpl query = new DeviceQueryImpl(scopeId);
        KapuaPredicate predicate = new AttributePredicate<String>(DevicePredicates.CLIENT_ID, clientId);
        query.setPredicate(predicate);

        //
        // Query and parse result
        Device device = null;
        DeviceListResult result = query(query);
        if (result.size() == 1) {
            device = result.get(0);
        }

        return device;
    }

}
