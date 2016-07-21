package org.org.eclipse.kapua.transport.message.jms;

import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.transport.message.TransportMessage;

public class JmsMessage implements TransportMessage<JmsTopic, JmsPayload>
{
    private JmsTopic            topic;

    private Date                receivedOn;

    private JmsPayload          payload;

    private Map<String, String> properties;

    public JmsMessage(JmsTopic topic, Date receivedOn, JmsPayload payload)
    {
        this.topic = topic;
        this.receivedOn = receivedOn;
        this.payload = payload;
    }

    public JmsTopic getTopic()
    {
        return topic;
    }

    public void setTopic(JmsTopic topic)
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

    public JmsPayload getPayload()
    {
        return payload;
    }

    public void setPayload(JmsPayload payload)
    {
        this.payload = payload;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

}
