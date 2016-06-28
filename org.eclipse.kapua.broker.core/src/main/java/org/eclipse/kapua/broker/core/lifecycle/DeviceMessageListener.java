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
package org.eclipse.kapua.broker.core.lifecycle;

import java.text.MessageFormat;
import java.util.Date;

import javax.jms.JMSException;

import org.apache.activemq.broker.BrokerRegistry;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.camel.spi.UriEndpoint;
import org.eclipse.kapua.KapuaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.eurotech.cloud.commons.EdcDuplicateClientIdException;
import com.eurotech.cloud.commons.EdcException;
import com.eurotech.cloud.commonsbundle.camel.AbstractListener;
import com.eurotech.cloud.commons.jms.Alerts.Code;
import com.eurotech.cloud.commons.jms.AlertMessage;
import com.eurotech.cloud.commons.jms.Alerts;
import com.eurotech.cloud.commons.jms.AmqEdcMessage;
import com.eurotech.cloud.commons.jms.JmsAssistantProducerPool;
import com.eurotech.cloud.commons.jms.JmsAssistantProducerPool.DESTINATIONS;
import com.eurotech.cloud.commons.jms.JmsConstants;
import com.eurotech.cloud.commons.jms.JmsUtil;
import com.eurotech.cloud.commons.jms.wrapper.JmsAssistantProducerWrapper;
import com.eurotech.cloud.commons.model.AlertCategories;
import com.eurotech.cloud.commons.model.AlertCreator;
import com.eurotech.cloud.commons.model.DeviceCredentialsTight;
import com.eurotech.cloud.commons.model.LoginSourceType;
import com.eurotech.cloud.commons.model.Alert.Severity;
import com.eurotech.cloud.commons.model.cluster.BrokerNode;
import com.eurotech.cloud.commons.service.DataStoreService;
import com.eurotech.cloud.commons.service.DeviceService;
import com.eurotech.cloud.commons.service.ProvisionRequestService;
import com.eurotech.cloud.commons.service.ServiceLocator;
import com.eurotech.cloud.commons.service.bean.AlertManagerTrusted;
import com.eurotech.cloud.commons.service.bean.DeviceRegistryManagerTrusted;
import com.eurotech.cloud.commons.util.AwsUtils;
import com.eurotech.cloud.commons.util.EdcConfig;
import com.eurotech.cloud.message.EdcInvalidTopicException;
import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcTopic;

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
public class DeviceMessageListener extends AbstractListener<AmqEdcMessage> {

	private static final Logger s_logger = LoggerFactory.getLogger(DeviceMessageListener.class);

	private static final String BIRTH 	   = "MQTT/BIRTH";
	private static final String DC         = "MQTT/DC";
	private static final String LWT 	   = "MQTT/LWT";
	private static final String APPS       = "MQTT/APPS";
	private static final String CONNECT    = "MQTT/CONNECT";
	private static final String DISCONNECT = "MQTT/DISCONNECT";
	private static final String PROVISION  = "MQTT/PROV";

	private static final BrokerNode brokerNode = AwsUtils.getBrokerNode();
	
	//metrics
	private Counter metricDeviceMessage;
	private Counter metricDeviceDirectMessage;
	private Counter metricDeviceDirectConnectMessage;
	private Counter metricDeviceDirectBirthMessage;
	private Counter metricDeviceDirectDisconnectMessage;
	private Counter metricDeviceDirectDcMessage;
	private Counter metricDeviceDirectMissingMessage;
	private Counter metricDeviceDirectProvisionMessage;
	private Counter metricDeviceDirectAppsMessage;
	private Counter metricDeviceDirectUnknownMessage;
	private Counter metricDeviceDirectErrorMessage;
	//forwarded
	private Counter metricDeviceForwardedMessage;
	private Counter metricDeviceForwardedDisconnctClient;
	private Counter metricDeviceForwardedDisconnctClientError;
	
	public DeviceMessageListener() {
		super("device");
		metricDeviceMessage          = registerCounter("messages", "count");
		//direct
		metricDeviceDirectMessage            = registerCounter("messages", "direct", "count");
		metricDeviceDirectConnectMessage     = registerCounter("messages", "direct", "connect", "count");
		metricDeviceDirectBirthMessage       = registerCounter("messages", "direct", "birth", "count");
		metricDeviceDirectDisconnectMessage  = registerCounter("messages", "direct", "disconnect", "count");
		metricDeviceDirectDcMessage          = registerCounter("messages", "direct", "dc", "count");
		metricDeviceDirectMissingMessage     = registerCounter("messages", "direct", "missing", "count");
		metricDeviceDirectProvisionMessage   = registerCounter("messages", "direct", "provision", "count");
		metricDeviceDirectAppsMessage        = registerCounter("messages", "direct", "apps", "count");
		metricDeviceDirectUnknownMessage     = registerCounter("messages", "direct", "unknown", "count");
		metricDeviceDirectErrorMessage       = registerCounter("messages", "direct", "error", "count");
		//forwarded
		metricDeviceForwardedMessage              = registerCounter("messages", "forwarded", "count");
		metricDeviceForwardedDisconnctClient      = registerCounter("messages", "forwarded", "disconnect_client", "count");
		metricDeviceForwardedDisconnctClientError = registerCounter("messages", "forwarded", "disconnect_client_error", "count");
	}
	
