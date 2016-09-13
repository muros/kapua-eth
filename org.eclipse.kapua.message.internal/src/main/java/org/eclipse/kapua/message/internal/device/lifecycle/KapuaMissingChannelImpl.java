package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaMissingChannel;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;

public class KapuaMissingChannelImpl extends KapuaChannelImpl implements KapuaMissingChannel {

	private String clientId;
	  
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	  
}