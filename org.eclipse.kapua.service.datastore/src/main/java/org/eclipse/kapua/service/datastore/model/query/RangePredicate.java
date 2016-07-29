package org.eclipse.kapua.service.datastore.model.query;

public interface RangePredicate extends StorablePredicate
{
    public StorableField getField();
    public Object getMinValue();
    public <V extends Comparable<V>> V getMinValue(Class<V> clazz);
    public Object getMaxValue();
    public <V extends Comparable<V>> V getMaxValue(Class<V> clazz);
}
