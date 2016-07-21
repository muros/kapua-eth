package org.eclipse.kapua.message;

import java.util.Map;

public interface KapuaPayload extends Payload
{
    public Map<String, Object> getProperties();

    public void setProperties(Map<String, Object> metrics);

    public byte[] getBody();

    public void setBody(byte[] body);

    public String toDisplayString();
}
