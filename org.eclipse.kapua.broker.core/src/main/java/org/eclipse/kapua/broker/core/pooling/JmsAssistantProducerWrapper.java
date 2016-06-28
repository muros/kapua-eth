/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
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
package org.eclipse.kapua.broker.core.pooling;

import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.kapua.broker.core.plugin.KapuaSecurityBrokerFilter;
import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.service.user.UserStatus;

/**
 * Broker assistant ({@link JmsProducerWrapper}) implementation
 * This class provide methods to send messages for the device life cycle (to be send outside to a device specific topic) and alert/login info update messages (to be send to a specific queue) 
 */
public class JmsAssistantProducerWrapper extends JmsProducerWrapper {
	
	// used by login update info asynch
	public static final String  METRIC_USERNAME                = "username";
    public static final String  METRIC_ACCOUNT                 = "account";
    public static final String  METRIC_CLIENT_ID               = "clientId";
    public static final String  METRIC_IP                      = "ip";
    public final static String  PROPERTY_TOPIC_ORIG            = "topicOrg";
    public final static String  PROPERTY_ENQUEUED_TIMESTAMP    = "enqueuedTimestamp";
    
    // used by login update info asynch
    public static final String  METRIC_USER_ID                 = "userId";
    public static final String  METRIC_NODE_ID                 = "nodeId";
    public static final String  METRIC_STATUS                  = "status";
    public static final String  METRIC_LOGIN_ON                = "loginOn";
    public static final String  METRIC_LOGIN_ATTEMPTS          = "loginAttempts";
    public static final String  METRIC_LOGIN_ATTEMPTS_RESET_ON = "loginAttemptsResetOn";
    public static final String  METRIC_LOCKED_ON               = "lockedOn";
    public static final String  METRIC_UNLOCK_ON               = "unlockOn";
    
	private static final String CONNECT_TOPIC      = "$EDC.{0}.{1}.MQTT.CONNECT";
	private static final String DISCONNECT_TOPIC   = "$EDC.{0}.{1}.MQTT.DISCONNECT";
	//TODO check the topic and the security broker filter behavior
	//(because if the device has a not allowed lwt topic there will be no missing and disconnect in the device events,
	//but if we send for default the missing message we will have missing event twice in the most of the cases?)
	private static final String MISSING_TOPIC      = "$EDC.{0}.{1}.MQTT.MISSING";

	//leave the MQTT format. this topic it's not used for sending message but only to create correctly the KapuaTopic object
	private static final String ALERT_TOPIC_MQTT   = "{0}/{1}/ALERT";
	private static final String UPDATE_LOGIN_INFO_TOPIC_MQTT = "$EDC/{0}/{1}/SERVICE/UPDATE_LOGIN_INFO";
	
	public JmsAssistantProducerWrapper(ActiveMQConnectionFactory vmconnFactory, String destination, boolean transacted, boolean start) throws JMSException {
		super(vmconnFactory, destination, transacted, start);
	}
	
	//==========================================================
	//Messages to be send outside (to a specific topic)
	//==========================================================
	
	public void sendConnectMessage(String accountName, long accountId, String userName, Long userId, String clientId, String nodeId, String remoteAddress) throws JMSException, KapuaInvalidTopicException {
		BytesMessage message = session.createBytesMessage();
		
		String topic = buildNetworkConnectMessage(message, accountName, userName, userId, clientId, nodeId, remoteAddress);
		Destination destination = session.createTopic(MessageFormat.format(KapuaSecurityBrokerFilter.VT_TOPIC_PREFIX_TEMPLATE, topic));

		producer.send(destination, message);
	}

	public void sendDisconnectMessage(String accountName, String userName, String clientId, String remoteAddress) 
	        throws JMSException, KapuaInvalidTopicException {
		BytesMessage message = session.createBytesMessage();
		
		String topic = buildNetworkDisconnectMessage(message, accountName, userName, null, clientId, remoteAddress);
		Destination destination = session.createTopic(MessageFormat.format(KapuaSecurityBrokerFilter.VT_TOPIC_PREFIX_TEMPLATE, topic));
		
		producer.send(destination, message);
	}

	public void sendMissingMessage(String accountName, String userName, String clientId, String remoteAddress) 
	        throws JMSException, KapuaInvalidTopicException {
		BytesMessage message = session.createBytesMessage();
		
		String topic = buildNetworkMissingMessage(message, accountName, userName, null, clientId, remoteAddress);
		Destination destination = session.createTopic(MessageFormat.format(KapuaSecurityBrokerFilter.VT_TOPIC_PREFIX_TEMPLATE, topic));

		producer.send(destination, message);
	}
	
