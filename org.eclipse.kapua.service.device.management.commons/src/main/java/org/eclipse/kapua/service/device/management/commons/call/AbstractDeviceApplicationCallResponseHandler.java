package org.eclipse.kapua.service.device.management.commons.call;

import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponsePayload;
import org.eclipse.kapua.service.device.message.response.KapuaResponseCode;

public abstract class AbstractDeviceApplicationCallResponseHandler<T>
{
    public T handle(KapuaResponseMessage responseMessage)
        throws DeviceManagementException
    {
        T responseObject = null;
        if (responseMessage != null) {
            KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();
            if (responsePayload != null) {
                KapuaResponseCode responseCode = responsePayload.getResponseCode();

                switch (responseCode) {
                    case ACCEPTED:
                    {
                        responseObject = handleAcceptedRequest(responseMessage);
                    }
                        break;
                    case BAD_REQUEST:
                    {
                        handleBadRequestReply(responseMessage);
                    }
                    case INTERNAL_ERROR:
                    {
                        handleDeviceInternalErrorReply(responseMessage);
                    }
                    case NOT_FOUND:
                    {
                        handleNotFoundReply(responseMessage);
                    }
                }
            }
        }

        return responseObject;
    }

    protected abstract T handleAcceptedRequest(KapuaResponseMessage responseMessage)
        throws DeviceManagementException;

    protected void handleBadRequestReply(KapuaResponseMessage responseMessage)
        throws DeviceManagementException
    {
        KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();
        KapuaResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_BAD_REQUEST,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

    protected void handleDeviceInternalErrorReply(KapuaResponseMessage responseMessage)
        throws DeviceManagementException
    {
        KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();
        KapuaResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_INTERNAL_ERROR,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

    protected void handleNotFoundReply(KapuaResponseMessage responseMessage)
        throws DeviceManagementException
    {
        KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();
        KapuaResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_NOT_FOUND,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

}
