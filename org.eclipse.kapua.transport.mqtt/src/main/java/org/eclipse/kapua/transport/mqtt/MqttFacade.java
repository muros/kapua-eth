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
package org.eclipse.kapua.transport.mqtt;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.transport.TransportFacade;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.mqtt.pooling.MqttClientPool;

public class MqttFacade implements TransportFacade<MqttTopic, MqttPayload, MqttMessage, MqttMessage>
{
    private MqttClient         borrowedClient;
    private MqttClientCallback mqttClientCallback;

    public MqttFacade() throws Exception
    {
        //
        // Get the client form the pool
        borrowedClient = MqttClientPool.getInstance().borrowObject();
    }

    //
    // Message management
    //
    @Override
    public void sendAsync(MqttMessage mqttMessage)
        throws KapuaException
    {
        sendSync(mqttMessage, null);
    }

    @Override
    public MqttMessage sendSync(MqttMessage mqttMessage, Long timeout)
        throws KapuaException
    {
        List<MqttMessage> responses = new ArrayList<>();

        sendInternal(mqttMessage, responses, timeout);

        if (timeout != null) {
            if (responses.isEmpty()) {
                throw new MqttClientException(MqttClientErrorCodes.CLIENT_TIMEOUT_EXCEPTION,
                                              null,
                                              new Object[] {
                                                             mqttMessage.getRequestTopic()
                                              });

            }

            return responses.get(0);
        }
        else {
            return null;
        }
    }

    private void sendInternal(MqttMessage mqttMessage, List<MqttMessage> responses, Long timeout)
        throws KapuaException
    {
        try {
            //
            // Subscribe if necessary
            if (mqttMessage.getResponseTopic() != null) {
                try {
                    mqttClientCallback = new MqttClientCallback(responses);
                    borrowedClient.setCallback(mqttClientCallback);
                    borrowedClient.subscribe(mqttMessage.getResponseTopic());
                }
                catch (KapuaException e) {
                    throw new MqttClientException(MqttClientErrorCodes.CLIENT_SUBSCRIBE_ERROR,
                                                  e,
                                                  new Object[] { mqttMessage.getResponseTopic().getTopic() });
                }
            }

            //
            // Publish message
            try {
                borrowedClient.publish(mqttMessage);
            }
            catch (KapuaException e) {
                throw new MqttClientException(MqttClientErrorCodes.CLIENT_PUBLISH_ERROR,
                                              e,
                                              new Object[] { mqttMessage.getRequestTopic().getTopic(),
                                                             mqttMessage.getPayload().getBody() });
            }

            //
            // Wait if required
            if (timeout != null &&
                mqttMessage.getResponseTopic() != null) {
                Timer timeoutTimer = new Timer(new StringBuilder().append(MqttFacade.class.getSimpleName())
                                                                  .append("-TimeoutTimer-")
                                                                  .append(borrowedClient.getClientId())
                                                                  .toString(),
                                               true);

                timeoutTimer.schedule(new TimerTask() {

                    @Override
                    public void run()
                    {
                        if (mqttMessage.getResponseTopic() != null) {
                            synchronized (mqttClientCallback) {
                                mqttClientCallback.notifyAll();
                            }
                        }
                    }
                }, timeout);

                try {
                    synchronized (mqttClientCallback) {
                        mqttClientCallback.wait();
                    }
                }
                catch (InterruptedException e) {
                    Thread.interrupted();
                    throw new MqttClientException(MqttClientErrorCodes.CLIENT_CALLBACK_ERROR,
                                                  e,
                                                  (Object[]) null);
                }
                finally {
                    timeoutTimer.cancel();
                }
            }
        }
        catch (Exception e) {
            throw new MqttClientException(MqttClientErrorCodes.SEND_ERROR,
                                          e,
                                          mqttMessage.getRequestTopic().getTopic());
        }
    }

    @Override
    public String getClientId()
    {
        return borrowedClient.getClientId();
    }

    @Override
    public Class<MqttMessage> getMessageClass()
    {
        return MqttMessage.class;
    }

    @Override
    public void clean()
    {
        //
        // Return the client form the pool
        MqttClientPool.getInstance().returnObject(borrowedClient);
        borrowedClient = null;
    }
}
