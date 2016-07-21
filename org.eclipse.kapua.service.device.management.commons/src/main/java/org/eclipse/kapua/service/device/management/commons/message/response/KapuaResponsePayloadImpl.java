package org.eclipse.kapua.service.device.management.commons.message.response;

import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.service.device.management.response.KapuaResponsePayload;

public class KapuaResponsePayloadImpl extends KapuaPayloadImpl implements KapuaResponsePayload
{
    public static final String PROPERTY_EXCEPTION_MSG   = "kapua.exception.message";
    public static final String PROPERTY_EXCEPTION_STACK = "kapua.exception.stack";

    @Override
    public String getExceptionMessage()
    {
        return (String) getProperties().get(PROPERTY_EXCEPTION_MSG);
    }

    @Override
    public void setExceptionMessage(String setExecptionMessage)
    {
        getProperties().put(PROPERTY_EXCEPTION_MSG, setExecptionMessage);
    }

    @Override
    public String getExceptionStack()
    {
        return (String) getProperties().get(PROPERTY_EXCEPTION_STACK);
    }

    @Override
    public void setExceptionStack(String setExecptionStack)
    {
        getProperties().put(PROPERTY_EXCEPTION_STACK, setExecptionStack);
    }

}
