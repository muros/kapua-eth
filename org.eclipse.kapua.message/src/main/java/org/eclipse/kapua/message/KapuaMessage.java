package org.eclipse.kapua.message;

import java.util.Date;

public interface KapuaMessage<D extends KapuaChannel, P extends KapuaPayload> extends Message
{
    public Date getReceivedOn();

    public void setReceivedOn(Date receivedOn);

    public Date getSentOn();

    public void setSentOn(Date sentOn);

    public D getChannel();

    public void setChannel(D channel);

    public P getPayload();

    public void setPayload(P payload);

    public KapuaPosition getPosition();

    public void setPosition();
}