	/**
	 * Build a network connection message
	 * @param vmSession
	 * @param destination
	 * @param message
	 * @return
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	private String buildNetworkConnectMessage(BytesMessage byteMessage, String accountName, String userName, Long userId, String clientId, String nodeId, String remoteAddress) throws JMSException, KapuaInvalidTopicException {
        String topic = MessageFormat.format(CONNECT_TOPIC, accountName, clientId);
        
        KapuaMessage kapuaMessage = buildNetworkKapuaMessage(topic, accountName, userName, userId, clientId, nodeId, remoteAddress);
		byteMessage.setStringProperty(PROPERTY_TOPIC_ORIG, topic);
		byteMessage.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());

		byteMessage.writeBytes(kapuaMessage.getPayload());
		return topic;
	}

	/**
	 * 
	 * @param vmSession
	 * @param destination
	 * @param message
	 * @return
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	private String buildNetworkDisconnectMessage(BytesMessage byteMessage, String accountName, String userName, Long userId, String clientId, String remoteAddress) throws JMSException, KapuaInvalidTopicException {
		KapuaMessage kapuaMessage = buildNetworkDisconnectMessage(accountName, userName, userId, clientId, remoteAddress);
		String topic = kapuaMessage.getTopic();
		
		byteMessage.setStringProperty(PROPERTY_TOPIC_ORIG, kapuaMessage.getTopic());
		byteMessage.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());

		byteMessage.writeBytes(kapuaMessage.getPayload());
		return topic;
	}

	/**
	 * 
	 * @param accountName
	 * @param userName
	 * @param clientId
	 * @param ip
	 * @return
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	public KapuaMessage buildNetworkDisconnectMessage(String accountName, String userName, Long userId, String clientId, String remoteAddress) throws JMSException, KapuaInvalidTopicException {
		String topic = MessageFormat.format(DISCONNECT_TOPIC, accountName, clientId);
		return buildNetworkKapuaMessage(topic, accountName, userName, userId, clientId, null, remoteAddress);
	}

	/**
	 * 
	 * @param vmSession
	 * @param destination
	 * @param message
	 * @return
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	private String buildNetworkMissingMessage(BytesMessage byteMessage, String accountName, String userName, Long userId, String clientId, String remoteAddress) throws JMSException, KapuaInvalidTopicException {
		KapuaMessage kapuaMessage = buildNetworkMissingMessage(accountName, userName, userId, clientId, remoteAddress);
		String topic = kapuaMessage.getTopic();
		
		byteMessage.setStringProperty(PROPERTY_TOPIC_ORIG, kapuaMessage.getTopic());
		byteMessage.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());

		byteMessage.writeBytes(kapuaMessage.getPayload());
		return topic;
	}

	/**
	 * 
	 * @param accountName
	 * @param userName
	 * @param clientId
	 * @param ip
	 * @return
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	public KapuaMessage buildNetworkMissingMessage(String accountName, String userName, Long userId, String clientId, String remoteAddress) throws KapuaInvalidTopicException {
		String topic = MessageFormat.format(MISSING_TOPIC, accountName, clientId);
		return buildNetworkKapuaMessage(topic, accountName, userName, userId, clientId, null, remoteAddress);		
	}
	
	//==========================================================
	//Messages to be send into the internal EDC_SERVICE queue
	//==========================================================
	
//	/**
//	 * Send a message to insert an alert asynchronous (this message will be send to the internal EDC_SERVICE queue)
//	 * The topic used for the instantiation of the KapuaTopic it's different (MQTT wildcards) from the topic used to set the property PROPERTY_TOPIC_ORIG
//	 * @param accountName
//	 * @param accountId
//	 * @param userName
//	 * @param userId
//	 * @param clientId
//	 * @param category
//	 * @param message
//	 * @throws KapuaInvalidTopicException
//	 * @throws JMSException
//	 */
//	public void sendAlertMessage(String accountName, String userName, String clientId, String category, Severity severity, String message) throws KapuaInvalidTopicException, JMSException {
//		BytesMessage byteMessage = session.createBytesMessage();
//		
//		String topic = MessageFormat.format(ALERT_TOPIC_MQTT, accountName, clientId);
//        
//        KapuaMessage kapuaMessage = buildNetworkKapuaMessage(topic, accountName, userName, null, clientId, null, null);
//        KapuaPayload kapuaPayload = kapuaMessage.getKapuaPayload();
//		kapuaPayload.addMetric(METRIC_ALERT_CATEGORY, category);
//		kapuaPayload.addMetric(METRIC_ALERT_MESSAGE, message);
//        if (severity!=null) {
//        	kapuaPayload.addMetric(METRIC_ALERT_SEVERITY, severity.name());
//        }
//        else {
//        	kapuaPayload.addMetric(METRIC_ALERT_SEVERITY, Severity.INFO);
//        }
//		
//		byteMessage.setStringProperty(PROPERTY_TOPIC_ORIG, convertMqttWildCardToJms(topic));
//		byteMessage.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());
//		
//		byteMessage.writeBytes(kapuaMessage.getPayload());
//		
//		producer.send(byteMessage);
//	}
	
