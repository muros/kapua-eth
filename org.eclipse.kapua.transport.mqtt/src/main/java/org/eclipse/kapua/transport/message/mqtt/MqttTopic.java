/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
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
