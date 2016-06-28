package org.eclipse.kapua.service.device.registry.connection.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionCreator;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;

public class DeviceConnectionFactoryImpl implements DeviceConnectionFactory
{

    @Override
    public DeviceConnectionCreator newCreator(KapuaId scopeId)
    {
        return new DeviceConnectionCreatorImpl(scopeId);
    }

    @Override
    public DeviceConnectionQuery newQuery(KapuaId scopeId)
    {
        return new DeviceConnectionQueryImpl(scopeId);
    }

}
