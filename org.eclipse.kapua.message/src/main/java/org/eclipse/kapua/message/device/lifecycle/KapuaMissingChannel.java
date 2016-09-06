package org.eclipse.kapua.message.device.lifecycle;

import org.eclipse.kapua.message.KapuaChannel;

public interface KapuaMissingChannel extends KapuaChannel
{
	
	public String getClientId();

    public void setClientId(String clientId);

}
