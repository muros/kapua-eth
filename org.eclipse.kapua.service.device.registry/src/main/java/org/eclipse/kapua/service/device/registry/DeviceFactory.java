package org.eclipse.kapua.service.device.registry;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceFactory extends KapuaEntityFactory
{
    public DeviceCreator newCreator(KapuaId scopeId, String clientId);

    public DeviceQuery newQuery(KapuaId scopeId);
}
