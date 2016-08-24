package org.eclipse.kapua.commons.configuration;

import java.util.Properties;

import org.eclipse.kapua.KapuaException;
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

    public Properties getConfigurations() throws KapuaException;

    public void setConfigurations(Properties configurations) throws KapuaException;
}
