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
package org.eclipse.kapua.broker.core.pool;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.broker.core.message.JmsUtil;
import org.eclipse.kapua.broker.core.plugin.AclConstants;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;
import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.org.eclipse.kapua.transport.message.jms.JmsMessage;

/**
 * Broker assistant ({@link JmsProducerWrapper}) implementation
 * This class provide methods to send messages for the device life cycle (to be send outside to a device specific topic) and alert/login info update messages (to be send to a specific queue) 
 */
public class JmsAssistantProducerWrapper extends JmsProducerWrapper {
	
//	private static final String CONNECT_TOPIC      = "$EDC.{0}.{1}.MQTT.CONNECT";
//	private static final String DISCONNECT_TOPIC   = "$EDC.{0}.{1}.MQTT.DISCONNECT";
//	//TODO check the topic and the security broker filter behavior
//	//(because if the device has a not allowed lwt topic there will be no missing and disconnect in the device events,
//	//but if we send for default the missing message we will have missing event twice in the most of the cases?)
//	private static final String MISSING_TOPIC      = "$EDC.{0}.{1}.MQTT.MISSING";
	
	private final static List<String> semanticTopicConnect = new ArrayList<String>();
	private final static List<String> semanticTopicDisconnect = new ArrayList<String>();
	private final static List<String> semanticTopicMissing = new ArrayList<String>();
	
	static {
		semanticTopicConnect.add("MQTT");
		semanticTopicConnect.add("CONNECT");
		
		semanticTopicDisconnect.add("MQTT");
		semanticTopicDisconnect.add("DISCONNECT");
		
		semanticTopicMissing.add("MQTT");
		semanticTopicMissing.add("MISSING");
	}

	//leave the MQTT format. this topic it's not used for sending message but only to create correctly the KapuaTopic object
//	private static final String ALERT_TOPIC_MQTT   = "{0}/{1}/ALERT";
//	private static final String UPDATE_LOGIN_INFO_TOPIC_MQTT = "$EDC/{0}/{1}/SERVICE/UPDATE_LOGIN_INFO";
	
	public JmsAssistantProducerWrapper(ActiveMQConnectionFactory vmconnFactory, String destination, boolean transacted, boolean start) throws JMSException {
		super(vmconnFactory, destination, transacted, start);
	}
	
	//==========================================================
	//Messages to be send outside (to a specific topic)
	//==========================================================
	
	public void sendConnectMessage(ConnectorDescriptor connectorDescriptor, String accountName, long accountId, String userName, Long userId, String clientId, String nodeId, String remoteAddress) throws JMSException, KapuaException {
		send(connectorDescriptor, buildNetworkKapuaMessage(semanticTopicConnect, accountName, userName, userId, clientId, nodeId, remoteAddress));
	}

	public void sendDisconnectMessage(ConnectorDescriptor connectorDescriptor, String accountName, String userName, String clientId, String remoteAddress) 
	        throws JMSException, KapuaException {
		send(connectorDescriptor, buildNetworkKapuaMessage(semanticTopicDisconnect, accountName, userName, null, clientId, null, remoteAddress));
	}

	public void sendMissingMessage(ConnectorDescriptor connectorDescriptor, String accountName, String userName, String clientId, String remoteAddress) 
	        throws JMSException, KapuaException {
		send(connectorDescriptor, buildNetworkKapuaMessage(semanticTopicMissing, accountName, userName, null, clientId, null, remoteAddress));
	}
	
	@SuppressWarnings("rawtypes")
	private void send(ConnectorDescriptor connectorDescriptor, KapuaMessage kapuaMessage) throws JMSException, KapuaException {
		BytesMessage message = session.createBytesMessage();
		JmsMessage jmsMessage;
		try {
			jmsMessage = JmsUtil.convertToJmsMessage(connectorDescriptor, kapuaMessage);
		}
		catch (ClassNotFoundException e) {
			//TODOO define new error code or new exception??
			throw new KapuaException(KapuaErrorCodes.INTERNAL_ERROR);
		}
		
		String topic = jmsMessage.getTopic().getTopic();
		message.setStringProperty(MessageConstants.PROPERTY_TOPIC_ORIG, topic);
		message.setLongProperty(MessageConstants.PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());
		Destination destination = session.createTopic(MessageFormat.format(AclConstants.VT_TOPIC_PREFIX_TEMPLATE, topic));
		
		producer.send(destination, message);
	}

	//==========================================================
	//Messages to be send into the internal KAPUA_SERVICE queue
	//==========================================================
	/**
	 * Send the data messages gone in error to a specific queue to keep them
	 * @param messageNotStored
	 * @param topic
	 * @throws JMSException
	 * @throws KapuaInvalidTopicException
	 */
	public void sendDataMessageNotStored(String topic, byte[] messageNotStored) throws JMSException {
		BytesMessage message = session.createBytesMessage();
		
		message.setStringProperty(MessageConstants.PROPERTY_TOPIC_ORIG, JmsUtil.convertMqttWildCardToJms(topic));
		message.setLongProperty(MessageConstants.PROPERTY_ENQUEUED_TIMESTAMP, System.currentTimeMillis());
		message.writeBytes(messageNotStored);
		
		producer.send(message);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private KapuaMessage<KapuaChannel, KapuaPayload> buildNetworkKapuaMessage(List<String> semanticTopic, String accountName, String userName, Long userId, String clientId, String nodeId, String remoteAddress) {
		KapuaChannel kapuaChannel = new KapuaChannelImpl();
		kapuaChannel.setSemanticParts(semanticTopic);
		KapuaPayload kapuapay = new KapuaPayloadImpl();
		kapuapay.getProperties().put(MessageConstants.METRIC_USERNAME, userName);
		if (userId!=null) {
			kapuapay.getProperties().put(MessageConstants.METRIC_USER_ID, userId);
		}
		kapuapay.getProperties().put(MessageConstants.METRIC_ACCOUNT, accountName);
		kapuapay.getProperties().put(MessageConstants.METRIC_CLIENT_ID, clientId);
		if (nodeId != null) {
			kapuapay.getProperties().put(MessageConstants.METRIC_NODE_ID, nodeId);
		}
		if (remoteAddress != null) {
			kapuapay.getProperties().put(MessageConstants.METRIC_IP, remoteAddress);
		}

		return new KapuaMessageImpl(kapuaChannel, kapuapay);
	}
	
}
