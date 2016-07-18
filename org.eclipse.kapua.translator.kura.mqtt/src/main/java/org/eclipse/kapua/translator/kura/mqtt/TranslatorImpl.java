package org.eclipse.kapua.translator.kura.mqtt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class TranslatorImpl implements Translator<KuraChannel, MqttTopic, KuraPayload, MqttPayload, KuraMessage, MqttMessage>
{

    @Override
    public MqttMessage translate(KuraMessage message)
        throws KapuaException
    {
        //
        // Mqtt topic
        MqttTopic mqttTopic = translate(message.getChannel());

        //
        // Mqtt payload
        MqttPayload mqttPayload = translate(message.getPayload());

        //
        // Mqtt message
        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  message.timestamp(),
                                                  mqttPayload);

        //
        // Return result
        return mqttMessage;
    }

    @Override
    public MqttTopic translate(KuraChannel channel)
        throws KapuaException
    {
        List<String> topicTokens = new ArrayList<String>();

        if (channel.getMessageClassification() != null) {
            topicTokens.add(channel.getMessageClassification());
        }

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());

        if (channel.getSemanticChannel() != null &&
            !channel.getSemanticChannel().isEmpty()) {
            topicTokens.addAll(channel.getSemanticChannel());
        }

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    @Override
    public MqttPayload translate(KuraPayload kuraPayload)
        throws KapuaException
    {
        return new MqttPayload(kuraPayload.toByteArray());
    }

    @Override
    public Class<?> getClassFrom()
    {
        return KuraMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return MqttMessage.class;
    }
}
