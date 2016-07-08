package org.eclipse.kapua.service.device.management.commons.message;

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.service.device.message.response.KapuaResponseCode;

public class KapuaResponsePayload extends KapuaPayload
{
    public static final String METRIC_RESPONSE_CODE   = "response.code";
    public static final String METRIC_EXCEPTION_MSG   = "response.exception.message";
    public static final String METRIC_EXCEPTION_STACK = "response.exception.stack";

    public KapuaResponsePayload(KapuaPayload kapuaPayload)
    {
        super(kapuaPayload);
    }

    public byte[] getResponseBody()
    {
        return super.getBody();
    }

    public KapuaResponseCode getResponseCode()
    {
        return KapuaResponseCode.fromResponseCode((String) getMetric(METRIC_RESPONSE_CODE));
    }

    public String getExceptionMessage()
    {
        return (String) getMetric(METRIC_EXCEPTION_MSG);
    }

    public String getExceptionStack()
    {
        return (String) getMetric(METRIC_EXCEPTION_STACK);
    }
}
