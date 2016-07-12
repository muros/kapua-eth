package org.eclipse.kapua.service.device.registry.event;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceEventFactory extends KapuaObjectFactory
{
    public DeviceEventCreator newCreator(KapuaId scopeId);

    public DeviceEventQuery newQuery(KapuaId scopeId);
}
