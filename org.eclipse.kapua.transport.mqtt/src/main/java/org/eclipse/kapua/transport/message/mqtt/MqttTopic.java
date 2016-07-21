package org.eclipse.kapua.transport.message.mqtt;

import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSettingKeys;

public class MqttTopic implements TransportChannel
{
    private static String topicSeparator = MqttClientSetting.getInstance().getString(MqttClientSettingKeys.TRANSPORT_TOPIC_SEPARATOR);

    private String        topic;

    public MqttTopic(String topic)
    {
        this.topic = topic;
    }

    public MqttTopic(String[] topicParts)
    {
        //
        // Concatenate topic parts
        StringBuilder sb = new StringBuilder();
        for (String s : topicParts) {
            sb.append(s)
              .append(topicSeparator);
        }
        sb.deleteCharAt(sb.length() - topicSeparator.length());

        this.topic = sb.toString();
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String[] getSplittedTopic()
    {
        return topic.split(topicSeparator);
    }
}
