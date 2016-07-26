package org.eclipse.kapua.service.device.registry.event;

import java.util.Date;

import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;

public interface DeviceEventCreator extends KapuaEntityCreator<DeviceEvent>
{
    public KapuaId getDeviceId();

    public void setDeviceId(KapuaId deviceId);

    public Date getSentOn();

    public void setSentOn(Date sentOn);

    public Date getReceivedOn();

    public void setReceivedOn(Date receivedOn);

    public KapuaPosition getPosition();

    public void setPosition(KapuaPosition position);

    public String getResource();

    public void setResource(String resource);

    public KapuaMethod getAction();

    public void setAction(KapuaMethod action);

    public KapuaResponseCode getResponseCode();

    public void setResponseCode(KapuaResponseCode responseCode);

    public String getEventMessage();

    public void setEventMessage(String eventMessage);
}
