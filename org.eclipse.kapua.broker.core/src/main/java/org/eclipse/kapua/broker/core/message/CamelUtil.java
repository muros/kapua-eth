package org.eclipse.kapua.broker.core.message;

import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.plugin.AclConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelUtil {
	
	public static final Logger logger = LoggerFactory.getLogger(CamelUtil.class);
	
	public static String getTopic(org.apache.camel.Message message) throws JMSException {
		String topicOrig =  message.getHeader(MessageConstants.PROPERTY_TOPIC_ORIG, String.class);
		if (topicOrig!=null) {
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