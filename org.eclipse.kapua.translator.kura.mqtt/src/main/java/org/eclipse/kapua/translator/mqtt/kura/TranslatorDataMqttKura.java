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
public class TranslatorDataMqttKura implements Translator<MqttMessage, KuraMessage>
{
    @Override
    @SuppressWarnings("unchecked")
    public KuraMessage translate(MqttMessage mqttMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraChannel kuraChannel = translate(mqttMessage.getRequestTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(mqttMessage.getPayload());

        //
        // Return Kura message
        return new KuraMessage(kuraChannel,
                               mqttMessage.getTimestamp(),
                               kuraPayload);
    }

    private KuraChannel translate(MqttTopic mqttTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = mqttTopic.getSplittedTopic();

        //
        // Return Kura Channel
        return new KuraResponseChannel(mqttTopicTokens[0],
                                       mqttTopicTokens[1]);
    }

    private KuraPayload translate(MqttPayload mqttPayload)
        throws KapuaException
    {
        byte[] jmsBody = mqttPayload.getBody();

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(jmsBody);

        //
        // Return Kura Payload
        return kuraPayload;
    }

    @Override
    public Class<MqttMessage> getClassFrom()
    {
        return MqttMessage.class;
    }

    @Override
    public Class<KuraMessage> getClassTo()
    {
        return KuraMessage.class;
    }

}