	@Override
	public void processMessage(AmqEdcMessage message) 
	{
		metricDeviceMessage.inc();
		EdcTopic edcTopic = message.getEdcTopic();
		if (edcTopic.isEdcTopic()) {
			if (!message.isForwarded()) {
				metricDeviceDirectMessage.inc();
				processLifeCycleMessage(message, edcTopic);
			} 
			else {
				metricDeviceForwardedMessage.inc();
				processForwardedLifeCycleMessage(message, edcTopic);
			}
		}
	}

	private void processLifeCycleMessage(EdcMessage message, EdcTopic edcTopic)
    {
		EdcPayload edcPayload = message.getEdcPayload();
		String accountName = edcTopic.getAccount();
		String clientId    = edcTopic.getAsset();
		String semanticTopic = edcTopic.getSemanticTopic();

		// FIXME: Remove this filtering of the republishes if no longer necessary
		if(semanticTopic.startsWith("BA/")) {
			s_logger.debug("Ignoring republish");
			return;
		}
		try {
			if(message.getEdcTopic().getFullTopic().contains("PURGE")){
				String topic = null;
                EdcPayload purgePayload = message.getEdcPayload();
                if (purgePayload != null) {
                    topic = (String) message.getEdcPayload().getMetric("topic");
                }

                s_logger.info("****** ENTERING PURGE ******");
                s_logger.info("* On topic: {}", accountName + (topic != null ? topic : "/+/#"));

				ServiceLocator locator = ServiceLocator.getInstance();
				DataStoreService ds = locator.getDataStoreService();
                ds.resetCache(accountName, topic);
                return;
			}
			else{
				//
				// send the appropriate signal to the device service
				ServiceLocator    locator = ServiceLocator.getInstance();
				DeviceService  dvcService = locator.getDeviceService();
				if (BIRTH.equals(semanticTopic)) {
					dvcService.birth(accountName, clientId, message);
					metricDeviceDirectBirthMessage.inc();
				}
				else if (DC.equals(semanticTopic)) {
					dvcService.dc(accountName, clientId, message);
					metricDeviceDirectDcMessage.inc();
				}
				else if (LWT.equals(semanticTopic)) {
					dvcService.missing(accountName, clientId, message);
					metricDeviceDirectMissingMessage.inc();
				}
				else if (APPS.equals(semanticTopic)) {
					// The APPS message has the same payload as the BIRTH message.
					// The payload is published on a different topic onlys
					// to create a different event.
					dvcService.applications(accountName, clientId, message);
					metricDeviceDirectAppsMessage.inc();
				}
				else if (CONNECT.equals(semanticTopic)) {
					Long userId = (Long)edcPayload.getMetric(JmsUtil.METRIC_USER_ID);
					String username = (String)edcPayload.getMetric(JmsUtil.METRIC_USERNAME);
                    dvcService.connect(accountName, clientId, userId, brokerNode, message, true);
					//tight coupling - refer to "Device Level Authorization - Tight coupling" for the annotations
					//add alert check
//					Long accountId = (Long)edcPayload.getMetric(JmsUtil.METRIC_ACCOUNT_ID);
//					DeviceCredentialsTight deviceCredentialsTight = DeviceCredentialsTight.valueOf((String)edcPayload.getMetric(JmsUtil.METRIC_DEVICE_CREDENTALS_TIGHT));
//					switch (deviceCredentialsTight) {
//					case STRICT:
//						long deviceCountWithuserId = DeviceRegistryManagerTrusted.getDeviceCountByUserId(accountId, userId);
//						if (deviceCountWithuserId != 1) {
//							//create alert
//							createAlert(accountName, clientId, AlertCategories.SECURITY.toString(), 
//									MessageFormat.format(AlertMessage.TIGHT_DEVICE_ALERT_NOT_EXCLUSIVELY_VIOLATION, new Object[]{username, clientId, accountName}));
//							s_logger.warn("User {} is not used exclusively by the device {} (Account {})! (code {})", new Object[]{username, clientId, accountName, "2.b"});
//						}
//						break;
//					case LOOSE:
//						long deviceStrictCountWithuserId = DeviceRegistryManagerTrusted.getDeviceCountStrictByUserId(accountId, userId);
//						if (deviceStrictCountWithuserId > 0) {
//							//create alert
//							createAlert(accountName, clientId, AlertCategories.SECURITY.toString(), 
//									MessageFormat.format(AlertMessage.TIGHT_DEVICE_ALERT_USER_USED_BY_MORE_DEV, new Object[]{username, clientId, accountName}));
//                                s_logger.warn("User {} used by the device {} is used also by one or more strict devices (Account {})! (code {})",
//                                              new Object[] { username, clientId, accountName, "2.b" });
//						}
//						break;
//					default:
//						s_logger.warn("Wrong device credential tight attribute {}", deviceCredentialsTight);
//						break;
//					}
					metricDeviceDirectConnectMessage.inc();
				}
				else if (DISCONNECT.equals(semanticTopic)) {
					dvcService.disconnect(accountName, clientId, message);
					metricDeviceDirectDisconnectMessage.inc();
				}
				else if (PROVISION.equals(semanticTopic)) {
				    
					s_logger.info("Provision message arrived from: {}", clientId);
					
					// The provision message will provision the device with the provided credentials.
					String mqttUsername = (String) edcPayload.getMetric("username");
					String mqttPassword = (String) edcPayload.getMetric("password");
					String mqttActivationKey = (String) edcPayload.getMetric("activationKey");
					String mqttProvisioningVersion = (String) edcPayload.getMetric("provisioningCertificateVersion");

					ProvisionRequestService provisionService = locator.getProvisionService();
					provisionService.provision(clientId, 
					                           mqttUsername, 
					                           mqttPassword, 
					                           mqttActivationKey, 
					                           mqttProvisioningVersion);
					metricDeviceDirectProvisionMessage.inc();
				}
				else {
					metricDeviceDirectUnknownMessage.inc();
					s_logger.info("Unknown semantic part for $EDC topic: {}", message.getEdcTopic().getFullTopic());
					return;
				}
			}
		}
		catch (KapuaException e) {
			metricDeviceDirectErrorMessage.inc();
//			alerts.error(message.getEdcTopic().getAccount(), Code.INVALID_EDCTOPIC);
			s_logger.error("Error while processing device life-cycle event", e);
			return;			    
		}
		if (!CONNECT.equals(semanticTopic) && ! DISCONNECT.equals(semanticTopic)) {
			// FIXME: Remove - Republish Life cycle message to console
			if(semanticTopic.equals(LWT)) {
				edcPayload = new EdcPayload();
			}

			Date now = new Date();
			edcPayload.addMetric("server_event_time", Long.toString(now.getTime()));
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
                s_logger.debug("MESSAGE: {}", edcPayload);
                JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.NO_DESTINATION);
                JmsAssistantProducerWrapper producer = null;
                try {
                    producer = pool.borrowObject();
                    producer.sendRawMessage(destination.toString(), edcPayload.toDisplayString().getBytes());
                }
                finally {
                    pool.returnObject(producer);
                }
            } 
			catch (JMSException e) {
                s_logger.error("An error occurred while publishing device history event: {}", e.getMessage());
//                alerts.severe(message.getEdcTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, e.getErrorCode().toString());
                return;
            }
            catch (EdcInvalidTopicException e) {
                s_logger.error("An error occurred while publishing device history event: {}", e.getMessage());
//                alerts.severe(message.getEdcTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, e, "");
            }
            catch (Throwable t) {
                s_logger.warn("Cannot send life cycle message {}! {}", new Object[]{destination.toString(), t.getMessage()}, t);
//                alerts.severe(message.getEdcTopic().getAccount(), Code.PUBLISHING_DEVICE_ERROR, t, "");
                return;
            }
		}
	}
	
	private void processForwardedLifeCycleMessage(EdcMessage message, EdcTopic edcTopic) {
		String accountName = edcTopic.getAccount();
		String clientId	= edcTopic.getAsset();
		String semanticTopic = edcTopic.getSemanticTopic();

		// If a device with the same client id has connected to another broker
		// in the cluster, disconnect the connection on this broker.
		if (CONNECT.equals(semanticTopic)) {
			disconnectClient("mqtt", accountName, clientId);
			disconnectClient("mqtts", accountName, clientId);
		}
	}

	private void disconnectClient(String transportConnectorName, String accountName, String clientId) {
		BrokerService brokerService = BrokerRegistry.getInstance().lookup(EdcConfig.getInstance().getAmqBrokerName());

		TransportConnector tc = brokerService.getTransportConnectorByName(transportConnectorName);
		if(tc == null) {
			return;
		}
		
		String connectionIdString = MessageFormat.format(JmsConstants.MULTI_ACCOUNT_CLIENT_ID, accountName, clientId);
		for(TransportConnection conn : tc.getConnections()) {
			
			if(connectionIdString.equals(conn.getConnectionId())) {
				metricDeviceForwardedDisconnctClient.inc();
				s_logger.info("New connection detected for {} on another broker.  Stopping the current connection on transport connector: {}.", connectionIdString, tc.getName());
				try {
					// Include EdcDuplicateClientIdException to notify the security broker filter
					conn.stopAsync(new EdcDuplicateClientIdException(connectionIdString));;
				} 
				catch (Exception e) {
					metricDeviceForwardedDisconnctClientError.inc();
					s_logger.error("Could not stop connection: " + conn.getConnectionId(), e);
				}
				
				// assume only one connection since this broker should have already handled any duplicates
				break;
			}
		}
	}

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
//		catch (EdcException e) {
//			s_logger.error("Error during ALERT storage ", e);
//		}	
//	}
}
