/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.commons.call;

import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponsePayload;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseCode;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;

public abstract class AbstractDeviceApplicationCallResponseHandler<T>
{
    @SuppressWarnings("rawtypes")
    public T handle(DeviceResponseMessage responseMessage)
        throws DeviceManagementException
    {
        T responseObject = null;
        if (responseMessage != null) {
            DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();
            if (responsePayload != null) {
                KuraResponseCode responseCode = responsePayload.getResponseCode();

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

    @SuppressWarnings("rawtypes")
    protected abstract T handleAcceptedRequest(DeviceResponseMessage responseMessage)
        throws DeviceManagementException;

    @SuppressWarnings("rawtypes")
    protected void handleBadRequestReply(DeviceResponseMessage responseMessage)
        throws DeviceManagementException
    {
        DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();
        KuraResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_BAD_REQUEST,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

    @SuppressWarnings("rawtypes")
    protected void handleDeviceInternalErrorReply(DeviceResponseMessage responseMessage)
        throws DeviceManagementException
    {
        DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();
        KuraResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_INTERNAL_ERROR,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

    @SuppressWarnings("rawtypes")
    protected void handleNotFoundReply(DeviceResponseMessage responseMessage)
        throws DeviceManagementException
    {
        DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();
        KuraResponseCode responseCode = responsePayload.getResponseCode();

        throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_NOT_FOUND,
                                            null,
                                            new Object[] { responseCode,
                                                           responsePayload.getExceptionMessage(),
                                                           responsePayload.getExceptionStack() });
    }

}
