package org.org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponseMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.DeviceCommandResponsePayload;

public class CommandManagementResponseHandlers
{
    public static class EXEC extends AbstractDeviceApplicationCallResponseHandler<DeviceCommandOutput>
    {

        @Override
        protected DeviceCommandOutput handleAcceptedRequest(KapuaResponseMessage responseMessage)
            throws DeviceManagementException
        {
            DeviceCommandResponsePayload responsePayload = new DeviceCommandResponsePayload(responseMessage.getResponsePayload());

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceCommandFactory deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);
            DeviceCommandOutput output = deviceCommandFactory.newOutputInstance();

            output.setExceptionMessage(responsePayload.getExceptionMessage());
            output.setExceptionStack(responsePayload.getExceptionStack());
            output.setExitCode(responsePayload.getExitCode());
            output.setStderr(responsePayload.getStderr());
            output.setStdout(responsePayload.getStdout());
            output.setHasTimedout(responsePayload.hasTimedout());

            return output;
        }

    }
}
