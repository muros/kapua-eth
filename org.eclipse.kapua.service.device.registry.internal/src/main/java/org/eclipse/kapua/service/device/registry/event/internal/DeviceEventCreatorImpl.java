package org.eclipse.kapua.service.device.registry.event.internal;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.message.internal.KapuaPosition;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;

public class DeviceEventCreatorImpl extends AbstractKapuaEntityCreator<DeviceEvent> implements DeviceEventCreator
{
    private static final long serialVersionUID = -3982569213440658172L;

    @XmlElement(name = "deviceId")
    private KapuaId           deviceId;

    @XmlElement(name = "receivedOn")
    private Date              receivedOn;

    @XmlElement(name = "sentOn")
    private Date              sentOn;

    @XmlElement(name = "eventType")
    private String            eventType;

    @XmlElement(name = "eventMessage")
    private String            eventMessage;

    @XmlElement(name = "position")
    private KapuaPosition     position;

    protected DeviceEventCreatorImpl(KapuaId scopeId)
    {
        super(scopeId);
    }

    @Override
    public KapuaId getDeviceId()
    {
        return deviceId;
    }

    @Override
    public void setDeviceId(KapuaId deviceId)
    {
        this.deviceId = deviceId;
    }

    @Override
    public Date getSentOn()
    {
        return sentOn;
    }

    @Override
    public void setSentOn(Date sentOn)
    {
        this.sentOn = sentOn;
    }

    @Override
    public Date getReceivedOn()
    {
        return receivedOn;
    }

    @Override
    public void setReceivedOn(Date receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    @Override
    public String getEventType()
    {
        return eventType;
    }

    @Override
    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    @Override
    public String getEventMessage()
    {
        return eventMessage;
    }

    @Override
    public void setEventMessage(String eventMessage)
    {
        this.eventMessage = eventMessage;
    }

    @Override
    public KapuaPosition getPosition()
    {
        return position;
    }

    @Override
    public void setPosition(KapuaPosition position)
    {
        this.position = position;
    }
}
