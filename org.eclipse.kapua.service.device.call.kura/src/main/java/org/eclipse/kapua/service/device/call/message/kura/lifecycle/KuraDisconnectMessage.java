package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraDisconnectMessage extends KuraMessage<KuraDisconnectChannel, KuraDisconnectPayload>
{

    public KuraDisconnectMessage()
    {
        super();
    }
    
    public KuraDisconnectMessage(KuraDisconnectChannel channel, 
    		Date timestamp,
            KuraDisconnectPayload payload) {
    	this.channel = channel;
    	this.timestamp = timestamp;
    	this.payload = payload;
    }

}