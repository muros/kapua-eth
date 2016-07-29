package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableListResult;
import org.eclipse.kapua.service.datastore.model.AssetInfo;
import org.eclipse.kapua.service.datastore.model.AssetInfoListResult;

public class AssetInfoListResultImpl extends AbstractStorableListResult<AssetInfo> implements AssetInfoListResult
{
    private static final long serialVersionUID = 3503852185102385667L;

    public AssetInfoListResultImpl()
    {
        super();
    }

    public AssetInfoListResultImpl(Object nextKey) {
        super(nextKey);
    }

    public AssetInfoListResultImpl(Object nextKey, Integer totalCount) {
        super(nextKey, totalCount);
    }
}
