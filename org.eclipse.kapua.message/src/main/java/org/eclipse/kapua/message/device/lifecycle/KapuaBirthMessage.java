package org.eclipse.kapua.message.device.lifecycle;

import org.eclipse.kapua.message.KapuaMessage;

public interface KapuaBirthMessage extends KapuaMessage<KapuaBirthChannel, KapuaBirthPayload>
{
	
	public void setClientId(String clientId);
	
	public String getClientId();

}
