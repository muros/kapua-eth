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
package org.eclipse.kapua.broker.core.message;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.camel.Message;
import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.plugin.AclConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Camel utility methods
 */
public class CamelUtil
{

    public static final Logger logger = LoggerFactory.getLogger(CamelUtil.class);

    /**
     * Extract the topic from the {@link Message} looking for Kapua header property that handles this value and inspecting Camel header properties)
     * 
     * @param message
     * @return
     * @throws JMSException
     */
    public static String getTopic(org.apache.camel.Message message) throws JMSException
    {
        String topicOrig = message.getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class);
        if (topicOrig != null) {
            return topicOrig;
        }
        else {
            ActiveMQDestination destination = message.getHeader(CamelConstants.JMS_HEADER_DESTINATION, ActiveMQDestination.class);
            if (destination instanceof ActiveMQTopic) {
                ActiveMQTopic destinationTopic = (ActiveMQTopic) destination;
                return destinationTopic.getTopicName().substring(AclConstants.VT_TOPIC_PREFIX.length());
            }
            else {
                logger.warn("jmsMessage destination is not a Topic or Queue: {}", destination.toString());
                throw new JMSException("Unable to extract the destination. Wrong destination {}", destination.toString());
            }
        }
    }

}
