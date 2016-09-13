package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectPayload;
import org.eclipse.kapua.message.internal.KapuaMessageImpl;

public class KapuaDisconnectMessageImpl extends KapuaMessageImpl<KapuaDisconnectChannel, KapuaDisconnectPayload> implements KapuaDisconnectMessage {
	
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