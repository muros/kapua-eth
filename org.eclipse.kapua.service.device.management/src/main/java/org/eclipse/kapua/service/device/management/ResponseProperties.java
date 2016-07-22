package org.eclipse.kapua.service.device.management;

public enum ResponseProperties
{
    RESP_PROPERTY_EXCEPTION_MESSAGE("kapua.response.exception.message"),
    RESP_PROPERTY_EXCEPTION_STACK("kapua.response.exception.stack"),
    ;

    private String value;

    ResponseProperties(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
