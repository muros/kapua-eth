package org.org.eclipse.kapua.transport.message.jms;

import org.eclipse.kapua.transport.message.TransportPayload;

public class JmsPayload implements TransportPayload
{
    private byte[] body;

    public JmsPayload(byte[] body)
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
