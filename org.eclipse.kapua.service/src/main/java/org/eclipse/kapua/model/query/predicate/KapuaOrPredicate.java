package org.eclipse.kapua.model.query.predicate;

import java.util.List;

public interface KapuaOrPredicate extends KapuaPredicate
{
    public KapuaPredicate or(KapuaPredicate predicate);

    public List<KapuaPredicate> getPredicates();
}
