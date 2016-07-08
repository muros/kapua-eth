package org.org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementMethod;
import org.eclipse.kapua.service.device.management.commons.call.DeviceApplicationCall;
import org.org.eclipse.kapua.service.device.management.command.message.internal.DeviceCommandRequestPayload;

public class DeviceCommandManagementServiceImpl implements DeviceCommandManagementService
{
    private static final String deviceCommandManagementAppId = "CMD-V1";

    @Override
    public DeviceCommandOutput exec(KapuaId scopeId, KapuaId deviceId, DeviceCommandInput commandInput)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(commandInput, "commandInput");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission("device-manage", "update", scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "command" };
        DeviceCommandRequestPayload commandRequestPayload = new DeviceCommandRequestPayload();
        commandRequestPayload.setArguments(commandInput.getArguments());
        commandRequestPayload.setStdin(commandInput.getStdin());
        commandRequestPayload.setTimeout(commandInput.getTimeout());
        commandRequestPayload.setWorkingDir(commandInput.getWorkingDir());
        commandRequestPayload.setEnvironmentPairs(commandInput.getEnvironment());
        commandRequestPayload.setRunAsync(commandInput.isRunAsynch());
        commandRequestPayload.setZipBytes(commandInput.getBytes());
        commandRequestPayload.setPassword(commandInput.getPassword());

        //
        // Do exec
        DeviceApplicationCall<DeviceCommandOutput> deviceApplicationCall = new DeviceApplicationCall<DeviceCommandOutput>(scopeId,
                                                                                                                          deviceId,
                                                                                                                          deviceCommandManagementAppId,
                                                                                                                          DeviceManagementMethod.EXEC,
                                                                                                                          resources);

        deviceApplicationCall.setRequestPayload(commandRequestPayload);
        deviceApplicationCall.setResponseHandler(new CommandManagementResponseHandlers.EXEC());

        //
        // Return result
        return deviceApplicationCall.send();
    }

}
