package org.eclipse.kapua.message;

import java.util.Date;
import java.util.UUID;

import org.eclipse.kapua.model.id.KapuaId;

public interface KapuaMessage<C extends KapuaChannel, P extends KapuaPayload> extends Message
{
    public UUID getId();

    public void setId(UUID id);

    public KapuaId getScopeId();

    public void setScopeId(KapuaId scopeId);

    public KapuaId getDeviceId();

    public void setDeviceId(KapuaId deviceId);

    public Date getReceivedOn();

    public void setReceivedOn(Date receivedOn);

    public Date getSentOn();

    public void setSentOn(Date sentOn);

    public Date getCapturedOn();

    public void setCapturedOn(Date capturedOn);

    public KapuaPosition getPosition();

    public void setPosition(KapuaPosition position);

    public C getSemanticChannel();

    public void setSemanticChannel(C semanticChannel);

    public P getPayload();

    public void setPayload(P payload);

}
