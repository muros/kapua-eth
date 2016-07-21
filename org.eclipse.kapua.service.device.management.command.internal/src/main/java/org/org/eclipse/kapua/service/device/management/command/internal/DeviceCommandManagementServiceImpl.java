package org.org.eclipse.kapua.service.device.management.command.internal;

import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestChannel;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestPayload;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandResponseMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandResponsePayload;

public class DeviceCommandManagementServiceImpl implements DeviceCommandManagementService
{
    private static final String deviceAppCommandName    = "COMMAND";
    private static final String deviceAppCommandVersion = "1.0.0";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeviceCommandOutput exec(KapuaId scopeId, KapuaId deviceId, DeviceCommandInput commandInput, Long timeout)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.execute, scopeId));

        //
        // Prepare the request
        CommandRequestChannel commandRequestChannel = new CommandRequestChannel();
        commandRequestChannel.setApp(deviceAppCommandName);
        commandRequestChannel.setVersion(deviceAppCommandVersion);
        commandRequestChannel.setMethod(KapuaMethod.EXECUTE);

        CommandRequestPayload commandRequestPayload = new CommandRequestPayload();
        commandRequestPayload.setArguments(commandInput.getArguments());
        commandRequestPayload.setStdin(commandInput.getStdin());
        commandRequestPayload.setTimeout(commandInput.getTimeout());
        commandRequestPayload.setWorkingDir(commandInput.getWorkingDir());
        commandRequestPayload.setEnvironmentPairs(commandInput.getEnvironment());
        commandRequestPayload.setRunAsync(commandInput.isRunAsynch());
        commandRequestPayload.setPassword(commandInput.getPassword());
        commandRequestPayload.setBody(commandInput.getBody());

        CommandRequestMessage commandRequestMessage = new CommandRequestMessage();
        // set scope id
        // set device id
        commandRequestMessage.setCapturedOn(new Date());
        commandRequestMessage.setPayload(commandRequestPayload);
        commandRequestMessage.setSemanticChannel(commandRequestChannel);

        //
        // Do exec
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor((KapuaRequestMessage) commandRequestMessage, timeout);
        CommandResponseMessage responseMessage = (CommandResponseMessage) deviceApplicationCall.send();

        //
        // Parse the response
        CommandResponsePayload responsePayload = responseMessage.getPayload();

        DeviceCommandOutput deviceCommandOutput = new DeviceCommandOutputImpl();
        deviceCommandOutput.setExceptionMessage(responsePayload.getExceptionMessage());
        deviceCommandOutput.setExceptionStack(responsePayload.getExceptionStack());
        deviceCommandOutput.setExitCode(responsePayload.getExitCode());
        deviceCommandOutput.setHasTimedout(false); // FIXME: implement track of timeout!!!
        deviceCommandOutput.setStderr(responsePayload.getStderr());
        deviceCommandOutput.setStdout(responsePayload.getStdout());

        return deviceCommandOutput;
    }
}
