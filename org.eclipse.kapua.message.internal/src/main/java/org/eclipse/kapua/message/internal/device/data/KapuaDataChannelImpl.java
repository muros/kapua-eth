package org.eclipse.kapua.message.internal.device.data;

import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;

public class KapuaDataChannelImpl extends KapuaChannelImpl implements KapuaDataChannel {
	
	private String clientId;
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
}