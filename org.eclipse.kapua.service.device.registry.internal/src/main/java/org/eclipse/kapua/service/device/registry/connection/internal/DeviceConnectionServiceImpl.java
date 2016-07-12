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
package org.eclipse.kapua.service.device.registry.connection.internal;

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
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionCreator;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionListResult;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionPredicates;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;

/**
 * DeviceConnectionService exposes APIs to retrieve Device connections under a scope.
 * It includes APIs to find, list, and update devices connections associated with a scope.
 */
public class DeviceConnectionServiceImpl implements DeviceConnectionService
{

    @Override
    public DeviceConnection create(DeviceConnectionCreator deviceConnectionCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceConnectionCreator, "deviceConnectionCreator");
        ArgumentValidator.notNull(deviceConnectionCreator.getScopeId(), "deviceConnectionCreator.scopeId");
        ArgumentValidator.notEmptyOrNull(deviceConnectionCreator.getClientId(), "deviceConnectionCreator.clientId");
        ArgumentValidator.notNull(deviceConnectionCreator.getUserId(), "deviceConnectionCreator.userId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.write, deviceConnectionCreator.getScopeId()));

        //
        // Create the connection
        DeviceConnection deviceConnection = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            JpaUtils.beginTransaction(em);

            deviceConnection = DeviceConnectionDAO.create(em, deviceConnectionCreator);
            JpaUtils.commit(em);

            deviceConnection = DeviceConnectionDAO.find(em, deviceConnection.getId());
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return deviceConnection;
    }

    @Override
    public DeviceConnection update(DeviceConnection deviceConnection)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceConnection, "deviceConnection");
        ArgumentValidator.notNull(deviceConnection.getId(), "deviceConnection.id");
        ArgumentValidator.notNull(deviceConnection.getScopeId(), "deviceConnection.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.write, deviceConnection.getScopeId()));

        //
        // Do update
        DeviceConnection deviceConnectionUpdated = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            if (DeviceConnectionDAO.find(em, deviceConnection.getId()) == null) {
                throw new KapuaEntityNotFoundException(DeviceConnection.TYPE, deviceConnection.getId());
            }

            JpaUtils.beginTransaction(em);
            deviceConnectionUpdated = DeviceConnectionDAO.update(em, deviceConnection);
            JpaUtils.commit(em);
        }
        catch (Exception e) {
            JpaUtils.rollback(em);
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return deviceConnectionUpdated;
    }

    @Override
    public DeviceConnection find(KapuaId scopeId, KapuaId entityId)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.read, scopeId));

        //
        // Do find
        DeviceConnection deviceConnection = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            deviceConnection = DeviceConnectionDAO.find(em, entityId);
        }
        catch (Exception e) {
            throw JpaUtils.toKapuaException(e);
        }
        finally {
            JpaUtils.close(em);
        }

        return deviceConnection;
    }

    @Override
    public DeviceConnection findByClientId(KapuaId scopeId, String clientId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notEmptyOrNull(clientId, "clientId");

        //
        // Build query
        DeviceConnectionQueryImpl query = new DeviceConnectionQueryImpl(scopeId);
        KapuaPredicate predicate = new AttributePredicate<String>(DeviceConnectionPredicates.CLIENT_ID, clientId);
        query.setPredicate(predicate);

        //
        // Query and parse result
        DeviceConnection device = null;
        DeviceConnectionListResult result = query(query);
        if (result.size() == 1) {
            device = result.get(0);
        }

        return device;
    }

    @Override
    public DeviceConnectionListResult query(KapuaQuery<DeviceConnection> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.read, query.getScopeId()));

        //
        // Do Query
        DeviceConnectionListResult result = null;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            result = DeviceConnectionDAO.query(em, query);
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
    public long count(KapuaQuery<DeviceConnection> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = JpaUtils.getEntityManager();
        try {
            count = DeviceConnectionDAO.count(em, query);
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
    public void delete(DeviceConnection deviceConnection)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceConnection, "deviceConnection");
        ArgumentValidator.notNull(deviceConnection.getId(), "deviceConnection.id");
        ArgumentValidator.notNull(deviceConnection.getScopeId(), "deviceConnection.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceConnectionDomain.device_connection, Actions.write, deviceConnection.getScopeId()));

        //
        // Do delete
        EntityManager em = JpaUtils.getEntityManager();
        try {
            KapuaId deviceConnectionId = deviceConnection.getId();

            if (DeviceConnectionDAO.find(em, deviceConnectionId) == null) {
                throw new KapuaEntityNotFoundException(DeviceConnection.TYPE, deviceConnectionId);
            }

            JpaUtils.beginTransaction(em);
            DeviceConnectionDAO.delete(em, deviceConnectionId);
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
    public void connect(DeviceConnectionCreator creator)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect(KapuaId scopeId, String clientId)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

}
