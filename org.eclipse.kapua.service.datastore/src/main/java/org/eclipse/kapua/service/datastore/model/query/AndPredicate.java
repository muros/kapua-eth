package org.eclipse.kapua.service.datastore.model.query;

import java.util.List;

public interface AndPredicate extends StorablePredicate
{    
    public List<StorablePredicate> getPredicates();
}
