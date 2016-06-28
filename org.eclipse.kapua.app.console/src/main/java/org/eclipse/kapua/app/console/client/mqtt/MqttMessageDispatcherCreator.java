package org.eclipse.kapua.app.console.client.mqtt;

import org.eclipse.kapua.app.console.shared.model.GwtSession;

public class MqttMessageDispatcherCreator {

    /**
     * Instance of the {@link MqttMessageDispatcher}
     * */
    private static MqttMessageDispatcher mqttMessageDispatcher;

    /**
     * Default constructor<br>
     * Build the instance of {@link MqttMessageDispatcher} if it is null
     */
    public MqttMessageDispatcherCreator(GwtSession currentSession) {
        mqttMessageDispatcher = new MqttMessageDispatcher(currentSession);
    }

    /**
     * Get the instance of the {@link MqttMessageDispatcher}
     * @return
     */
    public MqttMessageDispatcher getMqttMessageDispatcher() {
        return mqttMessageDispatcher;
    }
}
