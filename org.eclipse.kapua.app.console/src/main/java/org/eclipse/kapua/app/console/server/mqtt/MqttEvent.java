package org.eclipse.kapua.app.console.server.mqtt;

public interface MqttEvent {
    public void publishOccurred(String topic, byte[] payload);
}
