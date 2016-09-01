package org.eclipse.kapua.translator.jms.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.eclipse.kapua.transport.message.jms.JmsTopic;

@SuppressWarnings("rawtypes")
public class TranslatorDataJmsKura implements Translator<JmsMessage, KuraMessage>
{
    @Override
    @SuppressWarnings("unchecked")
    public KuraMessage translate(JmsMessage jmsMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraChannel kuraChannel = translate(jmsMessage.getTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(jmsMessage.getPayload());

        //
        // Return Kura message
        return new KuraMessage(kuraChannel,
                               jmsMessage.getReceivedOn(),
                               kuraPayload);

    }

    private KuraChannel translate(JmsTopic jmsTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = jmsTopic.getSplittedTopic();

        //
        // Return Kura Channel
        return new KuraResponseChannel(mqttTopicTokens[0],
                                       mqttTopicTokens[1]);
    }

    private KuraPayload translate(JmsPayload jmsPayload)
        throws KapuaException
    {
        byte[] jmsBody = jmsPayload.getBody();

        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.readFromByteArray(jmsBody);

        //
        // Return Kura Payload
        return kuraPayload;
    }

    @Override
    public Class<JmsMessage> getClassFrom()
    {
        return JmsMessage.class;
    }

    @Override
    public Class<KuraMessage> getClassTo()
    {
        return KuraMessage.class;
    }

}
