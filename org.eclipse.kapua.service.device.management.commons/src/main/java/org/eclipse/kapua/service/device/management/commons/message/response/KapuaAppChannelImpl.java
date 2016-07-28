package org.eclipse.kapua.service.device.management.commons.message.response;

import java.util.List;

import org.eclipse.kapua.service.device.management.KapuaAppChannel;
import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public abstract class KapuaAppChannelImpl implements KapuaAppChannel
{

	private List<String> semanticParts;
	private KapuaAppProperties appName;
	private KapuaAppProperties appVersion;
	
    @Override
    public List<String> getSemanticParts()
    {
        return semanticParts;
    }

    @Override
    public void setSemanticParts(List<String> semanticParts) 
    {
        this.semanticParts = semanticParts;
    }

    @Override
    public KapuaAppProperties getAppName()
    {
        return appName;
    }

    @Override
    public void setAppName(KapuaAppProperties appName) //do I have to keep it as a KapuaAppProperties?
    {
        this.appName = appName;
    }

    @Override
    public KapuaAppProperties getVersion()
    {
        return appVersion;
    }

    @Override
    public void setVersion(KapuaAppProperties appVersion)
    {
        this.appVersion = appVersion;
    }

}
