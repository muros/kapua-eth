package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaTopic;
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
}
