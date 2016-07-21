package org.org.eclipse.kapua.translator.jms.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.org.eclipse.kapua.transport.message.jms.JmsTopic;

@SuppressWarnings("rawtypes")
public class TranslatorDataJmsKura implements Translator<JmsTopic, KuraChannel, JmsPayload, KuraPayload, JmsMessage, KuraMessage>
{
    @Override
    public KuraMessage translate(JmsMessage message)
        throws KapuaException
    {
        //
        // Kura topic
        KuraChannel kuraChannel = translate(message.getTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(message.getPayload());

        //
        // Kura message
        @SuppressWarnings("unchecked")
        KuraMessage kuraMessage = new KuraMessage(kuraChannel,
                                                  message.getReceivedOn(),
                                                  kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraChannel translate(JmsTopic topic)
        throws KapuaException
    {
        String[] mqttTopicTokens = topic.getSplittedTopic();

        KuraChannel kuraResponseChannel = new KuraResponseChannel(mqttTopicTokens[0],
                                                                  mqttTopicTokens[1]);

        return kuraResponseChannel;
    }

    @Override
    public KuraPayload translate(JmsPayload payload)
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
        return JmsMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return KuraMessage.class;
    }

}
