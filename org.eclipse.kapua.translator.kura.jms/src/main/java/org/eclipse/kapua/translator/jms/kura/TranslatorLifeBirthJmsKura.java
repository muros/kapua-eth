package org.eclipse.kapua.translator.jms.kura;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraBirthChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraBirthMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraBirthPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.eclipse.kapua.transport.message.jms.JmsTopic;

public class TranslatorLifeBirthJmsKura extends Translator<JmsMessage, KuraBirthMessage>
{
    @Override
    public KuraBirthMessage translate(JmsMessage jmsMessage)
        throws KapuaException
    {
        return new KuraBirthMessage(translate(jmsMessage.getTopic()),
                                    jmsMessage.getReceivedOn(),
                                    translate(jmsMessage.getPayload().getBody()));
    }

    private KuraBirthChannel translate(JmsTopic jmsTopic)
        throws KapuaException
    {
        String[] topicTokens = jmsTopic.getSplittedTopic();
        // we shouldn't never get a shorter topic here (because that means we have issues on camel routing)
        // TODO check exception type
        if (topicTokens == null || topicTokens.length < 3) {
            throw new KapuaException(KapuaErrorCodes.INTERNAL_ERROR);
        }
        return new KuraBirthChannel(topicTokens[1],
                                    topicTokens[2]);
    }

    private KuraBirthPayload translate(byte[] jmsBody)
        throws KapuaException
    {
        KuraBirthPayload kuraBirthPayload = new KuraBirthPayload();
        kuraBirthPayload.readFromByteArray(jmsBody);
        return kuraBirthPayload;
    }

    @Override
    public Class<JmsMessage> getClassFrom()
    {
        return JmsMessage.class;
    }

    @Override
    public Class<KuraBirthMessage> getClassTo()
    {
        return KuraBirthMessage.class;
    }

}
