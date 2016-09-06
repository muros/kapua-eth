package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaBirthChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaBirthPayload;
import org.eclipse.kapua.message.internal.KapuaMessageImpl;

public class KapuaBirthMessageImpl extends KapuaMessageImpl<KapuaBirthChannel, KapuaBirthPayload> implements KapuaBirthMessage {
	
	private String clientId;

	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

}
