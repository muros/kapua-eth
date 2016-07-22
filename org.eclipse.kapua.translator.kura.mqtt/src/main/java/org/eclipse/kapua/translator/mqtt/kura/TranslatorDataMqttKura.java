package org.eclipse.kapua.translator.mqtt.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

@SuppressWarnings("rawtypes")
public class TranslatorDataMqttKura implements Translator<MqttTopic, KuraChannel, MqttPayload, KuraPayload, MqttMessage, KuraMessage>
{
    @Override
    public KuraMessage translate(MqttMessage message)
        throws KapuaException
    {
        //
        // Kura topic
        KuraChannel kuraChannel = translate(message.getRequestTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(message.getPayload());

        //
        // Kura message
        @SuppressWarnings("unchecked")
        KuraMessage kuraMessage = new KuraMessage(kuraChannel,
                                                  message.getTimestamp(),
                                                  kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraChannel translate(MqttTopic topic)
        throws KapuaException
    {
        String[] mqttTopicTokens = topic.getSplittedTopic();

        KuraChannel kuraResponseChannel = new KuraResponseChannel(mqttTopicTokens[0],
                                                                  mqttTopicTokens[1]);

        return kuraResponseChannel;
    }

    @Override
    public KuraPayload translate(MqttPayload payload)
        throws KapuaException
    {
        byte[] jmsBody = payload.getBody();

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(jmsBody);

        return kuraPayload;
    }

    @Override
    public Class<?> getClassFrom()
    {
        return MqttMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return KuraMessage.class;
    }

}
