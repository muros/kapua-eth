package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaBirthChannel;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;

public class KapuaBirthChannelImpl extends KapuaChannelImpl implements KapuaBirthChannel {

	private String clientId;
	  
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	  
}