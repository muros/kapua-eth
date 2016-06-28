package org.eclipse.kapua.app.console.server.mqtt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.gwt.server.GwtAtmosphereResource;
import org.eclipse.kapua.app.console.server.AtmosphereManager;
import org.eclipse.kapua.app.console.server.AtmosphereManagerCreator;
import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;
import org.eclipse.kapua.app.console.shared.model.GwtMqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateWorker implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(UpdateWorker.class);
		
	private BlockingQueue<GwtEdcPublish> queue;
	private ConsoleMqttClient consoleMqttClient;
	private HashMap<String, ArrayList<String>> atmosphereClients = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> topics = new HashMap<String, ArrayList<String>>();
	private boolean running;
	
    protected UpdateWorker(BlockingQueue<GwtEdcPublish> queue,
                           HashMap<String, ArrayList<String>> atmosphereClients,
                           HashMap<String, ArrayList<String>> topics,
                           int index)
    {
		this.running = true;
		this.queue = queue;
		this.atmosphereClients = atmosphereClients;
		this.topics = topics;
	}
	
    public void updateTopics(ConsoleMqttClient consoleMqttClient,
                             HashMap<String, ArrayList<String>> atmosphereClients,
                             HashMap<String,
                             ArrayList<String>> topics)
    {
		this.consoleMqttClient = consoleMqttClient;
		this.atmosphereClients = atmosphereClients;
		this.topics = topics;
	}
	
    public void stop()
    {
		running = false;
	}
		
    public void run()
    {
		GwtEdcPublish nextPublish = null;
        while (running) {
			try {
				// Grab message from queue
                logger.debug("Pulling publish from the queue in the worker");
				nextPublish = queue.take();
				// Process the publish
				processPublish(nextPublish);
            }
            catch (InterruptedException e) {
                logger.info("Thread was interrupted!");
				break;
			}
            catch (Exception e) {
                logger.error("Error: " + e.getMessage(), e);
            }
		}
	}

    private void processPublish(GwtEdcPublish publish)
    {
        logger.info("Got a publish: {} - Iterating over {} topic(s)", publish.getGwtMqttTopic(), topics.size());
		GwtMqttTopic publishTopic = publish.getGwtMqttTopic();
		
		Iterator<Entry<String, ArrayList<String>>> it = topics.entrySet().iterator();
		while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> pairs = (Entry<String, ArrayList<String>>) it.next();

			GwtMqttTopic hashTopic = new GwtMqttTopic(pairs.getKey());
            logger.info("Comparing publish with: {}", hashTopic.getFullTopic());
            if (hashTopic.equals(publishTopic)) {
                logger.info("Found match to publish to: {}", publishTopic.getFullTopic());
				ArrayList<String> atmosphereClients = topics.get(hashTopic.getFullTopic());
                if (atmosphereClients == null) {
					continue;
				}
				
                for (int i = 0; i < atmosphereClients.size(); i++) {
					String atmosphereClientId = atmosphereClients.get(i);
                    logger.info("Checking to see if we need to broadcast to: {}", atmosphereClientId);
					AtmosphereManager atmosphereManager = new AtmosphereManagerCreator().getAtmosphereManager();
					Broadcaster b = BroadcasterFactory.getDefault().lookup(Broadcaster.class, "GWT_COMET");
				    if (b != null) {
				    	GwtAtmosphereResource gwtAtmosphereResource = atmosphereManager.getGwtAtmosphereResource(atmosphereClientId);
				    	
                        if (gwtAtmosphereResource != null) {
				    		AtmosphereResource atmosphereResource = gwtAtmosphereResource.getAtmosphereResource();
                            logger.info("Broadcasting to {} \nResource: {}", atmosphereClientId, atmosphereResource);
				    		b.broadcast(publish, atmosphereManager.getGwtAtmosphereResource(atmosphereClientId).getAtmosphereResource());
                        }
                        else {
                            logger.info("Cleaning up in MQTT client subscriptions because {} is no longer connected - removing: {}", atmosphereClientId, hashTopic.getFullTopic());
				    		ArrayList<String> topics = this.atmosphereClients.get(atmosphereClientId);
				    		String[] topicsForRemoval = new String[topics.size()];
                            for (int j = 0; j < topics.size(); j++) {
                                logger.debug("Unsubscribing to: {} for: {}", topics.get(j), atmosphereClientId);
				    			topicsForRemoval[j] = topics.get(j);
				    		}
				    		try {
								consoleMqttClient.removeSubscriptions(atmosphereClientId, topicsForRemoval);
								running = false;
								i--;
                            }
                            catch (Exception e) {
								e.printStackTrace();
							}
				    	}
			        }
                    else {
                        logger.info("Not broadcasting because the GWT_COMET broadcaster is null");
                    }
				}
			}
		}
		
        logger.info("Done processing publish {}", topics.size());
	}
}
