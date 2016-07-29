package org.eclipse.kapua.service.datastore.model;

import java.util.Date;

public interface AssetInfoCreator extends StorableCreator<AssetInfo>
{ 
    public String getAsset();
    
    public String getScope();

    public StorableId getLastMessageId();

    public Date getLastMessageTimestamp();
}
