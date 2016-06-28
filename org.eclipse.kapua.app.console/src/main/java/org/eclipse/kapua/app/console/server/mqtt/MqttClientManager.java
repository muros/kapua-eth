package org.eclipse.kapua.app.console.server.mqtt;

import java.util.HashMap;

import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcErrorCode;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtMqttClientService;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.authorization.AuthorizationServiceOLd;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.Permission.Action;
import org.eclipse.kapua.service.authorization.Permission.Domain;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.message.EdcInvalidTopicException;
import com.eurotech.cloud.message.EdcTopic;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MqttClientManager extends RemoteServiceServlet implements GwtMqttClientService {

    private static final long                  serialVersionUID = -499617904082431679L;

    private static final Logger                s_logger         = LoggerFactory.getLogger(MqttClientManager.class);

    private HashMap<String, ConsoleMqttClient> mqttClients      = new HashMap<String, ConsoleMqttClient>();
    private static int                         clientIdIndex    = 0;

    public MqttClientManager() {
    }

    public synchronized void subscribe(GwtXSRFToken xsrfToken, String brokerAddress, String atmosphereClientId, String[] topics)
    throws GwtEdcException {

        // security check. make sure the client is allowed to perform this subscription
        checkTopics(topics);

        // see if we have a client to this broker already
        ConsoleMqttClient mqttClient = mqttClients.get(brokerAddress);
        if (mqttClient == null || (!mqttClient.isConnected() && !mqttClient.isTryingToConnect())) {
            // create the MQTT Client
            try {
                mqttClient = new ConsoleMqttClient(brokerAddress,
                                                   "eurotech-console-" + clientIdIndex,
                                                   atmosphereClientId,
                                                   topics);
            } catch (Exception e) {
                s_logger.error("Error setting up MqttClient for Live functionality", e);
                throw new GwtEdcException(GwtEdcErrorCode.INTERNAL_ERROR, e);
            }
            mqttClients.put(brokerAddress, mqttClient);
            clientIdIndex++;
        } else {
            try {
                mqttClient.addSubscriptions(atmosphereClientId, topics);
            } catch (Exception e) {
                s_logger.error("Exception while adding subscriptions!", e);
            }

            for (int i = 0; i < topics.length; i++) {
                s_logger.info("subscribe for {} on broker: {} with atmosphereClientId: {}",
                              new Object[] { topics[i], brokerAddress, atmosphereClientId });
            }
        }
    }

    public synchronized void unsubscribe(GwtXSRFToken xsrfToken, String brokerAddress, String atmosphereClientId, String[] topics)
    throws GwtEdcException {

        // security check. make sure the client is allowed to perform this subscription
        checkTopics(topics);

        // see if we have a client to this broker already
        ConsoleMqttClient mqttClient = mqttClients.get(brokerAddress);
        if (mqttClient == null) {
            return;
        }
        if (topics != null) {
            try {
                if (!mqttClient.removeSubscriptions(atmosphereClientId, topics)) {
                    mqttClients.remove(brokerAddress);
                }
            } catch (Exception e) {
                s_logger.error("Exception while removing subscriptions!", e);
            }
            for (int i = 0; i < topics.length; i++) {
                s_logger.info("unsubscribing for {} on broker: {} with atmosphereClientId {}",
                              new Object[] { topics[i], brokerAddress, atmosphereClientId });
            }
        }
    }

    private void checkTopics(String[] topics)
    throws GwtEdcException {
        if (topics == null || topics.length == 0) {
            throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_NULL_ARGUMENT, null, "topics");
        }

        // security checks
        // make sure current user is allowed to view data under this topic
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        AccountServiceOld accountService = serviceLocator.getAccountService();
        AuthorizationServiceOLd authService = serviceLocator.getAuthorizationService();
        try {

            for (String topic : topics) {

                EdcTopic edcTopic = new EdcTopic(topic);
                String accountName = edcTopic.getAccount();
                if (accountName == null || accountName.trim().length() == 0) {
                    throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ARGUMENT, null, "topics");
                }
                long accountId = accountService.getAccountId(accountName);

                Permission permAccountView = new Permission(Domain.account, Action.view, accountId);
                authService.checkAccess(permAccountView);

                Permission permDataView = new Permission(Domain.data, Action.view, accountId);
                authService.checkAccess(permDataView);
            }
        } catch (EdcInvalidTopicException et) {
            throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ARGUMENT, et, "topics");
        } catch (Exception e) {
            EdcExceptionHandler.handle(e);
        }
    }
}
