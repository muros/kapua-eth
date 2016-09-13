package org.eclipse.kapua.broker.core.plugin;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor.MESSAGE_TYPE;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorDescriptorLoader {
	
	private static Logger logger = LoggerFactory.getLogger(ConnectorDescriptorLoader.class);
	
	private final static String CONNECTOR_DESCRIPTOR_RESOURCE = "connectors.properties";
	
	public static Map<String, ConnectorDescriptor> loadConnectorDescriptors() {
		Map<String, ConnectorDescriptor> connectorsDescriptorMap = new HashMap<String, ConnectorDescriptor>();
		URL configurationUrl = ConnectorDescriptorLoader.class.getClassLoader().getResource(CONNECTOR_DESCRIPTOR_RESOURCE);
		//TODO load parameters from the file
		
		Map<MESSAGE_TYPE, Class<DeviceMessage<?, ?>>> deviceClass = new HashMap<MESSAGE_TYPE, Class<DeviceMessage<?, ?>>>();
		deviceClass.put(MESSAGE_TYPE.app, getDeviceClazz("org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraAppsMessage"));
		deviceClass.put(MESSAGE_TYPE.birth, getDeviceClazz("org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraBirthMessage"));
		deviceClass.put(MESSAGE_TYPE.disconnect, getDeviceClazz("org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraDisconnectMessage"));
		deviceClass.put(MESSAGE_TYPE.missing, getDeviceClazz("org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingMessage"));
		deviceClass.put(MESSAGE_TYPE.data, getDeviceClazz("org.eclipse.kapua.service.device.call.message.kura.data.KuraDataMessage"));
		
		Map<MESSAGE_TYPE, Class<KapuaMessage<?, ?>>> kapuaClass = new HashMap<MESSAGE_TYPE, Class<KapuaMessage<?, ?>>>();
		kapuaClass.put(MESSAGE_TYPE.app, getKapuaClazz("org.eclipse.kapua.message.device.lifecycle.KapuaAppsMessage"));
		kapuaClass.put(MESSAGE_TYPE.birth, getKapuaClazz("org.eclipse.kapua.message.device.lifecycle.KapuaBirthMessage"));
		kapuaClass.put(MESSAGE_TYPE.disconnect,  getKapuaClazz("org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage"));
		kapuaClass.put(MESSAGE_TYPE.missing, getKapuaClazz("org.eclipse.kapua.message.device.lifecycle.KapuaMissingMessage"));
		kapuaClass.put(MESSAGE_TYPE.data, getKapuaClazz("org.eclipse.kapua.message.device.data.KapuaDataMessage"));
		
		connectorsDescriptorMap.put("mqtt", new ConnectorDescriptor("mqtt", "kura", deviceClass, kapuaClass));
		connectorsDescriptorMap.put("mqtts", new ConnectorDescriptor("mqtts", "kura", deviceClass, kapuaClass));
        return connectorsDescriptorMap;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<KapuaMessage<?, ?>> getKapuaClazz(String clazz) {
		try {
			return (Class<KapuaMessage<?, ?>>)Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			//TODO throw runtime exception!
			logger.error("Cannot instantiate class {}", e.getMessage(), e);
		}
		//TODO to remove once runtime exception will be thrown
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<DeviceMessage<?, ?>> getDeviceClazz(String clazz) {
		try {
			return (Class<DeviceMessage<?, ?>>)Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			//TODO throw runtime exception!
			logger.error("Cannot instantiate class {}", e.getMessage(), e);
		}
		//TODO to remove once runtime exception will be thrown
		return null;
	}

}