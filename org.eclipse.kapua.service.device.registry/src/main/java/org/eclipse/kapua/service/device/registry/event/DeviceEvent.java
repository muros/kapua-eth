package org.eclipse.kapua.service.device.registry.event;

import java.util.Date;

import org.eclipse.kapua.message.internal.KapuaPosition;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceEvent extends KapuaEntity
{
    public static final String TYPE = "dvce-event";

    default public String getType()
    {
        return TYPE;
    }

    public KapuaId getDeviceId();

    public void setDeviceId(KapuaId deviceId);

    public Date getSentOn();

    public void setSentOn(Date sentOn);

    public Date getReceivedOn();

    public void setReceivedOn(Date receivedOn);

    public String getEventType();

    public void setEventType(String eventType);

    public String getEventMessage();

    public void setEventMessage(String eventMessage);

    public KapuaPosition getPosition();

    public void setPosition(KapuaPosition position);
}
