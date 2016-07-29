package org.eclipse.kapua.service.datastore.internal.model.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.kapua.service.datastore.model.query.AndPredicate;
import org.eclipse.kapua.service.datastore.model.query.StorablePredicate;

public class AndPredicateImpl implements AndPredicate
{
    private List<StorablePredicate> predicates = new ArrayList<StorablePredicate>();

    public AndPredicateImpl()
    {
    }
    
    public AndPredicateImpl(Collection<StorablePredicate> predicates)
    {
        predicates.addAll(predicates);
    }
    
    @Override
    public List<StorablePredicate> getPredicates()
    {
        return this.predicates;
    }
   
    public AndPredicate addPredicate(StorablePredicate predicate)
    {
        this.predicates.add(predicate);
        return this;

    }
    
    public AndPredicate clearPredicates()
    {
        this.predicates.clear();
        return this;
    }

}
