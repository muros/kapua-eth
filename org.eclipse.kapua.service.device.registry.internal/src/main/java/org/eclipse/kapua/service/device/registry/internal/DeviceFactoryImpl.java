package org.eclipse.kapua.service.device.registry.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceQuery;

public class DeviceFactoryImpl implements DeviceFactory
{

    @Override
    public DeviceCreator newCreator(KapuaId scopeId, String clientId)
    {
        return new DeviceCreatorImpl(scopeId);
    }

    @Override
    public DeviceQuery newQuery(KapuaId scopeId)
    {
        return new DeviceQueryImpl(scopeId);
    }

}
