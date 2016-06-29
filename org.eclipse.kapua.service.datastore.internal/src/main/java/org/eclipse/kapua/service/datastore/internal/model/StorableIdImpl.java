package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.model.StorableId;

public class StorableIdImpl implements StorableId
{
    private String uuid;
    
    public StorableIdImpl(String id) 
    {
        this.uuid = id;
    }

    @Override
    public String toString()
    {
        return uuid.toString();
    }
}
