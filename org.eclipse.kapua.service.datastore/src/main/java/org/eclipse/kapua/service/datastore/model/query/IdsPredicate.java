package org.eclipse.kapua.service.datastore.model.query;

import java.util.Set;

import org.eclipse.kapua.service.datastore.model.StorableId;

public interface IdsPredicate extends StorablePredicate
{
    public StorableField getField();
    public Set<StorableId> getIdSet();
}
