package org.eclipse.kapua.service.device.call.kura;

public enum ResponseMetrics
{
    RESP_METRIC_EXIT_CODE("response.code"),
    RESP_METRIC_EXCEPTION_MESSAGE("response.exception.message"),
    RESP_METRIC_EXCEPTION_STACK("response.exception.stack"),
    ;

    private String value;

    ResponseMetrics(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
