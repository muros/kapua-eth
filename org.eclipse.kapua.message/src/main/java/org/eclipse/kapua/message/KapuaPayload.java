package org.eclipse.kapua.message;

import java.util.Map;

public interface KapuaPayload extends Payload
{
    public Map<String, Object> getMetrics();

    public byte[] getBody();

    public String toDisplayString();
}
