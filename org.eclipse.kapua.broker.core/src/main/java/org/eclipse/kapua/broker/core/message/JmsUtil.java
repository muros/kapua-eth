package org.eclipse.kapua.broker.core.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQMessage;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.converter.KapuaConverter;
import org.eclipse.kapua.broker.core.plugin.AclConstants;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.broker.core.util.ReflectUtil;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.transport.message.jms.JmsMessage;
import org.org.eclipse.kapua.transport.message.jms.JmsPayload;
import org.org.eclipse.kapua.transport.message.jms.JmsTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsUtil {
	
	public static final Logger logger = LoggerFactory.getLogger(JmsUtil.class);
	
	@SuppressWarnings("rawtypes")
    private final static Map<String, Translator<JmsMessage, DeviceMessage>> translatorFromJmsMap = new HashMap<String, Translator<JmsMessage, DeviceMessage>>();
	@SuppressWarnings("rawtypes")
	private final static Map<String, Translator<DeviceMessage, KapuaMessage>> translatorToKapuaMap = new HashMap<String, Translator<DeviceMessage, KapuaMessage>>();
    
	@SuppressWarnings("rawtypes")
    private final static Map<String, Translator<KapuaMessage, DeviceMessage>> translatorFromKapuaMap = new HashMap<String, Translator<KapuaMessage, DeviceMessage>>();
	@SuppressWarnings("rawtypes")
	private final static Map<String, Translator<DeviceMessage, JmsMessage>> translatorToJmsMap = new HashMap<String, Translator<DeviceMessage, JmsMessage>>();
    
	/**
     * Return the topic for the message's destination
     * @param jmsMessage
     * @return
     * @throws JMSException 
     */
    public static String getJmsTopic(ActiveMQMessage jmsMessage) throws JMSException
    {
    	String jmsTopic = null;
		if (jmsMessage.getDestination().isTopic()) {
			jmsTopic = ((Topic) jmsMessage.getJMSDestination()).getTopicName().substring(AclConstants.VT_TOPIC_PREFIX.length());
		}
		else if (jmsMessage.getDestination().isQueue()) {
			jmsTopic = jmsMessage.getStringProperty(MessageConstants.PROPERTY_TOPIC_ORIG);
		}
		else {
			logger.warn("jmsMessage destination is not a Topic or Queue: {}", jmsMessage.getDestination());
		}
		return jmsTopic;
    }
    
    /**
     * Convert a jms byte message to CamelKapuaMessage
     * 
     * @param jmsMessage
     * @throws JMSException
     * @throws KapuaException 
     * @throws KapuaInvalidTopicException
     */
    public static CamelKapuaMessage<?> convertToKapuaMessage(ConnectorDescriptor connectorDescriptor, BytesMessage jmsMessage, KapuaId connectionId)
        throws JMSException, KapuaException
    {
        String jmsTopic = jmsMessage.getStringProperty(MessageConstants.PROPERTY_TOPIC_ORIG);
        Date queuedOn = new Date(jmsMessage.getLongProperty(MessageConstants.PROPERTY_ENQUEUED_TIMESTAMP));
        return convertToKapuaMessage(connectorDescriptor, jmsMessage, jmsTopic, queuedOn, connectionId);
    }
    
    /**
     * Convert a jms byte message to KapuaMessage
     * 
     * this code 
     * if (jmsMessage.getBodyLength() > 0) {
     * 		payload = new byte[(int) jmsMessage.getBodyLength()];
     * 		jmsMessage.readBytes(payload);
     * }
     * with camel doesn't work. The call getBodyLength returns the correct message size but the read call reads an empty array (-1 is returned).
	 * The following code return the payload evaluated. 
	 * ((ActiveMQMessage)jmsMessage).getContent().data
	 * so we modify the method assuming that camel converter called this utility method with a byte[] representing the jms body message
	 * see {@link KapuaConverter}
	 * 
	 * TODO check the code with huge messages
     * 
     * @param jmsMessage
     * @throws JMSException
     * @throws KapuaException 
     * @throws KapuaInvalidTopicException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static CamelKapuaMessage<?> convertToKapuaMessage(ConnectorDescriptor connectorDescriptor, BytesMessage jmsMessage, String jmsTopic, Date queuedOn, KapuaId connectionId)
        throws JMSException, KapuaException
    {
        byte[] payload = null;
        // TODO JMS message have no size limits!
        if (jmsMessage.getBodyLength() > 0) {
            payload = new byte[(int) jmsMessage.getBodyLength()];
            int readBytes = jmsMessage.readBytes(payload);
            logger.debug("Message conversion... {} bytes read!", readBytes);
        }
        KapuaMessage<?, ?> kapuaMessage = convertToKapuaMessage(connectorDescriptor, payload, jmsTopic, queuedOn, connectionId);
		return new CamelKapuaMessage(kapuaMessage, connectionId, connectorDescriptor);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static CamelKapuaMessage<?> convertToCamelKapuaMessage(ConnectorDescriptor connectorDescriptor, byte[] messageBody, String jmsTopic, Date queuedOn, KapuaId connectionId)
            throws KapuaException
        {
    	KapuaMessage<?, ?> kapuaMessage = convertToKapuaMessage(connectorDescriptor, messageBody, jmsTopic, queuedOn, connectionId);
 		return new CamelKapuaMessage(kapuaMessage, connectionId, connectorDescriptor);
    }
    
	@SuppressWarnings("rawtypes")
	private static KapuaMessage<?, ?> convertToKapuaMessage(ConnectorDescriptor connectorDescriptor, byte[] messageBody, String jmsTopic, Date queuedOn, KapuaId connectionId)
            throws KapuaException
        {
    	//first step... from jms message to device dependent protocol (unknown)
    	String protocol = connectorDescriptor.getDeviceProtocolName();
    	Translator<JmsMessage, DeviceMessage> translatorFromJms = translatorFromJmsMap.get(connectorDescriptor.getDeviceProtocolName());
		if (translatorFromJms==null) {
			//lookup
			translatorFromJms = Translator.getTranslatorFor(JmsMessage.class, ReflectUtil.create(DeviceMessage.class, connectorDescriptor.getDeviceProtocolImplementationClass()));
			translatorFromJmsMap.put(protocol, translatorFromJms);
		}
		DeviceMessage middleMessage = translatorFromJms.translate(new JmsMessage(new JmsTopic(jmsTopic), queuedOn, new JmsPayload(messageBody)));

		//second step.... from device dependent protocol (unknown) to Kapua
		Translator<DeviceMessage, KapuaMessage> translatorToKapua = translatorToKapuaMap.get(connectorDescriptor.getDeviceProtocolName());
		if (translatorToKapua==null) {
			//lookup
			translatorToKapua = Translator.getTranslatorFor(DeviceMessage.class, ReflectUtil.create(KapuaMessage.class, connectorDescriptor.getDeviceProtocolImplementationClass()));
			translatorToKapuaMap.put(protocol, translatorToKapua);
		}
		return translatorToKapua.translate(middleMessage);
        }
    	
    
	@SuppressWarnings("rawtypes")
	public static JmsMessage convertToJmsMessage(ConnectorDescriptor connectorDescriptor, KapuaMessage kapuaMessage) throws KapuaException, ClassNotFoundException {
		String protocol = connectorDescriptor.getDeviceProtocolName();
		//first step... from Kapua to device level
		Translator<KapuaMessage, DeviceMessage> translatorFromKapua = translatorFromKapuaMap.get(protocol);
		if (translatorFromKapua==null) {
			//lookup
			translatorFromKapua = Translator.getTranslatorFor(KapuaMessage.class, ReflectUtil.create(DeviceMessage.class, connectorDescriptor.getDeviceProtocolImplementationClass()));
			translatorFromKapuaMap.put(protocol, translatorFromKapua);
		}
		DeviceMessage middleMessage = translatorFromKapua.translate(kapuaMessage);

		//second step.... from device level to jms
		Translator<DeviceMessage, JmsMessage> translatorToJms = translatorToJmsMap.get(protocol);
		if (translatorToJms==null) {
			//lookup
			translatorToJms = Translator.getTranslatorFor(DeviceMessage.class, ReflectUtil.create(JmsMessage.class, connectorDescriptor.getDeviceProtocolImplementationClass()));
			translatorToJmsMap.put(protocol, translatorToJms);
		}
		return translatorToJms.translate(middleMessage);
	}
    
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//	public static CamelKapuaMessage convertToCamelKapuaMessage(ConnectorDescriptor connectorDescriptor, byte[] messageBody, String jmsTopic, Date queuedOn, KapuaId connectionId)
//            throws KapuaException
//        {
//        //
//        // de-serialize it to build an KapuaMessage
//        String[] topicTokens = kapuaTopic.getTopicParts();
//        KapuaMessage kapuaMsg = null;
//        String uuid = UUID.randomUUID().toString();
//        if (messageBody != null) {
//
//            //
//            // Testing for: $EDC/+/+/BA/#
//            // This is the re-publish of the device life-cycle messages
//            // to be consumed by the console. In this case the payload
//            // is the string version of the metrics contained in the msg.
//            if (topicTokens.length >= 5 && EDC.equals(topicTokens[0]) && BA.equals(topicTokens[3])) {
//                kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, uuid, messageBody);
//            }
//
//            // Testing for: $EDC/+/+/MQTT/LWT
//            // This is the last will testament message published
//            // by the broker when the device is forcefully disconnected.
//            // Also in this case the payload is not a google proto buf one
//            else if (topicTokens.length == 5 && EDC.equals(topicTokens[0]) && MQTT.equals(topicTokens[3]) && LWT.equals(topicTokens[4])) {
//                kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, uuid, messageBody);
//            }
//            else {
//
//                // In all other cases assume a google proto buf payload.
//                // So try to de-serialize it into an KapuaPayload.
//                try {
//                    kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, uuid, KapuaPayload.buildFromByteArray(messageBody, jmsTopic));
//                }
////                catch (KapuaInvalidMessageException e) {
////                    logger.warn("Invalid KapuaMessage - account: {}, topic: {}", new Object[] { kapuaTopic.getAccount(), kapuaTopic.getFullTopic() }, e);
////                    kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, messageBody);
////                }
//                catch (IOException e) {
//                    logger.warn("IOException converting message - account: {}, topic: {}" + new Object[] { kapuaTopic.getAccount(), kapuaTopic.getFullTopic() }, e);
//                    kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, messageBody);
//                }
//            }
//        }
//        else {
//            logger.warn("Empty message received from topic {}", kapuaTopic.getFullTopic());
//            kapuaMsg = new KapuaMessage(kapuaTopic, queuedOn, uuid, (byte[]) null);
//        }
//
//        // fix new KapuaMessage with uuid (Listeners load payload from kapuapayload but this method build a new
//        if (kapuaMsg.getKapuaPayload() == null && kapuaMsg.getPayload() != null) {
//            KapuaPayload kapuaPay = new KapuaPayload();
//            kapuaPay.setBody(kapuaMsg.getPayload());
//            kapuaMsg.setKapuaPayload(kapuaPay);
//        }
//    }
    
	// =========================================
    // wildcards conversion
    // =========================================
    /**
     * A-MQ translate wildcards from jms to mqtt
     * function        ActiveMQ        MQTT
     * separator        .               /
     * element          *               +
     * sub tree         >               #
     * 
     * @param jmsTopic
     * @return
     */
    public static String convertJmsWildCardToMqtt(String jmsTopic)
    {
        String processedTopic = null;
        if (jmsTopic != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jmsTopic.length(); i++) {
                sb.append(convertWildcardJmsToMqtt(jmsTopic.charAt(i)));
            }
            processedTopic = sb.toString();
        }
        return processedTopic;
    }

    private static char convertWildcardJmsToMqtt(char c)
    {
        switch (c) {
            case '.':
                return '/';
            case '/':
                return '.';
            default:
                return c;
        }
    }

    /**
     * A-MQ translate wildcards from jms to mqtt
     * function        ActiveMQ        MQTT
     * separator        .               /
     * element          *               +
     * sub tree         >               #
     * 
     * @param mqttTopic
     * @return
     */
    public static String convertMqttWildCardToJms(String mqttTopic)
    {
        String processedTopic = null;
        if (mqttTopic != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mqttTopic.length(); i++) {
                sb.append(convertWildcardMqttToJms(mqttTopic.charAt(i)));
            }
            processedTopic = sb.toString();
        }
        return processedTopic;
    }

    private static char convertWildcardMqttToJms(char c)
    {
        switch (c) {
            case '.':
                return '/';
            case '/':
                return '.';
            default:
                return c;
        }
    }
    
}