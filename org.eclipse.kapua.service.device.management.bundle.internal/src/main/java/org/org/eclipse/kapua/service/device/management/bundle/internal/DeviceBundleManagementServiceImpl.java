package org.org.eclipse.kapua.service.device.management.bundle.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleListResult;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;

public class DeviceBundleManagementServiceImpl implements DeviceBundleManagementService
{
    private static final String deviceBundleManagementAppId = "DEPLOY-V1";

    @Override
    public DeviceBundleListResult get(KapuaId scopeId, KapuaId deviceId)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.read, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "bundle" };

        //
        // Do get
        DeviceCallExecutor<DeviceBundleListResult> deviceApplicationCall = new DeviceCallExecutor<DeviceBundleListResult>(scopeId,
                                                                                                                                deviceId,
                                                                                                                                deviceBundleManagementAppId,
                                                                                                                                KapuaMethod.GET,
                                                                                                                                resources);
        deviceApplicationCall.setResponseHandler(new BundleManagementResponseHandlers.GET());

        //
        // Return result
        return deviceApplicationCall.send();
    }

    @Override
    public void start(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(bundleId, "bundleId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "start", bundleId };

        //
        // Do exec
        DeviceCallExecutor<Void> deviceApplicationCall = new DeviceCallExecutor<Void>(scopeId,
                                                                                            deviceId,
                                                                                            deviceBundleManagementAppId,
                                                                                            KapuaMethod.EXEC,
                                                                                            resources);
        deviceApplicationCall.setResponseHandler(new BundleManagementResponseHandlers.PUT());

        //
        // Make call
        deviceApplicationCall.send();
    }

    @Override
    public void stop(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(bundleId, "bundleID");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "stop", bundleId };

        //
        // Do exec
        DeviceCallExecutor<Void> deviceApplicationCall = new DeviceCallExecutor<Void>(scopeId,
                                                                                            deviceId,
                                                                                            deviceBundleManagementAppId,
                                                                                            KapuaMethod.EXEC,
                                                                                            resources);
        deviceApplicationCall.setResponseHandler(new BundleManagementResponseHandlers.PUT());

        //
        // Make call
        deviceApplicationCall.send();
    }

}
