package org.eclipse.kapua.app.console.shared.service;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("mqtt")
public interface GwtMqttClientService extends RemoteService {

    /**
     * subscribes to the specified topics - auto connects the MQTT client if necessary
     *
     * @param brokerAddress   the URL of the broker
     * @param clientId    the client ID
     * @param topics    the topics to subscribe to
     * @throws GwtEdcException  if the initialization fails
     */
    public void subscribe(GwtXSRFToken xsfrToken, String brokerAddress, String clientId, String[] topics) throws GwtEdcException;

    /**
     * unsubscribes to the specified topics
     *
     * @param brokerAddress   the URL of the broker
     * @param clientId    the client ID
     * @param topics    the topics to unsubscribe from
     * @throws GwtEdcException  if the initialization fails
     */
    public void unsubscribe(GwtXSRFToken xsfrToken, String brokerAddress, String clientId, String[] topics) throws GwtEdcException;
}
