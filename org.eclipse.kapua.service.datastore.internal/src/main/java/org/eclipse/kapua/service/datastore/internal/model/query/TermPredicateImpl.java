package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.service.datastore.model.query.StorableField;
import org.eclipse.kapua.service.datastore.model.query.TermPredicate;

public class TermPredicateImpl implements TermPredicate
{
    private StorableField field;
    private Object value;
    
    public TermPredicateImpl()
    {
    }
    
    public <V> TermPredicateImpl(StorableField field, V value)
    {
        this.field = field;
        this.value = value;
    }
    
    @Override
    public StorableField getField()
    {
        return this.field;
    }
    
    public TermPredicate setField(StorableField field)
    {
        this.field = field;
        return this;
    }

    @Override
    public Object getValue()
    {
        return value;
    }

    @Override
    public <V> V getValue(Class<V> clazz)
    {
        return clazz.cast(value);
    }

    public <V> TermPredicate setValue(V value)
    {
        this.value = value;
        return this;
    }
}
