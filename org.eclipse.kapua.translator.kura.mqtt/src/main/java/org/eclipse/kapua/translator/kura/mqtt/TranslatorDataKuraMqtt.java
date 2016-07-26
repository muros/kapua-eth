package org.eclipse.kapua.translator.kura.mqtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

@SuppressWarnings("rawtypes")
public class TranslatorDataKuraMqtt implements Translator<KuraMessage, MqttMessage>
{
    @Override
    public MqttMessage translate(KuraMessage message)
        throws KapuaException
    {
        //
        // Jms request topic
        MqttTopic jmsRequestTopic = translate(message.getChannel());

        //
        // Jms payload
        MqttPayload jmsPayload = translate(message.getPayload());

        //
        // Jms message
        MqttMessage jmsMessage = new MqttMessage(jmsRequestTopic,
                                                 new Date(),
                                                 jmsPayload);

        //
        // Return result
        return jmsMessage;
    }

    private MqttTopic translate(KuraChannel channel)
        throws KapuaException
    {
        List<String> topicTokens = new ArrayList<>();

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());

        if (channel.getSemanticChannelParts() != null &&
            !channel.getSemanticChannelParts().isEmpty()) {
            topicTokens.addAll(channel.getSemanticChannelParts());
        }

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    private MqttPayload translate(KuraPayload payload)
        throws KapuaException
    {
        return new MqttPayload(payload.toByteArray());
    }

    @Override
    public Class<KuraMessage> getClassFrom()
    {
        return KuraMessage.class;
    }

    @Override
    public Class<MqttMessage> getClassTo()
    {
        return MqttMessage.class;
    }

}
