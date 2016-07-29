package org.eclipse.kapua.service.datastore.model.query;

public interface TermPredicate extends StorablePredicate
{
    public StorableField getField();
    public Object getValue();
    public <V> V getValue(Class<V> clazz);
}
