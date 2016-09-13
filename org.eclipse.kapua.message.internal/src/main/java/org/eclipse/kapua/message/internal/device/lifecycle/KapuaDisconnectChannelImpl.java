package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectChannel;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;

public class KapuaDisconnectChannelImpl extends KapuaChannelImpl implements KapuaDisconnectChannel {

	private String clientId;
	  
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	  
}