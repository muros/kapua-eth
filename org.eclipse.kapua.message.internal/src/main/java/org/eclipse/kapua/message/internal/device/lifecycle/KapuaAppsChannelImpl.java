package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaAppsChannel;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;

public class KapuaAppsChannelImpl extends KapuaChannelImpl implements KapuaAppsChannel {

	private String clientId;
	  
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	  
}