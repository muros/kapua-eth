package org.eclipse.kapua.app.console.server.mqtt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.gwt.server.GwtAtmosphereResource;
import org.eclipse.kapua.app.console.server.AtmosphereManager;
import org.eclipse.kapua.app.console.server.AtmosphereManagerCreator;
import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;
import org.eclipse.kapua.service.authorization.AuthorizationServiceOLd;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.user.EdcSysAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.net.mqtt.MqttClient;
import com.eurotech.cloud.net.mqtt.MqttClientFactory;
import com.eurotech.cloud.net.mqtt.MqttException;
import com.eurotech.cloud.net.mqtt.MqttNotConnectedException;
import com.eurotech.cloud.net.mqtt.MqttPersistenceException;

public class ConsoleMqttClient implements Runnable {

    private static final Logger s_logger = LoggerFactory.getLogger(ConsoleMqttClient.class);

    private static final int POOL_SIZE = 3;
    private ExecutorService executor;
    private BlockingQueue<GwtEdcPublish> queue;

    private MqttClientCallback mqttClientCallbackHandler;
    private MqttClientFactory mqttClientFactory;
    private MqttClient mqttClient;

    private String brokerAddress;
    private String mqttClientId;
    private HashMap<String, ArrayList<String>> atmosphereClients = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> topics = new HashMap<String, ArrayList<String>>();

    private UpdateWorker[] updateWorkers;

    private boolean tryingToConnect = false;

    private ClientCleanupWorker m_clientCleanupWorker;

    public ConsoleMqttClient(String brokerAddress, String mqttClientId, String initialAtmosphereClientId, String[] intialTopics) throws Exception {
        this.brokerAddress = brokerAddress;
        this.mqttClientId = mqttClientId;

        // Create BlockingQueue
        queue = new LinkedBlockingQueue<GwtEdcPublish>();

        // Create the MQtt client
        s_logger.debug("brokerAddress: " + brokerAddress);
        mqttClientFactory = MqttClientFactory.getInstance();
        mqttClient = mqttClientFactory.newMqttClient(brokerAddress);

        //get a list of all of the topics
        ArrayList<String> topicsList = new ArrayList<String>();
        Iterator<Entry<String, ArrayList<String>>> it = atmosphereClients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it.next();
            topicsList.addAll(pairs.getValue());
        }

        // Create and register the MQtt callback handler
        mqttClientCallbackHandler = new MqttClientCallback(this, mqttClient, mqttClientId, topicsList, queue);
        mqttClient.setCallbackHandler(mqttClientCallbackHandler);

        // Create pool of workers
        executor = Executors.newFixedThreadPool(4);
        updateWorkers = new UpdateWorker[POOL_SIZE];
        for(int i=0; i<POOL_SIZE; i++) {
            updateWorkers[i] = new UpdateWorker(queue, atmosphereClients, topics, i);
        }

        //start the client
        s_logger.info("new ConsoleMqttClient for brokerAddress: " + brokerAddress + " with initialAtmosphereClientId: " + initialAtmosphereClientId);
        Thread managerThread = new Thread(this, "ConsoleMqttClient");
        managerThread.start();

