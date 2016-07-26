package org.eclipse.kapua.service.device.registry.event.internal;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;

@Entity
@Table(name = "dvc_device_event")
public class DeviceEventImpl extends AbstractKapuaEntity implements DeviceEvent
{
    private static final long serialVersionUID = 7142819355352738950L;

    @XmlElement(name = "deviceId")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "device_id", nullable = false, updatable = false))
    })
    private KapuaEid          deviceId;

    @XmlElement(name = "receivedOn")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "received_on", updatable = false, nullable = false)
    private Date              receivedOn;

    @XmlElement(name = "sentOn")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_on", updatable = false)
    private Date              sentOn;

    @XmlElement(name = "position")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "longitude", column = @Column(name = "pos_longitude", updatable = false)),
                          @AttributeOverride(name = "latitude", column = @Column(name = "pos_latitude", updatable = false)),
                          @AttributeOverride(name = "altitude", column = @Column(name = "pos_altitude", updatable = false)),
                          @AttributeOverride(name = "precision", column = @Column(name = "pos_precision", updatable = false)),
                          @AttributeOverride(name = "heading", column = @Column(name = "pos_heading", updatable = false)),
                          @AttributeOverride(name = "speed", column = @Column(name = "pos_speed", updatable = false)),
                          @AttributeOverride(name = "timestamp", column = @Column(name = "pos_timestamp", updatable = false)),
                          @AttributeOverride(name = "satellites", column = @Column(name = "pos_satellites", updatable = false)),
                          @AttributeOverride(name = "status", column = @Column(name = "pos_status", updatable = false))
    })
    private KapuaPosition     position;

    @XmlElement(name = "resource")
    @Basic
    @Column(name = "resource", updatable = false, nullable = false)
    private String            resource;

    @XmlElement(name = "action")
    @Enumerated(EnumType.STRING)
    @Column(name = "action", updatable = false, nullable = false)
    private KapuaMethod       action;

    @XmlElement(name = "responseCode")
    @Enumerated(EnumType.STRING)
    @Column(name = "responseCode", updatable = false, nullable = false)
    private KapuaResponseCode responseCode;

    @XmlElement(name = "eventMessage")
    @Lob
    @Column(name = "event_message", updatable = false, nullable = false)
    private String            eventMessage;

    public DeviceEventImpl(KapuaId scopeId)
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
        this.deviceId = (KapuaEid) deviceId;
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
    public KapuaPosition getPosition()
    {
        return position;
    }

    @Override
    public void setPosition(KapuaPosition position)
    {
        this.position = position;
    }

    @Override
    public String getResource()
    {
        return resource;
    }

    @Override
    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public KapuaMethod getAction()
    {
        return action;
    }

    public void setAction(KapuaMethod action)
    {
        this.action = action;
    }

    public KapuaResponseCode getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(KapuaResponseCode responseCode)
    {
        this.responseCode = responseCode;
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

}
