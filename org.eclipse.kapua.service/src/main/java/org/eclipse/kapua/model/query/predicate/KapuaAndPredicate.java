package org.eclipse.kapua.model.query.predicate;

import java.util.List;

public interface KapuaAndPredicate extends KapuaPredicate
{
    public KapuaAndPredicate and(KapuaPredicate predicate);

    public List<KapuaPredicate> getPredicates();
}
