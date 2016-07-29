package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableQueryConverter;
import org.eclipse.kapua.service.datastore.model.TopicInfo;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.eclipse.kapua.service.datastore.model.query.TopicInfoQuery;

public class TopicInfoQueryConverter extends AbstractStorableQueryConverter<TopicInfo, TopicInfoQuery>
{

    @Override
    protected String[] getIncludes(MessageFetchStyle fetchStyle)
    {
        return null;
    }

    @Override
    protected String[] getExcludes(MessageFetchStyle fetchStyle)
    {
        return null;
    }

    @Override
    protected String[] getFields()
    {
        return new String[] {EsSchema.TOPIC_SEM_NAME,
                             EsSchema.TOPIC_TIMESTAMP,
                             EsSchema.TOPIC_ASSET,
                             EsSchema.TOPIC_ACCOUNT};
    }

}
