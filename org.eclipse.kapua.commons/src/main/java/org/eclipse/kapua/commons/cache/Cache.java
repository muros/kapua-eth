package org.eclipse.kapua.commons.cache;

public interface Cache<K, V>
{
    public String getNamespace();

    public void setNamespace(String namespace);

    public V get(K k);

    public void put(K k, V v);

    public void remove(K k);
}
