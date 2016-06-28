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
package org.eclipse.kapua.app.console.server.mqtt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;
import org.eclipse.kapua.app.console.shared.model.GwtMqttTopic;
import org.eclipse.kapua.service.account.AccountDataIndexBy;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.account.AccountServicePlan;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.message.EdcPayload;
import com.eurotech.cloud.message.EdcTopic;
import com.eurotech.cloud.net.mqtt.MqttCallbackHandler;
import com.eurotech.cloud.net.mqtt.MqttClient;
import com.eurotech.cloud.net.mqtt.MqttException;
import com.eurotech.cloud.net.mqtt.MqttNotConnectedException;
import com.eurotech.cloud.net.mqtt.MqttPersistenceException;

public class MqttClientCallback implements MqttCallbackHandler {

    private static final Logger s_logger = LoggerFactory.getLogger(MqttClientCallback.class);

    private BlockingQueue<GwtEdcPublish> queue;
    private MqttClient mqttClient;
    private ConsoleMqttClient m_console;

    private ArrayList<String> topics;

    @SuppressWarnings("unused")
    private String mqttClientId;
    @SuppressWarnings("unused")
    private short keepAlive;

    protected MqttClientCallback(ConsoleMqttClient console, MqttClient client, String mqttClientId, ArrayList<String> topics, BlockingQueue<GwtEdcPublish> queue) {
        this.m_console = console;
        this.mqttClient = client;
        this.mqttClientId = mqttClientId;
        this.topics = topics;
        this.queue = queue;
        this.keepAlive = 10;
    }

    public void updateTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    public void connectionLost() {
        int retry = 0;
        int maxRetries = 3;
        boolean subscriptionsRestored = false;
        while (!subscriptionsRestored && retry < maxRetries) {
            synchronized(topics) {
                s_logger.info("connectionLost() Attempting to restore subscriptions...");
                if (mqttClient != null && mqttClient.isConnected()) {

                    String [] subTopics = new String[topics.size()];
                    int [] qos = new int[topics.size()];
                    for(int i=0; i<topics.size(); i++) {
                        subTopics[i] = topics.get(i);
                        qos[i] = 1;
                    }
                    try {
                        mqttClient.subscribe(subTopics, qos);
                        s_logger.info("subscriptionsRestored!");
                        subscriptionsRestored = true;
                    } catch (MqttPersistenceException e) {
                        e.printStackTrace();
                    } catch (MqttNotConnectedException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry++;
        }

        if(!subscriptionsRestored) {
            s_logger.info("Could not restore subscription.  Shutting down...");
            m_console.shutdown();
        }
    }

    public void publishArrived(String topic, byte[] payload, int qos, boolean retained) {
        s_logger.debug("publishArrived...");

        Date timestamp = new Date();
        EdcPayload edcPayload = null;
        try {
            edcPayload = EdcPayload.buildFromByteArray(payload);
        } catch (Exception e) {
            s_logger.debug("Non-protobuf message received");
        }

        // Create the EDC Publish object and add to the queue
        GwtEdcPublish publish = null;
        if (edcPayload == null) {
            publish = new GwtEdcPublish(new GwtMqttTopic(topic), timestamp, payload);
        } else {

            // handle timestamp. If the account has order by device
            // timestamp, adjust the timestamp before republishing it
            if (edcPayload.getTimestamp() != null) {
                try {
                    ServiceLocator serviceLocator = ServiceLocator.getInstance();
                    AccountServiceOld accountService = serviceLocator.getAccountService();

                    EdcTopic  edcTopic = new EdcTopic(topic);
                    String accountName = edcTopic.getAccount();
                    long     accountId = accountService.getAccountId(accountName);
                    AccountServicePlan asp = accountService.getAccountServicePlanTrusted(accountId);
                    if (asp.getDataIndexBy().equals(AccountDataIndexBy.DEVICE_TIMESTAMP)) {
                        timestamp = edcPayload.getTimestamp();
                    }
                } catch (Exception e) {
                    s_logger.warn("Error adjusting message timestamp",e);
                }
            }

            // handle metrics
            Map<String,Number> values = new HashMap<String,Number>();
            for (String header : edcPayload.metricNames()) {
                Object value = edcPayload.getMetric(header);
                if (Number.class.isAssignableFrom(value.getClass())) {
                    values.put(header, (Number) value);
                }
            }
            Map<String,String> stringValues = new HashMap<String,String>();
            for (String header : edcPayload.metricNames()) {
                Object value = edcPayload.getMetric(header);
                if (value instanceof String) {
                    stringValues.put(header, (String) value);
                }
            }
            if (values.size() > 0 || stringValues.size() > 0) {
                publish = new GwtEdcPublish(new GwtMqttTopic(topic), timestamp, values, stringValues);
            }
        }
        try {
            if (publish != null) {
                s_logger.debug("putting into the queue");
                queue.put(publish);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void published(int arg0) {

    }

    public void subscribed(int arg0, byte[] arg1) {

    }

    public void unsubscribed(int arg0) {

    }
}
