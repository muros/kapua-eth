package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.model.KapuaUpdatableEntity;

public interface ServiceConfig extends KapuaUpdatableEntity
{
    public static final String TYPE = "scfg";

    default public String getType()
    {
        return TYPE;
    }

    public String getPid();

    public void setPid(String pid);

}
