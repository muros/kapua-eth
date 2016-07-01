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
package org.eclipse.kapua.broker.core.listener;

import java.util.Date;

import javax.jms.JMSException;

import org.apache.camel.spi.UriEndpoint;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.broker.core.pool.JmsAssistantProducerPool;
import org.eclipse.kapua.broker.core.pool.JmsAssistantProducerWrapper;
import org.eclipse.kapua.broker.core.pool.JmsAssistantProducerPool.DESTINATIONS;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifeCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Device messages listener (device life cycle). Manage:
 * - BIRTH/DC/LWT/APPS/CONNECT/DISCONNECT device messages
 * - Certificates updates
 *
 */
@UriEndpoint(
		title="device message processor", 
		syntax="bean:deviceMessageListener",
		scheme="bean") 
public class DeviceMessageListener extends AbstractListener<CamelKapuaMessage> {

	private static final Logger s_logger = LoggerFactory.getLogger(DeviceMessageListener.class);
	
	private static KapuaLocator                 locator               = KapuaLocator.getInstance();

	private static final String BIRTH 	   = "MQTT/BIRTH";
	private static final String DC         = "MQTT/DC";
	private static final String LWT 	   = "MQTT/LWT";
	private static final String APPS       = "MQTT/APPS";
	private static final String CONNECT    = "MQTT/CONNECT";
	private static final String DISCONNECT = "MQTT/DISCONNECT";
	private static final String PROVISION  = "MQTT/PROV";

	//metrics
	private Counter metricDeviceMessage;
	private Counter metricDeviceDirectMessage;
	private Counter metricDeviceDirectBirthMessage;
	private Counter metricDeviceDirectDcMessage;
	private Counter metricDeviceDirectMissingMessage;
	private Counter metricDeviceDirectAppsMessage;
	private Counter metricDeviceDirectUnknownMessage;
	private Counter metricDeviceDirectErrorMessage;
	
	public DeviceMessageListener() {
		super("device");
		metricDeviceMessage          = registerCounter("messages", "count");
		//direct
		metricDeviceDirectMessage            = registerCounter("messages", "direct", "count");
		metricDeviceDirectBirthMessage       = registerCounter("messages", "direct", "birth", "count");
		metricDeviceDirectDcMessage          = registerCounter("messages", "direct", "dc", "count");
		metricDeviceDirectMissingMessage     = registerCounter("messages", "direct", "missing", "count");
		metricDeviceDirectAppsMessage        = registerCounter("messages", "direct", "apps", "count");
		metricDeviceDirectUnknownMessage     = registerCounter("messages", "direct", "unknown", "count");
		metricDeviceDirectErrorMessage       = registerCounter("messages", "direct", "error", "count");
	}
	
	@Override
	public void processMessage(CamelKapuaMessage message) 
	{
		metricDeviceMessage.inc();
		KapuaTopic kapuaTopic = message.getMessage().getKapuaTopic();
		if (kapuaTopic.isKapuaTopic()) {
			metricDeviceDirectMessage.inc();
			processLifeCycleMessage(message.getConnectionId(), message.getMessage(), kapuaTopic);
		}
	}