	/**
	 * Send the update login info message
	 * @param accountName
	 * @param clientId
	 * @param accountId
	 * @param userId
	 * @param status
	 * @param loginOn
	 * @param loginAttempts
	 * @param loginAttemptsResetOn
	 * @param lockedOn
	 * @param unlockOn
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 * @throws KapuaInvalidTopicException
	 */
	public void sendUpdateLoginInfo(String accountName, String clientId, long accountId, long userId, UserStatus status,
			Date loginOn, int loginAttempts, Date loginAttemptsResetOn, Date lockedOn, Date unlockOn) throws JMSException, KapuaInvalidTopicException {
		
		BytesMessage message = session.createBytesMessage();
		
		buildUpdateLoginInfoMessage(message, accountName, clientId, userId, status,
			loginOn, loginAttempts, loginAttemptsResetOn, lockedOn, unlockOn);
		
		producer.send(message);
	}
	
	/**
	 * Prepare the update login info message (this message will be send to the internal EDC_SERVICE queue)
	 * The topic used for the instantiation of the KapuaTopic it's different (MQTT wildcards) from the topic used to set the property PROPERTY_TOPIC_ORIG
	 * @param byteMessage
	 * @param accountName
	 * @param clientId
	 * @param accountId
	 * @param userId
	 * @param status
	 * @param loginOn
	 * @param loginAttempts
	 * @param loginAttemptsResetOn
	 * @param lockedOn
	 * @param unlockOn
	 * @throws KapuaInvalidTopicException
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException 
	 */
	public void buildUpdateLoginInfoMessage(BytesMessage byteMessage, String accountName, String clientId, long userId, UserStatus status,
			Date loginOn, int loginAttempts, Date loginAttemptsResetOn, Date lockedOn, Date unlockOn) throws JMSException, KapuaInvalidTopicException {
		String topic = MessageFormat.format(UPDATE_LOGIN_INFO_TOPIC_MQTT, accountName, clientId);
        
		String uuid = UUID.randomUUID().toString();
		KapuaTopic kapuaTopic = new KapuaTopic(topic);
		KapuaPayload kapuapay = new KapuaPayload();
		kapuapay.addMetric(METRIC_USER_ID, userId);
		if (status!=null) {
			kapuapay.addMetric(METRIC_STATUS, status.name());
		}
		if (loginOn!=null) {
			kapuapay.addMetric(METRIC_LOGIN_ON, loginOn.getTime());
		}
		kapuapay.addMetric(METRIC_LOGIN_ATTEMPTS, loginAttempts);
		if (loginAttemptsResetOn!=null) {
			kapuapay.addMetric(METRIC_LOGIN_ATTEMPTS_RESET_ON, loginAttemptsResetOn.getTime());
		}
		if (lockedOn!=null) {
			kapuapay.addMetric(METRIC_LOCKED_ON, lockedOn.getTime());
		}
		if (unlockOn!=null) {
			kapuapay.addMetric(METRIC_UNLOCK_ON, unlockOn.getTime());
		}

		KapuaMessage kapuaMessage = new KapuaMessage(kapuaTopic, new Date(), uuid, kapuapay);
		
		byteMessage.setStringProperty(PROPERTY_TOPIC_ORIG, convertMqttWildCardToJms(topic));
		byteMessage.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());

		byteMessage.writeBytes(kapuaMessage.getPayload());
	}
	
	/**
	 * Send the data messages gone in error to a specific queue to keep them
	 * @param messageNotStored
	 * @param topic
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException
	 */
	public void sendDataMessageNotStored(String topic, byte[] messageNotStored) throws JMSException {
		BytesMessage message = session.createBytesMessage();
		
		message.setStringProperty(PROPERTY_TOPIC_ORIG, convertMqttWildCardToJms(topic));
		message.setLongProperty(PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());
		message.writeBytes(messageNotStored);
		
		producer.send(message);
	}
	
	private KapuaMessage buildNetworkKapuaMessage(String topic, String accountName, String userName, Long userId, String clientId, String nodeId, String remoteAddress) throws KapuaInvalidTopicException {
		String uuid = UUID.randomUUID().toString();
		KapuaTopic kapuaTopic = new KapuaTopic(topic);
		KapuaPayload kapuapay = new KapuaPayload();
		kapuapay.addMetric(METRIC_USERNAME, userName);
		if (userId!=null) {
			kapuapay.addMetric(METRIC_USER_ID, userId);
		}
		kapuapay.addMetric(METRIC_ACCOUNT, accountName);
		kapuapay.addMetric(METRIC_CLIENT_ID, clientId);
		if (nodeId != null) {
			kapuapay.addMetric(METRIC_NODE_ID, nodeId);
		}
		if (remoteAddress != null) {
			kapuapay.addMetric(METRIC_IP, remoteAddress);
		}

		return new KapuaMessage(kapuaTopic, new Date(), uuid, kapuapay);
	}
	
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
