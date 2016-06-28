package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;


public class GwtDownloadProtocol extends EdcBaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2477264640452966413L;

	
	public GwtDownloadProtocol() {		
	}	
	
	public GwtDownloadProtocol(String protocol, String protocolName) {
		setProtocol(protocol);
		setProtocolName(protocolName);
	}

	public String getProtocol() {
		return get("protocol");
	}

	public void setProtocol(String protocol) {
		set("protocol", protocol);
	}

	public String getProtocolName() {
		return get("protocolName");
	}

	public void setProtocolName(String protocolName) {
		set("protocolName", protocolName);
	}
	
}
