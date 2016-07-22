package org.eclipse.kapua.service.device.management.commons.message.response;

import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.service.device.management.ResponseProperties;
import org.eclipse.kapua.service.device.management.response.KapuaResponsePayload;

public class KapuaResponsePayloadImpl extends KapuaPayloadImpl implements KapuaResponsePayload
{

    @Override
    public String getExceptionMessage()
    {
        return (String) getProperties().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue());
    }

    @Override
    public void setExceptionMessage(String setExecptionMessage)
    {
        getProperties().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue(), setExecptionMessage);
    }

    @Override
    public String getExceptionStack()
    {
        return (String) getProperties().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue());
    }

    @Override
    public void setExceptionStack(String setExecptionStack)
    {
        getProperties().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue(), setExecptionStack);
    }

}
