package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.Payload;
import org.eclipse.kapua.service.datastore.model.Position;
import org.eclipse.kapua.service.datastore.model.Topic;

public class MessageConverter
{
    public static KapuaTopic toKapuaTopic(Topic aTopic) throws KapuaInvalidTopicException
    {
        if (aTopic == null)
            return null;
        
        KapuaTopic kaupaTopic = new KapuaTopic(aTopic.getTopicName());
        return kaupaTopic;
    }
    
    public static KapuaPosition toKapuaPosition(Position position)
    {
        if (position == null)
            return null;
        
        KapuaPosition kapuaPosition = new KapuaPosition();
        kapuaPosition.setAltitude(position.getAltitude());
        kapuaPosition.setHeading(position.getHeading());
        kapuaPosition.setLatitude(position.getLatitude());
        kapuaPosition.setLongitude(position.getLongitude());
        kapuaPosition.setPrecision(position.getPrecision());
        kapuaPosition.setSatellites(position.getSatellites());
        kapuaPosition.setSpeed(position.getSpeed());
        kapuaPosition.setStatus(position.getStatus());
        kapuaPosition.setTimestamp(position.getTimestamp());
        return kapuaPosition;
    }
    
    public static KapuaPayload toKapuaPayload(Payload payload)
    {
        KapuaPayload kapauPayload = null;
        if (payload == null)
            return null;

        kapauPayload = new KapuaPayload();
        kapauPayload.setBody(payload.getBody());
        
        if (payload.getMetrics() != null)
        {
            Map<String, Object> map = payload.getMetrics();
            Iterator<String> i = map.keySet().iterator();
            while(i.hasNext())
            {
                String metric = i.next();
                kapauPayload.addMetric(metric, map.get(metric));
            }
        }
        
        kapauPayload.setPosition(MessageConverter.toKapuaPosition(payload.getPosition()));
        kapauPayload.setTimestamp(payload.getCollectedOn());
        
        return kapauPayload;
    }
    
    public static KapuaMessage toKapuaMessage(Message message) throws KapuaInvalidTopicException 
    {
        if (message == null)
            return null;
        
        String idStr = message.getId() == null ? null : message.getId().toString();
        Date timestamp = message.getTimestamp();
        KapuaTopic kapuaTopic = MessageConverter.toKapuaTopic(message.getTopic());
        KapuaPayload payload = MessageConverter.toKapuaPayload(message.getPayload());
        
        return new KapuaMessage(kapuaTopic, timestamp, idStr, payload);
    }
    
    public static KapuaMessage toKapuaMessage(MessageCreator messageCreator) throws KapuaInvalidTopicException 
    {
        if (messageCreator == null)
            return null;
        
        Date timestamp = messageCreator.getTimestamp();
        KapuaTopic kapuaTopic = MessageConverter.toKapuaTopic(messageCreator.getTopic());
        KapuaPayload payload = MessageConverter.toKapuaPayload(messageCreator.getPayload());
        return new KapuaMessage(kapuaTopic, timestamp, null, payload);
    }
}
