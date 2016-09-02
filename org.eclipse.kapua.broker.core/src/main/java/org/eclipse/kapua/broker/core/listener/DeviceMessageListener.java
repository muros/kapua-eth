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
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.broker.core.pool.JmsAssistantProducerPool.DESTINATIONS;
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
public class DeviceMessageListener extends AbstractListener<CamelKapuaMessage<?>> {

	private static final Logger logger = LoggerFactory.getLogger(DeviceMessageListener.class);
	
	private static final String[] BIRTH_SEMANTIC_TOPIC 	    = new String[]{"MQTT", "BIRTH"};
	private static final String[] DC_SEMANTIC_TOPIC         = new String[]{"MQTT", "DC"};
	private static final String[] LWT_SEMANTIC_TOPIC 	    = new String[]{"MQTT", "LWT"};
	private static final String[] APPS_SEMANTIC_TOPIC       = new String[]{"MQTT", "APPS"};
	private static final String[] CONNECT_SEMANTIC_TOPIC    = new String[]{"MQTT", "CONNECT"};
	private static final String[] DISCONNECT_SEMANTIC_TOPIC = new String[]{"MQTT", "DISCONNECT"};
	private static final String[] PROVISION_SEMANTIC_TOPIC  = new String[]{"MQTT", "PROV"};

	private static DeviceLifeCycleService deviceLifeCycleService = KapuaLocator.getInstance().getService(DeviceLifeCycleService.class);
	
	//metrics
	private Counter metricDeviceMessage;
	private Counter metricDeviceBirthMessage;
	private Counter metricDeviceDisconnectMessage;
	private Counter metricDeviceMissingMessage;
	private Counter metricDeviceAppsMessage;
	private Counter metricDeviceUnknownMessage;
	private Counter metricDeviceErrorMessage;
	
	public DeviceMessageListener() {
		super("device");
		
		metricDeviceMessage          = registerCounter("messages", "count");
		//direct
		metricDeviceBirthMessage       = registerCounter("messages", "birth", "count");
		metricDeviceDisconnectMessage  = registerCounter("messages", "dc", "count");
		metricDeviceMissingMessage     = registerCounter("messages", "missing", "count");
		metricDeviceAppsMessage        = registerCounter("messages", "apps", "count");
		metricDeviceUnknownMessage     = registerCounter("messages", "unknown", "count");
		metricDeviceErrorMessage       = registerCounter("messages", "error", "count");
	}
	
	@Override
	public void processMessage(CamelKapuaMessage<?> message) 
	{
		//TO BE ROMEVED!
	}
	
