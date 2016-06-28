package org.eclipse.kapua.service.device.registry.connection;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceConnectionFactory extends KapuaEntityFactory
{
    public DeviceConnectionCreator newCreator(KapuaId scopeId);

    public DeviceConnectionQuery newQuery(KapuaId scopeId);
}
