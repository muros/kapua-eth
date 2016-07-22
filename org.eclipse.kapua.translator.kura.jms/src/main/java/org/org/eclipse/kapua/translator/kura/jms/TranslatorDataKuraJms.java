package org.org.eclipse.kapua.translator.kura.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.org.eclipse.kapua.transport.message.jms.JmsTopic;

@SuppressWarnings("rawtypes")
public class TranslatorDataKuraJms implements Translator<KuraMessage, JmsMessage>
{

    @Override
    public JmsMessage translate(KuraMessage message)
        throws KapuaException
    {
        //
        // Jms request topic
        JmsTopic jmsRequestTopic = translate(message.getChannel());

        //
        // Jms payload
        JmsPayload jmsPayload = translate(message.getPayload());

        //
        // Jms message
        JmsMessage jmsMessage = new JmsMessage(jmsRequestTopic,
                                               new Date(),
                                               jmsPayload);

        //
        // Return result
        return jmsMessage;
    }

    public JmsTopic translate(KuraChannel channel)
        throws KapuaException
    {
        List<String> topicTokens = new ArrayList<String>();

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());

        if (channel.getSemanticChannelParts() != null &&
            !channel.getSemanticChannelParts().isEmpty()) {
            topicTokens.addAll(channel.getSemanticChannelParts());
        }

        return new JmsTopic(topicTokens.toArray(new String[0]));
    }

    public JmsPayload translate(KuraPayload payload)
        throws KapuaException
    {
        return new JmsPayload(payload.toByteArray());
    }

    @Override
    public Class<KuraMessage> getClassFrom()
    {
        return KuraMessage.class;
    }

    @Override
    public Class<JmsMessage> getClassTo()
    {
        return JmsMessage.class;
    }

}