	private void processLifeCycleMessage(KapuaId connectionId, KapuaMessage message, KapuaTopic kapuaTopic)
    {
		KapuaPayload kapuaPayload = message.getKapuaPayload();
		String accountName = kapuaTopic.getAccount();
		String clientId    = kapuaTopic.getAsset();
		String semanticTopic = kapuaTopic.getSemanticTopic();

		// FIXME: Remove this filtering of the republishes if no longer necessary
		if(semanticTopic.startsWith("BA/")) {
			s_logger.debug("Ignoring republish");
			return;
		}
		try {
//			if(message.getKapuaTopic().getFullTopic().contains("PURGE")){
//				String topic = null;
//                KapuaPayload purgePayload = message.getKapuaPayload();
//                if (purgePayload != null) {
//                    topic = (String) message.getKapuaPayload().getMetric("topic");
//                }
//
//                s_logger.info("****** ENTERING PURGE ******");
//                s_logger.info("* On topic: {}", accountName + (topic != null ? topic : "/+/#"));
//
//                MessageStoreService messageStoreService = locator.getService(MessageStoreService.class);
//                //TODO check if it's not necessary with the new datastore
////                messageStoreService.resetCache(accountName, topic);
//                return;
//			}
//			else{
				//
				// send the appropriate signal to the device service
				DeviceLifeCycleService deviceLifeCycleService = locator.getService(DeviceLifeCycleService.class);
				if (BIRTH.equals(semanticTopic)) {
					deviceLifeCycleService.birth(connectionId, message);
					metricDeviceDirectBirthMessage.inc();
				}
				else if (DC.equals(semanticTopic)) {
					deviceLifeCycleService.death(connectionId, message);
					metricDeviceDirectDcMessage.inc();
				}
				else if (LWT.equals(semanticTopic)) {
					deviceLifeCycleService.missing(connectionId, message);
					metricDeviceDirectMissingMessage.inc();
				}
				else if (APPS.equals(semanticTopic)) {
					// The APPS message has the same payload as the BIRTH message.
					// The payload is published on a different topic onlys
					// to create a different event.
					deviceLifeCycleService.applications(connectionId, message);
					metricDeviceDirectAppsMessage.inc();
				}
				else {
					metricDeviceDirectUnknownMessage.inc();
					s_logger.info("Unknown semantic part for $EDC topic: {}", message.getKapuaTopic().getFullTopic());
					return;
				}
//			}
		}
		catch (KapuaException e) {
			metricDeviceDirectErrorMessage.inc();
//			alerts.error(message.getKapuaTopic().getAccount(), Code.INVALID_EDCTOPIC);
			s_logger.error("Error while processing device life-cycle event", e);
			return;			    
		}
		if (!CONNECT.equals(semanticTopic) && ! DISCONNECT.equals(semanticTopic)) {
			// FIXME: Remove - Republish Life cycle message to console
			if(semanticTopic.equals(LWT)) {
				kapuaPayload = new KapuaPayload();
			}

			Date now = new Date();
			kapuaPayload.addMetric("server_event_time", Long.toString(now.getTime()));
			StringBuilder destination = new StringBuilder();
			destination.append("$EDC/");
			destination.append(accountName);
			destination.append("/");
			destination.append(clientId);
			if(semanticTopic.equals(BIRTH)) {
				destination.append("/BA/BIRTH");
            }
            else if (semanticTopic.equals(APPS)) {
				// The APPS message has the same payload as the BIRTH message
				// and should have the same effect on the receiving application.
				// Hence we republish it under the same topic.
				destination.append("/BA/BIRTH");
            }
            else if (semanticTopic.equals(DC)) {
				//FIXME: change DISCONNECT into DC
				destination.append("/BA/DISCONNECT");
            }
            else if (semanticTopic.equals(LWT)) {
				destination.append("/BA/LWT");
            }
            else if (semanticTopic.equals(PROVISION)) {
				destination.append("/BA/PROV");
			}
			try {
                s_logger.debug("MESSAGE: {}", kapuaPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(destination.toString(), kapuaPayload.toDisplayString().getBytes());
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                s_logger.error("An error occurred while publishing device history event: {}", e.getMessage());
//                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, e.getErrorCode().toString());
                return;
            }
            catch (KapuaInvalidTopicException e) {
                s_logger.error("An error occurred while publishing device history event: {}", e.getMessage());
//                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, "");
            }
            catch (Throwable t) {
                s_logger.warn("Cannot send life cycle message {}! {}", new Object[]{destination.toString(), t.getMessage()}, t);
//                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, t, "");
                return;
            }
		}

    }
	
//	private void processForwardedLifeCycleMessage(KapuaMessage message, KapuaTopic kapuaTopic) {
//		String accountName = kapuaTopic.getAccount();
//		String clientId	= kapuaTopic.getAsset();
//		String semanticTopic = kapuaTopic.getSemanticTopic();
//
//		// If a device with the same client id has connected to another broker
//		// in the cluster, disconnect the connection on this broker.
//		if (CONNECT.equals(semanticTopic)) {
//			disconnectClient("mqtt", accountName, clientId);
//			disconnectClient("mqtts", accountName, clientId);
//		}
//	}
//
//	private void disconnectClient(String transportConnectorName, String accountName, String clientId) {
//		BrokerService brokerService = BrokerRegistry.getInstance().lookup(KapuaConfig.getInstance().getAmqBrokerName());
//
//		TransportConnector tc = brokerService.getTransportConnectorByName(transportConnectorName);
//		if(tc == null) {
//			return;
//		}
//		
//		String connectionIdString = MessageFormat.format(KapuaSecurityBrokerFilter.MULTI_ACCOUNT_CLIENT_ID, accountName, clientId);
//		for(TransportConnection conn : tc.getConnections()) {
//			
//			if(connectionIdString.equals(conn.getConnectionId())) {
//				metricDeviceForwardedDisconnctClient.inc();
//				s_logger.info("New connection detected for {} on another broker.  Stopping the current connection on transport connector: {}.", connectionIdString, tc.getName());
//				try {
//					// Include KapuaDuplicateClientIdException to notify the security broker filter
//					conn.stopAsync(new KapuaDuplicateClientIdException(connectionIdString));;
//				} 
//				catch (Exception e) {
//					metricDeviceForwardedDisconnctClientError.inc();
//					s_logger.error("Could not stop connection: " + conn.getConnectionId(), e);
//				}
//				
//				// assume only one connection since this broker should have already handled any duplicates
//				break;
//			}
//		}
//	}

//	private void createAlert(String account, String clientId, String category, String message) 
//	{
//		// Create alert
//		AlertCreator alertCreator = new AlertCreator(account, clientId, Severity.WARNING);
//		alertCreator.setCategory(category);
//		alertCreator.setCode(LoginSourceType.MQTT.name());
//		alertCreator.setMessage(message);
//		
//		// Store the alert
//		try {
//			AlertManagerTrusted.create(alertCreator);
//		} 
//		catch (KapuaException e) {
//			s_logger.error("Error during ALERT storage ", e);
//		}	
//	}
}
