package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.service.datastore.internal.AbstractStorableQuery;
import org.eclipse.kapua.service.datastore.model.AssetInfo;
import org.eclipse.kapua.service.datastore.model.query.AssetInfoQuery;

public class AssetInfoQueryImpl extends AbstractStorableQuery<AssetInfo> implements AssetInfoQuery
{
    public void copy(AssetInfoQuery query)
    {
        super.copy(query);
        // Add copy for local members
    }
}
