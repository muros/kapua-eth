package org.eclipse.kapua.translator.jms.kura;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.eclipse.kapua.transport.message.jms.JmsTopic;

public class TranslatorLifeDisconnectJmsKura extends Translator<JmsMessage, KuraDisconnectMessage>
{
    @Override
    public KuraDisconnectMessage translate(JmsMessage jmsMessage)
        throws KapuaException
    {
        return new KuraDisconnectMessage(translate(jmsMessage.getTopic()),
                                         jmsMessage.getReceivedOn(),
                                         translate(jmsMessage.getPayload().getBody()));
    }

    private KuraDisconnectChannel translate(JmsTopic jmsTopic)
        throws KapuaException
    {
        String[] topicTokens = jmsTopic.getSplittedTopic();
        // we shouldn't never get a shorter topic here (because that means we have issues on camel routing)
        // TODO check exception type
        if (topicTokens == null || topicTokens.length < 3) {
            throw new KapuaException(KapuaErrorCodes.INTERNAL_ERROR);
        }
        return new KuraDisconnectChannel(topicTokens[1],
                                         topicTokens[2]);
    }

    private KuraDisconnectPayload translate(byte[] jmsBody)
        throws KapuaException
    {
        KuraDisconnectPayload kuraDisconnectPayload = new KuraDisconnectPayload();
        kuraDisconnectPayload.readFromByteArray(jmsBody);
        return kuraDisconnectPayload;
    }

    @Override
    public Class<JmsMessage> getClassFrom()
    {
        return JmsMessage.class;
    }

    @Override
    public Class<KuraDisconnectMessage> getClassTo()
    {
        return KuraDisconnectMessage.class;
    }

}