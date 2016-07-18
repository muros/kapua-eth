package org.org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.app.response.KapuaResponsePayload;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponsePayload;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.org.eclipse.kapua.service.device.management.command.message.internal.DeviceCommandResponsePayload;

public class CommandManagementResponseHandlers
{
    public static class EXEC extends AbstractDeviceApplicationCallResponseHandler<DeviceCommandOutput>
    {

        @SuppressWarnings("rawtypes")
        @Override
        protected DeviceCommandOutput handleAcceptedRequest(DeviceResponseMessage responseMessage)
            throws DeviceManagementException
        {
            DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();

            DeviceCommandResponsePayload commandResponsePayload = null;
            try {
                commandResponsePayload = new DeviceCommandResponsePayload((KapuaResponsePayload) responsePayload);
            }
            catch (KapuaException e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    new Object[] {
                                                                   responseMessage
                                                    });
            }

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceCommandFactory deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);
            DeviceCommandOutput output = deviceCommandFactory.newOutputInstance();

            output.setExceptionMessage(commandResponsePayload.getExceptionMessage());
            output.setExceptionStack(commandResponsePayload.getExceptionStack());
            output.setExitCode(commandResponsePayload.getExitCode());
            output.setStderr(commandResponsePayload.getStderr());
            output.setStdout(commandResponsePayload.getStdout());
            output.setHasTimedout(commandResponsePayload.hasTimedout());

            return output;
        }

    }
}
