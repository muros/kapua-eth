package org.eclipse.kapua.model.query.predicate;

public interface KapuaAttributePredicate<T> extends KapuaPredicate
{
    public enum Operator
    {
        EQUAL,
        NOT_EQUAL,
        IS_NULL,
        NOT_NULL,
        STARTS_WITH,
        LIKE;
    }

    public String getAttributeName();

    public T getAttributeValue();

    public Operator getOperator();
}
