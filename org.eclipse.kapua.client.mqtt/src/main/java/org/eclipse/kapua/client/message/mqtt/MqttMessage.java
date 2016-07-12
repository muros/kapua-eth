package org.eclipse.kapua.client.message.mqtt;

import java.util.Date;

import org.eclipse.kapua.client.message.KapuaMessage;

public class MqttMessage implements KapuaMessage<MqttTopic, MqttPayload>
{
    private MqttTopic   topic;

    private Date        receivedOn;

    private MqttPayload payload;

    public MqttMessage(MqttTopic topic, Date receivedOn, MqttPayload payload)
    {
        this.topic = topic;
        this.receivedOn = receivedOn;
        this.payload = payload;
    }

    @Override
    public MqttTopic getDestination()
    {
        return topic;
    }

    @Override
    public void setDestination(MqttTopic topic)
    {
        this.topic = topic;
    }

    public Date getReceivedOn()
    {
        return receivedOn;
    }

    public void setReceivedOn(Date receivedOn)
    {
        this.receivedOn = receivedOn;
    }

    @Override
    public MqttPayload getPayload()
    {
        return payload;
    }

    @Override
    public void setPayload(MqttPayload payload)
    {
        this.payload = payload;
    }

}
