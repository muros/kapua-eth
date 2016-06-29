package org.eclipse.kapua.broker.core.message;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;

public class CamelKapuaMessage {
	
	private KapuaMessage message;
	private KapuaId connectionId;
	
	public CamelKapuaMessage(KapuaMessage message, KapuaId connectionId) {
		this.connectionId = connectionId;
		this.message = message;
	}

	public KapuaMessage getMessage() {
		return message;
	}

	public void setMessage(KapuaMessage message) {
		this.message = message;
	}

	public KapuaId getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(KapuaId connectionId) {
		this.connectionId = connectionId;
	}

}