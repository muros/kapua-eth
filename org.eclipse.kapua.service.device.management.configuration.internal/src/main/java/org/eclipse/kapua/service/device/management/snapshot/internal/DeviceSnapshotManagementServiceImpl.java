package org.eclipse.kapua.service.device.management.snapshot.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationManagementResponseHandlers;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotListResult;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotManagementService;

public class DeviceSnapshotManagementServiceImpl implements DeviceSnapshotManagementService
{
    private static final String deviceSnapshotManagementAppId = "CONF-V1";

    @Override
    public DeviceSnapshotListResult get(KapuaId scopeId, KapuaId deviceId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.read, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "snapshots" };

        //
        // Do get
        DeviceCallExecutor<DeviceSnapshotListResult> deviceApplicationCall = new DeviceCallExecutor<DeviceSnapshotListResult>(scopeId,
                                                                                                                                    deviceId,
                                                                                                                                    deviceSnapshotManagementAppId,
                                                                                                                                    KapuaMethod.GET,
                                                                                                                                    resources);
        deviceApplicationCall.setResponseHandler(new SnapshotManagementResponseHandlers.GET());

        //
        // Return result
        return deviceApplicationCall.send();
    }

    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String snapshotId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(snapshotId, "snapshotId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.read, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "snapshots", snapshotId };

        //
        // Do get
        DeviceCallExecutor<DeviceConfiguration> deviceApplicationCall = new DeviceCallExecutor<DeviceConfiguration>(scopeId,
                                                                                                                          deviceId,
                                                                                                                          deviceSnapshotManagementAppId,
                                                                                                                          KapuaMethod.GET,
                                                                                                                          resources);
        deviceApplicationCall.setResponseHandler(new ConfigurationManagementResponseHandlers.GET());

        //
        // Return result
        return deviceApplicationCall.send();
    }

    // @Override
    // public void exec(KapuaId scopeId, KapuaId deviceid, DeviceConfiguration snapshotId)
    // throws KapuaException
    // {
    // // TODO Auto-generated method stub
    //
    // }

    @Override
    public void rollback(KapuaId scopeId, KapuaId deviceId, String snapshotId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(snapshotId, "snapshotId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.execute, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "snapshots", snapshotId };

        //
        // Do get
        DeviceCallExecutor<Void> deviceApplicationCall = new DeviceCallExecutor<Void>(scopeId,
                                                                                            deviceId,
                                                                                            deviceSnapshotManagementAppId,
                                                                                            KapuaMethod.EXEC,
                                                                                            resources);
        deviceApplicationCall.setResponseHandler(new SnapshotManagementResponseHandlers.EXEC());

        //
        // Return result
        deviceApplicationCall.send();
    }

}
