package org.eclipse.kapua.transport.message.mqtt;

import org.eclipse.kapua.transport.message.TransportPayload;

public class MqttPayload implements TransportPayload
{
    private byte[] body;

    public MqttPayload(byte[] body)
    {
        this.body = body;
    }

    public byte[] getBody()
    {
        return body;
    }

    public void setBody(byte[] body)
    {
        this.body = body;
    }
}
