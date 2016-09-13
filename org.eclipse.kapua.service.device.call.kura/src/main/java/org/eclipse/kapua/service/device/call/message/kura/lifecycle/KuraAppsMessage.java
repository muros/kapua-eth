package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraAppsMessage extends KuraMessage<KuraAppsChannel, KuraAppsPayload>
{

    public KuraAppsMessage()
    {
        super();
    }

    public KuraAppsMessage(KuraAppsChannel channel, 
    		Date timestamp,
            KuraAppsPayload payload) {
    	this.channel = channel;
    	this.timestamp = timestamp;
    	this.payload = payload;
    }
    
}