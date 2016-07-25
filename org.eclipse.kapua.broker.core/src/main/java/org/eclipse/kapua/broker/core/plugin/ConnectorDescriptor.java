package org.eclipse.kapua.broker.core.plugin;

import java.io.Serializable;

public class ConnectorDescriptor implements Serializable {
	
	private static final long serialVersionUID = -7220383679289083726L;
	
	private String connectorName;
	private String deviceProtocolName;
	private String deviceProtocolImplementationClass;
	
	public ConnectorDescriptor(String connectorName, String deviceProtocolName, String deviceProtocolImplementationClass) {
		this.connectorName = connectorName;
		this.deviceProtocolName = deviceProtocolName;
		this.deviceProtocolImplementationClass = deviceProtocolImplementationClass;
	}
	
	public String getConnectorName() {
		return connectorName;
	}
	public String getDeviceProtocolName() {
		return deviceProtocolName;
	}
	
	public String getDeviceProtocolImplementationClass() {
		return deviceProtocolImplementationClass;
	}

}