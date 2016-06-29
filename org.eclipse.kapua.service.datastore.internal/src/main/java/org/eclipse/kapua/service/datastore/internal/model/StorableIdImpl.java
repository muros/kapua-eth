package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.service.datastore.model.StorableId;

public class StorableIdImpl implements StorableId
{
    private String sid;
    
    public StorableIdImpl(String id) 
    {
        this.sid = id;
    }

    @Override
    public String toString()
    {
        return sid.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StorableIdImpl other = (StorableIdImpl) obj;
        if (sid == null) {
            if (other.sid != null)
                return false;
        }
        else if (!sid.equals(other.sid))
            return false;

        return true;
    }
}
