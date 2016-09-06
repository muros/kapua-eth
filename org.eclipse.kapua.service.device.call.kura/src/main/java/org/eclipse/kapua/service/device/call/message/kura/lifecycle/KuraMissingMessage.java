package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraMissingMessage extends KuraMessage<KuraMissingChannel, KuraMissingPayload>
{

    public KuraMissingMessage()
    {
        super();
    }
    
    public KuraMissingMessage(KuraMissingChannel channel, 
    		Date timestamp,
            KuraMissingPayload payload) {
    	this.channel = channel;
    	this.timestamp = timestamp;
    	this.payload = payload;
    }

}