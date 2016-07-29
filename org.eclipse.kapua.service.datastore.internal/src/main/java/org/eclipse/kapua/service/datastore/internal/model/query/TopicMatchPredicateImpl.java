package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.service.datastore.model.query.TopicMatchPredicate;

public class TopicMatchPredicateImpl implements TopicMatchPredicate
{
    private String expression;
    
    public TopicMatchPredicateImpl()
    {
    }
    
    public TopicMatchPredicateImpl(String expression)
    {
        this.expression = expression;
    }

    @Override
    public String getExpression()
    {
        return this.expression;
    }

    public TopicMatchPredicate setExpression(String expression)
    {
        this.expression = expression;
        return this;
    }
}
