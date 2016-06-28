package org.eclipse.kapua.app.console.client.mqtt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;
import org.eclipse.kapua.app.console.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;
import org.eclipse.kapua.app.console.shared.model.GwtEdcPublishSerializer;
import org.eclipse.kapua.app.console.shared.model.GwtMqttTopic;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtMqttClientService;
import org.eclipse.kapua.app.console.shared.service.GwtSecurityTokenService;

import com.allen_sauer.gwt.log.client.Log;
import com.eurotech.cloud.console.shared.service.GwtMqttClientServiceAsync;
import com.eurotech.cloud.console.shared.service.GwtSecurityTokenServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class MqttMessageDispatcher implements AtmosphereListener
{
    private final GwtMqttClientServiceAsync                              mqttClientService = GWT.create(GwtMqttClientService.class);

    private HashMap<GwtMqttTopic, HashMap<Integer, MqttConsoleCallback>> globalTopics      = new HashMap<GwtMqttTopic, HashMap<Integer, MqttConsoleCallback>>();
    private HashMap<Integer, ArrayList<String>>                          globalCallbacks   = new HashMap<Integer, ArrayList<String>>();
    private HashMap<Integer, String>                                     brokerUrlById     = new HashMap<Integer, String>();

    private int                                                          idIndex           = -1;
    private GwtSession                                                   currentSession;
    private AtmosphereClient                                             client;
    private String                                                       clientId;

    private final GwtSecurityTokenServiceAsync                           gwtXSRFService    = GWT.create(GwtSecurityTokenService.class);
    private GwtXSRFToken                                                 xsrfToken;

    public MqttMessageDispatcher(GwtSession currentSession)
    {

        // XSRF
        gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

            @Override
            public void onFailure(Throwable ex)
            {
                FailureHandler.handle(ex);
            }

            @Override
            public void onSuccess(GwtXSRFToken token)
            {
                xsrfToken = token;
            }
        });

        Log.debug("starting the MQTT Message Dispatcher");

        this.currentSession = currentSession;

        StringBuilder sb = new StringBuilder();
        sb.append("console-");
        sb.append(currentSession.getSelectedAccount().getName());
        sb.append("-");
        sb.append(new Random().nextInt());
        clientId = sb.toString();

        AtmosphereGWTSerializer serializer = GWT.create(GwtEdcPublishSerializer.class);
        // set a small length parameter to force refreshes
        // normally you should remove the length parameter
        client = new AtmosphereClient(GWT.getModuleBaseURL() + "gwtComet?clientId=" + clientId, serializer, this);
        client.start();
    }

    public void shutdown()
    {
        client.stop();
        client = null;

        globalTopics = null;
        globalCallbacks = null;
    }

    public void onConnected(int heartbeat, int connectionID)
    {
        Log.debug("comet.connected [" + heartbeat + ", " + connectionID + "]");
    }

    public void onBeforeDisconnected()
    {
        Log.debug("comet.beforeDisconnected");
    }

    public void onDisconnected()
    {
        Log.debug("comet.disconnected");
    }

    public void onError(Throwable exception, boolean connected)
    {
        int statuscode = -1;
        if (exception instanceof StatusCodeException) {
            statuscode = ((StatusCodeException) exception).getStatusCode();
        }
        Log.debug("comet.error [connected=" + connected + "] (" + statuscode + ")", exception);
    }

    public void onHeartbeat()
    {
        Log.debug("comet.heartbeat [" + client.getConnectionID() + "]");
    }

    public void onRefresh()
    {
        Log.debug("comet.refresh [" + client.getConnectionID() + "]");
    }

    public void onAfterRefresh()
    {
        Log.debug("comet.onAfterRefresh [" + client.getConnectionID() + "]");
    }

    public void onMessage(List<? extends Serializable> messages)
    {
        for (Serializable obj : messages) {
            if (obj instanceof GwtEdcPublish) {
                GwtMqttTopic incomingTopic = ((GwtEdcPublish) obj).getGwtMqttTopic();

                Log.debug("Incoming publish on: " + incomingTopic + "\n\tSize of topics is: " + globalTopics.size() + "\n\tSize of callbacks is: " + globalCallbacks.size());

                // find 'equivalent' topics
                Iterator<Entry<GwtMqttTopic, HashMap<Integer, MqttConsoleCallback>>> globalTopicsIterator = globalTopics.entrySet().iterator();
                while (globalTopicsIterator.hasNext()) {
                    Map.Entry<GwtMqttTopic, HashMap<Integer, MqttConsoleCallback>> topicPairs = (Entry<GwtMqttTopic, HashMap<Integer, MqttConsoleCallback>>) globalTopicsIterator.next();
                    Log.debug("trying to match against: " + topicPairs.getKey());
                    if (incomingTopic.equals(topicPairs.getKey())) {
                        Log.debug("Found a match: " + topicPairs.getKey());
                        HashMap<Integer, MqttConsoleCallback> callbacks = topicPairs.getValue();

                        if (callbacks != null) {
                            Iterator<Entry<Integer, MqttConsoleCallback>> callbacksIterator = callbacks.entrySet().iterator();
                            while (callbacksIterator.hasNext()) {
                                Map.Entry<Integer, MqttConsoleCallback> callbackPairs = (Entry<Integer, MqttConsoleCallback>) callbacksIterator.next();
                                Log.debug("Dispatching to callback with ID: " + callbackPairs.getKey());
                                MqttConsoleCallback callback = callbackPairs.getValue();
                                callback.publishArrived(((GwtEdcPublish) obj));
                            }
                        }
                        else {
                            Log.debug("Callbacks is null");
                        }
                    }
                }
            }
            else {
                ConsoleInfo.display("[" + client.getConnectionID() + "] Received " + messages.size() + " messages", obj.toString());
            }
        }
    }

    public int registerCallback(String brokerUrl, MqttConsoleCallback callback, String[] subscriptions)
    {
        synchronized (globalTopics) {
            Log.debug("registering new client: " + idIndex + " \n\tSize of topics is now: " + globalTopics.size() + "\n\tSize of callbacks is now: " + globalCallbacks.size());

            if (subscriptions == null || callback == null) {
                return -1;
            }

            mqttClientService.subscribe(xsrfToken, brokerUrl, clientId, subscriptions, new AsyncCallback<Void>() {
                public void onFailure(Throwable arg0)
                {
                    Log.debug("Failed to subscribe to topics");
                }

                public void onSuccess(Void arg0)
                {
                    Log.debug("Successfully subscribed to topics");
                }
            });

            // init
            idIndex++;
            ArrayList<String> topicStrings = new ArrayList<String>();

            // add to topics
            for (int i = 0; i < subscriptions.length; i++) {
                HashMap<Integer, MqttConsoleCallback> mqttConsoleCallbacks = this.globalTopics.get(subscriptions[i]);
                if (mqttConsoleCallbacks == null) {
                    mqttConsoleCallbacks = new HashMap<Integer, MqttConsoleCallback>();
                }

                mqttConsoleCallbacks.put(new Integer(idIndex), callback);

                this.globalTopics.put(new GwtMqttTopic(subscriptions[i]), mqttConsoleCallbacks);
                topicStrings.add(subscriptions[i]);
            }

            // add to callbacks
            globalCallbacks.put(new Integer(idIndex), topicStrings);
            brokerUrlById.put(idIndex, brokerUrl);

            Log.debug("registered new client: " + idIndex + " \n\tSize of topics is now: " + globalTopics.size() + "\n\tSize of callbacks is now: " + globalCallbacks.size());
            return idIndex;
        }
    }

    public void unregisterCallback(int id)
    {

        if (globalTopics == null) {
            return;
        }

        synchronized (globalTopics) {
            // get the topics to iterate over
            ArrayList<String> topicStrings = globalCallbacks.get(id);
            if (topicStrings == null) {
                return;
            }

            for (String topic : topicStrings) {
                // remove from topics
                HashMap<Integer, MqttConsoleCallback> callbacks = this.globalTopics.get(topic);
                if (callbacks != null) {
                    callbacks.remove(id);
                }
            }

            // remove from callbacks
            globalCallbacks.remove(id);

            // get the broker url for this id
            String brokerUrl = brokerUrlById.remove(id);

            mqttClientService.unsubscribe(xsrfToken,
                                          brokerUrl,
                                          clientId,
                                          topicStrings.toArray(new String[0]),
                                          new AsyncCallback<Void>() {
                                              public void onFailure(Throwable arg0)
                                              {
                                                  Log.info("Failed to unsubscribe to topics");
                                              }

                                              public void onSuccess(Void arg0)
                                              {
                                                  Log.info("Successfully unsubscribed to topics");
                                              }
                                          });
        }
    }

    public void unregisterAllCallbacks()
    {
        synchronized (globalTopics) {

            if (globalTopics == null) {
                return;
            }

            Iterator<Entry<Integer, ArrayList<String>>> it = globalCallbacks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, ArrayList<String>> pairs = (Map.Entry<Integer, ArrayList<String>>) it.next();
                Integer id = pairs.getKey();
                ArrayList<String> topicStrings = pairs.getValue();

                if (topicStrings == null) {
                    return;
                }

                for (String topic : topicStrings) {
                    // remove from topics
                    HashMap<Integer, MqttConsoleCallback> callbacks = this.globalTopics.get(topic);
                    if (callbacks != null) {
                        callbacks.remove(id);
                    }
                }

                // remove from callbacks
                globalCallbacks.remove(id);

                mqttClientService.unsubscribe(xsrfToken, currentSession.getSelectedAccount().getBrokerURL(),
                                              clientId, topicStrings.toArray(new String[0]),
                                              new AsyncCallback<Void>() {
                                                  public void onFailure(Throwable arg0)
                                                  {
                                                      Log.debug("Failed to unsubscribe to topics");
                                                  }

                                                  public void onSuccess(Void arg0)
                                                  {
                                                      Log.debug("Successfully unsubscribed to topics");
                                                  }
                                              });
            }
        }
    }
}
