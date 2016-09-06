package org.eclipse.kapua.broker.core.plugin;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;

/**
 * Describes the protocol in terms of message type implementations. It's needed by the broker to translate the specific device protocol message domain to the kapua message domain.
 * Each broker connector can be bound with only one device level protocol.
 *
 */
public class ConnectorDescriptor implements Serializable {
	
	private static final long serialVersionUID = -7220383679289083726L;
	
	public enum MESSAGE_TYPE {
		app,
		birth,
		disconnect,
		missing,
		data
	}
	
	private String connectorName;
	private String deviceProtocolName;

	private final Map<MESSAGE_TYPE, Class<DeviceMessage<?, ?>>> deviceClass;
	private final Map<MESSAGE_TYPE, Class<KapuaMessage<?, ?>>> kapuaClass;
	
	public ConnectorDescriptor(String connectorName, String deviceProtocolName, Map<MESSAGE_TYPE, Class<DeviceMessage<?, ?>>> deviceClass, Map<MESSAGE_TYPE, Class<KapuaMessage<?, ?>>> kapuaClass) {
		this.connectorName = connectorName;
		this.deviceProtocolName = deviceProtocolName;
		this.deviceClass = deviceClass;
		this.kapuaClass = kapuaClass;
	}
	
	public String getConnectorName() {
		return connectorName;
	}
	public String getDeviceProtocolName() {
		return deviceProtocolName;
	}
	
	public Class<DeviceMessage<?, ?>> getDeviceClass(MESSAGE_TYPE messageType) throws KapuaException {
		return deviceClass.get(messageType);
	}
	
	public Class<KapuaMessage<?, ?>> getKapuaClass(MESSAGE_TYPE messageType) throws KapuaException {
		return kapuaClass.get(messageType);
	}
	
}