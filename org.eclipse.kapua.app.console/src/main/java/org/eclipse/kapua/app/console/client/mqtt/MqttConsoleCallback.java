package org.eclipse.kapua.app.console.client.mqtt;

import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;


public interface MqttConsoleCallback {

    /**
     * a connection to the broker is lost
     */
    public void connectionLost();

    /**
     * a publish occurs on a topic we are subscribed on
     *
     * @param publish the topic and payload of a published message
     */
    public void publishArrived(GwtEdcPublish publish);

    /**
     * an acknowledgement of a publish being received by the broker
     *
     * @param messageId the id of the message
     */
    public void published(int messageId);

    /**
     * an acknowledgement of a subscription being received by the broker
     *
     * @param messageId the id of the message
     */
    public void subscribed(int messageId);

    /**
     * an acknowledgement of a subscription being received by the broker
     *
     * @param messageId the id of the message
     */
    public void unsubscribed(int messageId);

}
