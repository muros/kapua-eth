package org.eclipse.kapua.service.device.registry.event.internal;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.EntityManager;
import org.eclipse.kapua.commons.util.KapuaExceptionUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceEntityManagerFactory;

public class DeviceEventServiceImpl implements DeviceEventService
{

    @Override
    public DeviceEvent create(DeviceEventCreator deviceEventCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceEventCreator, "deviceEventCreator");
        ArgumentValidator.notNull(deviceEventCreator.getScopeId(), "deviceEventCreator.scopeId");
        ArgumentValidator.notNull(deviceEventCreator.getDeviceId(), "deviceEventCreator.deviceId");
        ArgumentValidator.notNull(deviceEventCreator.getReceivedOn(), "deviceEventCreator.receivedOn");
        ArgumentValidator.notEmptyOrNull(deviceEventCreator.getResource(), "deviceEventCreator.eventType");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceEventDomain.DEVICE_EVENT, Actions.write, deviceEventCreator.getScopeId()));

        //
        // Create the event
        DeviceEvent deviceEvent = null;
        EntityManager em = DeviceEntityManagerFactory.getEntityManager();
        try {
            em.beginTransaction();

            deviceEvent = DeviceEventDAO.create(em, deviceEventCreator);
            em.commit();

            deviceEvent = DeviceEventDAO.find(em, deviceEvent.getId());
        }
        catch (Exception e) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return deviceEvent;
    }

    @Override
    public DeviceEvent find(KapuaId scopeId, KapuaId entityId)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceEventDomain.DEVICE_EVENT, Actions.read, scopeId));

        //
        // Do find
        DeviceEvent deviceEvent = null;
        EntityManager em = DeviceEntityManagerFactory.getEntityManager();
        try {
            deviceEvent = DeviceEventDAO.find(em, entityId);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return deviceEvent;
    }

    @Override
    public DeviceEventListResult query(KapuaQuery<DeviceEvent> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceEventDomain.DEVICE_EVENT, Actions.read, query.getScopeId()));

        //
        // Do Query
        DeviceEventListResult result = null;
        EntityManager em = DeviceEntityManagerFactory.getEntityManager();
        try {
            result = DeviceEventDAO.query(em, query);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return result;
    }

    @Override
    public long count(KapuaQuery<DeviceEvent> query)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceEventDomain.DEVICE_EVENT, Actions.read, query.getScopeId()));

        //
        // Do count
        long count = 0;
        EntityManager em = DeviceEntityManagerFactory.getEntityManager();
        try {
            count = DeviceEventDAO.count(em, query);
        }
        catch (Exception e) {
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }

        return count;
    }

    @Override
    public void delete(DeviceEvent deviceEvent)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(deviceEvent, "deviceEvent");
        ArgumentValidator.notNull(deviceEvent.getId(), "deviceEvent.id");
        ArgumentValidator.notNull(deviceEvent.getScopeId(), "deviceEvent.scopeId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceEventDomain.DEVICE_EVENT, Actions.delete, deviceEvent.getScopeId()));

        //
        // Do delete
        EntityManager em = DeviceEntityManagerFactory.getEntityManager();
        try {
            KapuaId deviceEventId = deviceEvent.getId();

            if (DeviceEventDAO.find(em, deviceEventId) == null) {
                throw new KapuaEntityNotFoundException(DeviceEvent.TYPE, deviceEventId);
            }

            em.beginTransaction();
            DeviceEventDAO.delete(em, deviceEventId);
            em.commit();
        }
        catch (Exception e) {
            em.rollback();
            throw KapuaExceptionUtils.convertPersistenceException(e);
        }
        finally {
            em.close();
        }
    }
}
