package org.eclipse.kapua.transport.message.mqtt;

import java.util.Date;

import org.eclipse.kapua.transport.message.TransportMessage;

// FIXME: add QoS
public class MqttMessage implements TransportMessage<MqttTopic, MqttPayload>
{
    private MqttTopic   topic;

    private Date        timestamp;

    private MqttPayload payload;

    public MqttMessage(MqttTopic topic, MqttPayload mqttPayload)
    {
        this(topic, null, mqttPayload);
    }

    public MqttMessage(MqttTopic topic, Date receivedOn, MqttPayload payload)
    {
        this.topic = topic;
        this.timestamp = receivedOn;
        this.payload = payload;
    }

    public MqttTopic getTopic()
    {
        return topic;
    }

    public void setTopic(MqttTopic topic)
    {
        this.topic = topic;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public MqttPayload getPayload()
    {
        return payload;
    }

    public void setPayload(MqttPayload payload)
    {
        this.payload = payload;
    }
}
