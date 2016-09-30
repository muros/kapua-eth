/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.translator.jms.kura;

import java.util.Arrays;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataChannel;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage;
import org.eclipse.kapua.service.device.call.message.kura.data.KuraDataPayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.eclipse.kapua.transport.message.jms.JmsTopic;

public class TranslatorDataJmsKura extends Translator<JmsMessage, KuraDataMessage>
{
    @Override
    public KuraDataMessage translate(JmsMessage jmsMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraDataChannel kuraChannel = translate(jmsMessage.getTopic());

        //
        // Kura payload
        KuraDataPayload kuraPayload = translate(jmsMessage.getPayload());

        //
        // Return Kura message
        return new KuraDataMessage(kuraChannel,
                                   jmsMessage.getReceivedOn(),
                                   kuraPayload);

    }

    private KuraDataChannel translate(JmsTopic jmsTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = jmsTopic.getSplittedTopic();

        KuraDataChannel kuraDataChannel = new KuraDataChannel();
        kuraDataChannel.setScope(mqttTopicTokens[0]);
        kuraDataChannel.setClientId(mqttTopicTokens[1]);
        kuraDataChannel.setSemanticChannelParts(Arrays.asList(mqttTopicTokens).subList(2, mqttTopicTokens.length));

        //
        // Return Kura Channel
        return kuraDataChannel;
    }

    private KuraDataPayload translate(JmsPayload jmsPayload)
        throws KapuaException
    {
        KuraDataPayload kuraPayload = null;

        if (jmsPayload.getBody() != null) {
            kuraPayload = new KuraDataPayload();
            kuraPayload.readFromByteArray(jmsPayload.getBody());
        }

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
    public Class<KuraDataMessage> getClassTo()
    {
        return KuraDataMessage.class;
    }

}
