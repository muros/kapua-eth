package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import java.util.Date;

import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;

public class KuraBirthMessage extends KuraMessage<KuraBirthChannel, KuraBirthPayload>
{

    public KuraBirthMessage()
    {
        super();
    }
    
    public KuraBirthMessage(KuraBirthChannel channel, 
    		Date timestamp,
            KuraBirthPayload payload) {
    	this.channel = channel;
    	this.timestamp = timestamp;
    	this.payload = payload;
    }

}