package org.eclipse.kapua.translator.jms.kura;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.eclipse.kapua.transport.message.jms.JmsTopic;

public class TranslatorLifeMissingJmsKura extends Translator<JmsMessage, KuraMissingMessage>
{
    @Override
    public KuraMissingMessage translate(JmsMessage jmsMessage)
        throws KapuaException
    {
        return new KuraMissingMessage(translate(jmsMessage.getTopic()),
                                      jmsMessage.getReceivedOn(),
                                      translate(jmsMessage.getPayload().getBody()));
    }

    private KuraMissingChannel translate(JmsTopic jmsTopic)
        throws KapuaException
    {
        String[] topicTokens = jmsTopic.getSplittedTopic();
        // we shouldn't never get a shorter topic here (because that means we have issues on camel routing)
        // TODO check exception type
        if (topicTokens == null || topicTokens.length < 3) {
            throw new KapuaException(KapuaErrorCodes.INTERNAL_ERROR);
        }
        return new KuraMissingChannel(topicTokens[1],
                                      topicTokens[2]);
    }

    private KuraMissingPayload translate(byte[] jmsBody)
        throws KapuaException
    {
        KuraMissingPayload kuraMissingPayload = new KuraMissingPayload();
        kuraMissingPayload.readFromByteArray(jmsBody);
        return kuraMissingPayload;
    }

    @Override
    public Class<JmsMessage> getClassFrom()
    {
        return JmsMessage.class;
    }

    @Override
    public Class<KuraMissingMessage> getClassTo()
    {
        return KuraMissingMessage.class;
    }
}
