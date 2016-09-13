package org.eclipse.kapua.message.internal.device.lifecycle;

import org.eclipse.kapua.message.device.lifecycle.KapuaDisconnectPayload;
import org.eclipse.kapua.message.internal.KapuaPayloadImpl;

public class KapuaDisconnectPayloadImpl extends KapuaPayloadImpl implements KapuaDisconnectPayload {
	
	private String uptime;
    private String displayName;

    public KapuaDisconnectPayloadImpl(String uptime,
    		String displayName) {
    	this.uptime = uptime;
    	this.displayName = displayName;
    }
    
    public String toDisplayString()
    {
        return new StringBuilder().append("[ getUptime()=").append(getUptime())
                                  .append(", getDisplayName()=").append(getDisplayName())
                                  .append("]")
                                  .toString();
    }

	public String getUptime() {
		return uptime;
	}

	public String getDisplayName() {
		return displayName;
	}

}