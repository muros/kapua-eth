package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.service.datastore.internal.AbstractStorableQuery;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;

public class MetricInfoQueryImpl extends AbstractStorableQuery<MetricInfo> implements MetricInfoQuery
{
    public void copy(MetricInfoQuery query)
    {
        super.copy(query);
        // Add copy for local members
    }
}
