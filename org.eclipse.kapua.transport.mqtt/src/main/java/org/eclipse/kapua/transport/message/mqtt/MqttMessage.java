package org.eclipse.kapua.transport.message.mqtt;

import java.util.Date;

import org.eclipse.kapua.transport.message.pubsub.PubSubTransportMessage;

// FIXME: add QoS
public class MqttMessage implements PubSubTransportMessage<MqttTopic, MqttPayload>
{
    private MqttTopic   requestTopic;

    private MqttTopic   responseTopic;

    private Date        timestamp;

    private MqttPayload payload;

    public MqttMessage(MqttTopic requestTopic, MqttTopic responseTopic, MqttPayload mqttPayload)
    {
        this(requestTopic, (Date) null, mqttPayload);
        this.responseTopic = responseTopic;
    }

    public MqttMessage(MqttTopic requestTopic, Date receivedOn, MqttPayload payload)
    {
        this.requestTopic = requestTopic;
        this.timestamp = receivedOn;
        this.payload = payload;
    }

    public MqttTopic getRequestTopic()
    {
        return requestTopic;
    }

    public void setRequestTopic(MqttTopic requestTopic)
    {
        this.requestTopic = requestTopic;
    }

    public MqttTopic getResponseTopic()
    {
        return responseTopic;
    }

    public void setResponseTopic(MqttTopic responseTopic)
    {
        this.responseTopic = responseTopic;
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