        //add the initial configuration
        addSubscriptions(initialAtmosphereClientId, intialTopics);
    }

    public void addSubscriptions(String atmosphereClientId, String[] newSubscriptions) throws Exception {
        synchronized(atmosphereClients) {
            s_logger.info("adding subscriptions");

            //update the atmosphereClients
            ArrayList<String> existingSubscriptions = atmosphereClients.get(atmosphereClientId);
            if(existingSubscriptions == null) {
                existingSubscriptions = new ArrayList<String>();
            }
            for(int i=0; i<newSubscriptions.length; i++) {
                String newSubscription = newSubscriptions[i];

                boolean found = false;
                for(int j=0; j<existingSubscriptions.size(); j++) {
                    if(newSubscription.equals(existingSubscriptions.get(j))) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    s_logger.info("\tadding subscription: " + newSubscription);
                    existingSubscriptions.add(newSubscription);
                } else {
                    s_logger.info("\tnot adding subscription: " + newSubscription);
                }
            }
            atmosphereClients.put(atmosphereClientId, existingSubscriptions);

            //update topics
            for(int i=0; i<newSubscriptions.length; i++) {
                String newTopic = newSubscriptions[i];
                ArrayList<String> existingAtmosphereClients = topics.get(newTopic);
                if(existingAtmosphereClients == null) {
                    existingAtmosphereClients = new ArrayList<String>();
                }

                boolean found = false;
                for(int j=0; j<existingAtmosphereClients.size(); j++) {
                    if(atmosphereClientId.equals(existingAtmosphereClients.get(j))) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    s_logger.info("adding topic " + newTopic + " for " + atmosphereClientId);
                    existingAtmosphereClients.add(atmosphereClientId);
                }

                topics.put(newTopic, existingAtmosphereClients);
            }

            if(s_logger.isDebugEnabled()) {
                s_logger.info("updating the workers");

                Iterator<Entry<String, ArrayList<String>>> it = atmosphereClients.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it.next();
                    ArrayList<String> topics = pairs.getValue();
                    for(int j=0; j<topics.size(); j++) {
                        s_logger.info("atmosphere client " + pairs.getKey() + " with topic " + topics.get(j));
                    }
                }

                Iterator<Entry<String, ArrayList<String>>> it2 = topics.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it2.next();
                    ArrayList<String> atmosphereClients = pairs.getValue();
                    for(int j=0; j<atmosphereClients.size(); j++) {
                        s_logger.info("topic " + pairs.getKey() + " with atmosphere client " + atmosphereClients.get(j));
                    }
                }
            }

            //update the workers
            for(int i=0; i<POOL_SIZE; i++) {
                updateWorkers[i].updateTopics(this, atmosphereClients, topics);
            }

            //update the callback (so recovery can occur if needed)
            ArrayList<String> handlerTopics = new ArrayList<String>();
            Iterator<Entry<String, ArrayList<String>>> it = topics.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it.next();
                handlerTopics.add(pairs.getKey());
            }
            mqttClientCallbackHandler.updateTopics(handlerTopics);

            //update the subscriptions
            try {
                int[] qos = new int[newSubscriptions.length];
                for(int i=0; i<newSubscriptions.length; i++) {
                    qos[i] = 1;
                }

                if (mqttClient != null && mqttClient.isConnected())
                    mqttClient.subscribe(newSubscriptions, qos);
            } catch (MqttPersistenceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MqttNotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean removeSubscriptions(String atmosphereClientId, String[] subscriptions) throws Exception {
        boolean clientActive = true;
        synchronized(atmosphereClients) {
            for(String sub : subscriptions) {
                s_logger.info("attempting removal of " + sub + " from " + atmosphereClientId);
            }

            //update the atmosphereClients
            ArrayList<String> existingSubscriptions = atmosphereClients.get(atmosphereClientId);
            if(existingSubscriptions != null) {
                for(int i=0; i<subscriptions.length; i++) {
                    String subscription = subscriptions[i];

                    for(Iterator<String> iterator = existingSubscriptions.iterator(); iterator.hasNext();) {
                        if(subscription.equals(iterator.next())) {
                            iterator.remove();
                        }
                    }
                }

                if(existingSubscriptions.size() == 0) {
                    s_logger.info("removing atmosphere client " + atmosphereClientId + " because it has no subscriptions");
                    atmosphereClients.remove(atmosphereClientId);
                }
            }

            ArrayList<String> subscriptionsToRemove = new ArrayList<String>();

            //update topics
            for(int i=0; i<subscriptions.length; i++) {
                String oldTopic = subscriptions[i];
                ArrayList<String> existingAtmosphereClients = topics.get(oldTopic);

                if(existingAtmosphereClients != null) {
                    for(Iterator<String> iterator = existingAtmosphereClients.iterator(); iterator.hasNext();) {
                        if(atmosphereClientId.equals(iterator.next())) {
                            s_logger.info("removing " + atmosphereClientId + " from topic " + oldTopic + " in the MQTT client.  Size is now " + topics.size());
                            iterator.remove();
                        }
                    }

                    if(existingAtmosphereClients.size() == 0) {
                        s_logger.info("removing topic " + oldTopic + " because it has no attached atmosphere clients");
                        topics.remove(oldTopic);
                        subscriptionsToRemove.add(oldTopic);
                    }
                }
            }

            //if we are no longer subscribed to any topics - kill the MQTT client
            if(topics.size() == 0) {
                s_logger.info("shutting down the MQTT client because we have no subscriptions");
                shutdown();
                clientActive = false;
            } else {
                if(s_logger.isDebugEnabled()) {
                    s_logger.debug("updating the workers");
                    Iterator<Entry<String, ArrayList<String>>> it = atmosphereClients.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it.next();
                        ArrayList<String> topics = pairs.getValue();
                        for(int j=0; j<topics.size(); j++) {
                            s_logger.info("atmosphere client " + pairs.getKey() + " with topic " + topics.get(j));
                        }
                    }

                    Iterator<Entry<String, ArrayList<String>>> it2 = topics.entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it2.next();
                        ArrayList<String> atmosphereClients = pairs.getValue();
                        for(int j=0; j<atmosphereClients.size(); j++) {
                            s_logger.debug("topic " + pairs.getKey() + " with atmosphere client " + atmosphereClients.get(j));
                        }
                    }
                }

                //update the workers
                for(int i=0; i<POOL_SIZE; i++) {
                    updateWorkers[i].updateTopics(this, atmosphereClients, topics);
                }

                //update the callback (so recovery can occur if needed)
                s_logger.info("about to update the topics in the callbackhandler");
                ArrayList<String> handlerTopics = new ArrayList<String>();
                Iterator<Entry<String, ArrayList<String>>> it = topics.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>)it.next();
                    s_logger.info("handler topic: " + pairs.getKey());
                    handlerTopics.add(pairs.getKey());
                }
                mqttClientCallbackHandler.updateTopics(handlerTopics);

                //update the subscriptions if necessary
                try {
                    String[] toRemove = new String[subscriptionsToRemove.size()];
                    for(int i=0; i<toRemove.length; i++) {
                        s_logger.info("adding subscription for: " + subscriptions[i] + " to the list to be removed the MQTT client");
                        toRemove[i] = (String) subscriptionsToRemove.get(i);
                    }

                    if(toRemove.length > 0) {
                        if (mqttClient != null && mqttClient.isConnected())
                            mqttClient.unsubscribe(toRemove);
                    }
                } catch (MqttPersistenceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MqttNotConnectedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return clientActive;
    }

    public void shutdown() {
        synchronized(atmosphereClients) {
            try {
                m_clientCleanupWorker.setDone();

                mqttClient.disconnect();
                mqttClient.terminate();

                executor.shutdownNow();
                queue = null;
                updateWorkers = null;

                tryingToConnect = false;

                s_logger.info("ConsoleMqttClient  shutdown for brokerAddress: " + brokerAddress);

            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            } catch (MqttNotConnectedException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            synchronized(atmosphereClients) {
                tryingToConnect = true;
                s_logger.debug("trying to start console MQTT client");

                m_clientCleanupWorker = new ClientCleanupWorker();
                new Thread(m_clientCleanupWorker).start();

                for(int i=0; i<POOL_SIZE; i++) {
                    executor.execute(updateWorkers[i]);
                }

                // Connect the client
                int retry = 0;
                int maxRetries = 3;
                while(!mqttClient.isConnected() && retry < maxRetries) {
                    try {

                        s_logger.info("Attempting to connect...");

                        ServiceLocator locator = ServiceLocator.getInstance();
                        AuthorizationServiceOLd authServ = locator.getAuthorizationService();

                        // NOTE IMPORTANT!!
                        // for now use a generate token instead of the user in context
                        // we need to find out how to avoid that the user in context
                        // will create new devices upon connect
//         User currentUser = authServ.getCurrentUser();
//         String username = currentUser.getUsername();
//         String password = currentUser.getRawPassword();
//                      mqttClient.connect(mqttClientId, true, (short)10, username, password);

                        EdcSysAuthToken edcSysAuthToken = authServ.createEdcSysAuthToken(mqttClientId+"-");
                        String username = edcSysAuthToken.getUsername();
                        String password = edcSysAuthToken.getPassword();
                        String clientId = edcSysAuthToken.getClientId();
                        mqttClient.connect(clientId, true, (short)10, username, password);

                        tryingToConnect = false;

                        s_logger.info("ConsoleMqttClient  connected for brokerAddress: " + brokerAddress);

                        //reset the subscriptions
                        int i = 0;
                        int[] qos = new int[topics.size()];
                        List<String> topicsToSubscribe = new ArrayList<String>();
                        Iterator<String> subscribedTopics = topics.keySet().iterator();
                        while (subscribedTopics.hasNext()) {
                            topicsToSubscribe.add(subscribedTopics.next());
                            qos[i++] = 1;
                        }
                        mqttClient.subscribe(topicsToSubscribe.toArray( new String[] {}), qos);
                    } catch (Exception e) {
                        s_logger.debug("connection failed");
                        e.printStackTrace();
                        // Try again after 1 second
                        Thread.sleep(1000);
                    }
                    retry++;
                }
                if(!mqttClient.isConnected()) {
                    s_logger.info("ConsoleMqttClient could not connect to " + brokerAddress + " - shutting down...");
                    shutdown();
                }
            }
        } catch (Exception e) {
            s_logger.error("Error encountered while initializing MQtt client", e);
            e.printStackTrace();
        }
    }

    /**
     * worker class to clean up the MQTT clients based on Atmosphere sessions expiring
     *
     * @author eurotech
     *
     */
    private class ClientCleanupWorker implements Runnable {

        private static final int DELAY = 1*60*1000;   //10 mins

        private boolean done;

        public void setDone() {
            done = true;
        }

        public void run() {
            done = false;
            while(!done) {

                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized(atmosphereClients) {
                    s_logger.debug("checking for broadcasters...");
                    AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
                    Broadcaster b = BroadcasterFactory.getDefault().lookup(Broadcaster.class, "GWT_COMET");

                    if (b != null) {
                        try {
                            if(atmosphereClients != null && !atmosphereClients.isEmpty()) {
                                String[] atmosphereClientIds = atmosphereClients.keySet().toArray(new String[atmosphereClients.size()]);
                                for(int i=0; i<atmosphereClients.size(); i++) {
                                    String atmosphereClientId = atmosphereClientIds[i];
                                    GwtAtmosphereResource gwtAtmosphereResource = atmosphereManager.getGwtAtmosphereResource(atmosphereClientId);

                                    if(gwtAtmosphereResource == null) {
                                        s_logger.info("cleaning up in MQTT client subscriptions because " + atmosphereClientId + " is no longer connected");
                                        ArrayList<String> topics = atmosphereClients.get(atmosphereClientId);
                                        String[] topicsForRemoval = new String[topics.size()];
                                        for(int j=0; j<topics.size(); j++) {
                                            s_logger.debug("unsubscribing to " + topics.get(j) + " for " + atmosphereClientId);
                                            topicsForRemoval[j] = topics.get(j);
                                        }
                                        try {
                                            removeSubscriptions(atmosphereClientId, topicsForRemoval);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        //its not null - but make sure it still alive
                                        if(!gwtAtmosphereResource.isAlive()) {
                                            s_logger.info(atmosphereClientId + " is no longer alive");
                                            subscriptionCleanup(atmosphereClientId);
                                        }

                                        //check for expired sessions
                                        try {
                                            if(gwtAtmosphereResource.getSession(false) != null) {
                                                s_logger.debug("Session is not null");
                                            } else {
                                                s_logger.info("Session is null - need to clean up for " + atmosphereClientId);
                                                subscriptionCleanup(atmosphereClientId);
                                            }
                                        } catch(IllegalStateException ise) {
                                            s_logger.info("Session is ??? - need to clean up for " + atmosphereClientId);
                                            subscriptionCleanup(atmosphereClientId);
                                        }
                                    }
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        s_logger.debug("broadcasters is null");
                    }
                }
            }
        }
    }

    private void subscriptionCleanup(String atmosphereClientId) {
        synchronized(atmosphereClients) {
            s_logger.info("cleaning up in MQTT client subscriptions for " + atmosphereClientId);
            ArrayList<String> topics = atmosphereClients.get(atmosphereClientId);
            if(topics == null) {
                return;
            }
            String[] topicsForRemoval = new String[topics.size()];
            for(int i=0; i<topics.size(); i++) {
                s_logger.debug("unsubscribing to " + topics.get(i) + " for " + atmosphereClientId);
                topicsForRemoval[i] = topics.get(i);
            }
            try {
                removeSubscriptions(atmosphereClientId, topicsForRemoval);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //b.removeAtmosphereResource(gwtAtmosphereResource.getAtmosphereResource());
            //gwtAtmosphereResource.getAtmosphereResource().suspend();
            //gwtAtmosphereResource.getAtmosphereResource().getAtmosphereHandler().destroy();
        }
    }

    public boolean isTryingToConnect() {
        return tryingToConnect;
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    /*
    //destroy the client?
    private void destroyAtmosphereClient(String atmosphereClientId) {
     synchronized(atmosphereClients) {
      AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
      GwtAtmosphereResource gwtAtmosphereResource = atmosphereManager.getGwtAtmosphereResource(atmosphereClientId);
      gwtAtmosphereResource.getAtmosphereResource().getAtmosphereHandler().destroy();
     }
    }*/
}
