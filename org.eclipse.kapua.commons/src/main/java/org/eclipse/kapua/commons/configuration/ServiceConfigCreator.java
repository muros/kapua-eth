package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;

public interface ServiceConfigCreator extends KapuaUpdatableEntityCreator<ServiceConfig>
{
    public String getPid();

    public void setPid(String pid);
}
