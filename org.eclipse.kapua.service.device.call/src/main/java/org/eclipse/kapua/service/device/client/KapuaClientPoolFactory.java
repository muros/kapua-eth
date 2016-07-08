package org.eclipse.kapua.service.device.client;

import org.eclipse.kapua.model.KapuaEntityFactory;

public interface KapuaClientPoolFactory<C extends KapuaClient> extends KapuaEntityFactory
{
    public <I extends KapuaClientPoolFactory<KapuaClient>> I newInstance();
}
