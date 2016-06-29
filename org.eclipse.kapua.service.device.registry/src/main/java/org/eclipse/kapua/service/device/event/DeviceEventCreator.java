package org.eclipse.kapua.service.device.event;

import java.util.Date;

import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceEventCreator extends KapuaEntityCreator<DeviceEvent>
{
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