	public void processBirthMessage(CamelKapuaMessage<KapuaBirthMessage> birthMessage) {
		try {
			deviceLifeCycleService.birth(birthMessage.getConnectionId(), birthMessage.getMessage());
			metricDeviceBirthMessage.inc();
			//republish BA
			Date now = new Date();
			KapuaPayload kapuaPayload = birthMessage.getMessage().getPayload();
			kapuaPayload.getProperties().put("server_event_time", Long.toString(now.getTime()));
			
//			birthMessage.getMessage().setSemanticChannel(BIRTH_SEMANTIC_TOPIC);
			try {
                logger.debug("MESSAGE: {}", kapuaPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(birthMessage);
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                logger.error("An error occurred while publishing device history event: {}", e.getMessage());
                return;
            }
            catch (Throwable t) {
                logger.warn("Cannot send birth life cycle message {}! {}", new Object[]{birthMessage.getMessage().getChannel().toString(), t.getMessage()}, t);
                return;
            }
		}
		catch (KapuaException e) {
			metricDeviceErrorMessage.inc();
			logger.error("Error while processing device birth life-cycle event", e);
			return;			    
		}
    }
	
	public void processDisconnectMessage(CamelKapuaMessage<KapuaDisconnectMessage> disconnectMessage) {
		try {
			deviceLifeCycleService.death(disconnectMessage.getConnectionId(), disconnectMessage.getMessage());
			metricDeviceDisconnectMessage.inc();
			//republish BA
			Date now = new Date();
			KapuaPayload kapuaPayload = disconnectMessage.getMessage().getPayload();
			kapuaPayload.getProperties().put("server_event_time", Long.toString(now.getTime()));
			
//			birthMessage.getMessage().setSemanticChannel(BIRTH_SEMANTIC_TOPIC);
			try {
                logger.debug("MESSAGE: {}", kapuaPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(disconnectMessage);
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                logger.error("An error occurred while publishing disconnect event: {}", e.getMessage());
                return;
            }
            catch (Throwable t) {
                logger.warn("Cannot send disconnect life cycle message {}! {}", new Object[]{disconnectMessage.getMessage().getChannel().toString(), t.getMessage()}, t);
                return;
            }
		}
		catch (KapuaException e) {
			metricDeviceErrorMessage.inc();
			logger.error("Error while processing device disconnect life-cycle event", e);
			return;			    
		}
    }
	
	public void processAppsMessage(CamelKapuaMessage<KapuaBirthMessage> appsMessage) {
		try {
			deviceLifeCycleService.applications(appsMessage.getConnectionId(), appsMessage.getMessage());
			metricDeviceAppsMessage.inc();
			//republish BA
			Date now = new Date();
			KapuaPayload kapuaPayload = appsMessage.getMessage().getPayload();
			kapuaPayload.getProperties().put("server_event_time", Long.toString(now.getTime()));
			
//			birthMessage.getMessage().setSemanticChannel(BIRTH_SEMANTIC_TOPIC);
			try {
                logger.debug("MESSAGE: {}", kapuaPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(appsMessage);
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                logger.error("An error occurred while publishing apps event: {}", e.getMessage());
                return;
            }
            catch (Throwable t) {
                logger.warn("Cannot send apps life cycle message {}! {}", new Object[]{appsMessage.getMessage().getChannel().toString(), t.getMessage()}, t);
                return;
            }
		}
		catch (KapuaException e) {
			metricDeviceErrorMessage.inc();
			logger.error("Error while processing device apps life-cycle event", e);
			return;			    
		}
    }
	
	public void processMissingMessage(CamelKapuaMessage<KapuaMessage> missingMessage) {
		try {
			deviceLifeCycleService.missing(missingMessage.getConnectionId(), missingMessage.getMessage());
			metricDeviceMissingMessage.inc();
			//republish BA
			Date now = new Date();
			KapuaPayload kapuaPayload = missingMessage.getMessage().getPayload();
			kapuaPayload.getProperties().put("server_event_time", Long.toString(now.getTime()));
			
//			birthMessage.getMessage().setSemanticChannel(BIRTH_SEMANTIC_TOPIC);
			try {
                logger.debug("MESSAGE: {}", kapuaPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(missingMessage);
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                logger.error("An error occurred while publishing missing event: {}", e.getMessage());
                return;
            }
            catch (Throwable t) {
                logger.warn("Cannot send missing life cycle message {}! {}", new Object[]{missingMessage.getMessage().getChannel().toString(), t.getMessage()}, t);
                return;
            }
		}
		catch (KapuaException e) {
			metricDeviceErrorMessage.inc();
			logger.error("Error while processing device missing life-cycle event", e);
			return;			    
		}
    }
	
//	private void processLifeCycleMessage(KapuaId connectionId, KapuaMessage<KapuaChannel, KapuaPayload> message, KapuaChannel kapuaChannel)
//    {
//		KapuaPayload kapuaPayload = message.getPayload();
//		String accountName = kapuaChannel.getAccount();
//		String clientId    = kapuaChannel.getClientId();
//		String[] semanticTopic = kapuaChannel.getSemanticParts();
//
//		// FIXME: Remove this filtering of the republishes if no longer necessary
//		if(semanticTopic==null || semanticTopic.length<=0 || semanticTopic[0].equals("BA")) {
//			logger.debug("Ignoring republish");
//			return;
//		}
//		try {
////			if(message.getKapuaTopic().getFullTopic().contains("PURGE")){
////				String topic = null;
////                KapuaPayload purgePayload = message.getKapuaPayload();
////                if (purgePayload != null) {
////                    topic = (String) message.getKapuaPayload().getMetric("topic");
////                }
////
////                logger.info("****** ENTERING PURGE ******");
////                logger.info("* On topic: {}", accountName + (topic != null ? topic : "/+/#"));
////
////                MessageStoreService messageStoreService = KapuaLocator.getInstance().getService(MessageStoreService.class);
////                //TODO check if it's not necessary with the new datastore
//////                messageStoreService.resetCache(accountName, topic);
////                return;
////			}
////			else{
//				//
//				// send the appropriate signal to the device service
//				
//				if (BIRTH.equals(semanticTopic[0])) {
//				}
//				else if (DC.equals(semanticTopic[0])) {
//					deviceLifeCycleService.death(connectionId, message);
//					metricDeviceDcMessage.inc();
//				}
//				else if (LWT.equals(semanticTopic[0])) {
//					deviceLifeCycleService.missing(connectionId, message);
//					metricDeviceMissingMessage.inc();
//				}
//				else if (APPS.equals(semanticTopic[0])) {
//					// The APPS message has the same payload as the BIRTH message.
//					// The payload is published on a different topic onlys
//					// to create a different event.
//					deviceLifeCycleService.applications(connectionId, message);
//					metricDeviceAppsMessage.inc();
//				}
//				else {
//					metricDeviceUnknownMessage.inc();
//					//TODO add comment to override the toString when implementing the channel interface to show it in a more significant way 
//					logger.info("Unknown semantic part for system topic: {}", message.getSemanticChannel().);
//					return;
//				}
////			}
//		}
//		catch (KapuaException e) {
//			metricDeviceErrorMessage.inc();
////			alerts.error(message.getKapuaTopic().getAccount(), Code.INVALID_KAPUATOPIC);
//			logger.error("Error while processing device life-cycle event", e);
//			return;			    
//		}
//		if (!CONNECT.equals(semanticTopic[0]) && ! DISCONNECT.equals(semanticTopic[0])) {
//			// FIXME: Remove - Republish Life cycle message to console
//			if(semanticTopic[0].equals(LWT)) {
//				kapuaPayload = new KapuaPayload();
//			}
//
//			Date now = new Date();
//			kapuaPayload.getMetrics().put("server_event_time", Long.toString(now.getTime()));
//			StringBuilder destination = new StringBuilder();
//			destination.append("$EDC/");
//			destination.append(accountName);
//			destination.append("/");
//			destination.append(clientId);
//			if(semanticTopic[0].equals(BIRTH)) {
//				destination.append("/BA/BIRTH");
//            }
//            else if (semanticTopic[0].equals(APPS)) {
//				// The APPS message has the same payload as the BIRTH message
//				// and should have the same effect on the receiving application.
//				// Hence we republish it under the same topic.
//				destination.append("/BA/BIRTH");
//            }
//            else if (semanticTopic[0].equals(DC)) {
//				//FIXME: change DISCONNECT into DC
//				destination.append("/BA/DISCONNECT");
//            }
//            else if (semanticTopic[0].equals(LWT)) {
//				destination.append("/BA/LWT");
//            }
//            else if (semanticTopic[0].equals(PROVISION)) {
//				destination.append("/BA/PROV");
//			}
//			try {
//                logger.debug("MESSAGE: {}", kapuaPayload);
//                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
//                JmsAssistantProducerWrapper producer = null;
//                try {
//                    producer = pool.borrowObject();
//                    producer.sendRawMessage(destination.toString(), kapuaPayload.toDisplayString().getBytes());
//                }
//                finally {
//                    pool.returnObject(producer);
//                }
//            } 
//			catch (JMSException e) {
//                logger.error("An error occurred while publishing device history event: {}", e.getMessage());
////                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, e.getErrorCode().toString());
//                return;
//            }
////            catch (KapuaInvalidTopicException e) {
////                logger.error("An error occurred while publishing device history event: {}", e.getMessage());
//////                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, "");
////            }
//            catch (Throwable t) {
//                logger.warn("Cannot send life cycle message {}! {}", new Object[]{destination.toString(), t.getMessage()}, t);
////                alerts.severe(message.getKapuaTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, t, "");
//                return;
//            }
//		}

	
}