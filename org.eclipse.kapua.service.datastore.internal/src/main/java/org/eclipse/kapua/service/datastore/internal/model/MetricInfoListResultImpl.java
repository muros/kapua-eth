package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableListResult;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;

public class MetricInfoListResultImpl extends AbstractStorableListResult<MetricInfo> implements MetricInfoListResult
{
    private static final long serialVersionUID = -829492000973519867L;

    public MetricInfoListResultImpl()
    {
        super();
    }

    public MetricInfoListResultImpl(Object nextKey) {
        super(nextKey);
    }

    public MetricInfoListResultImpl(Object nextKey, Integer totalCount) {
        super(nextKey, totalCount);
    }
}