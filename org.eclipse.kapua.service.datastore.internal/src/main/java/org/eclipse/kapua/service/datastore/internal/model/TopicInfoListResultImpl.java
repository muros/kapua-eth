package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableListResult;
import org.eclipse.kapua.service.datastore.model.TopicInfo;
import org.eclipse.kapua.service.datastore.model.TopicInfoListResult;

public class TopicInfoListResultImpl extends AbstractStorableListResult<TopicInfo> implements TopicInfoListResult
{
    private static final long serialVersionUID = -6150141413325816028L;

    public TopicInfoListResultImpl()
    {
        super();
    }

    public TopicInfoListResultImpl(Object nextKey)
    {
        super(nextKey);
    }

    public TopicInfoListResultImpl(Object nextKey, Integer totalCount)
    {
        super(nextKey, totalCount);
    }
}