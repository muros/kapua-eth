package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableListResult;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageListResult;

public class MessageListResultImpl extends AbstractStorableListResult<Message> implements MessageListResult
{
    private static final long serialVersionUID = -3862584760563199758L;

    public MessageListResultImpl()
    {
        super();
    }

    public MessageListResultImpl(Object nextKey) {
        super(nextKey);
    }

    public MessageListResultImpl(Object nextKey, Integer totalCount) {
        super(nextKey, totalCount);
    }
}
