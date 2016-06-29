package org.eclipse.kapua.service.device.event;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceEventFactory extends KapuaEntityFactory
{
    public DeviceEventCreator newCreator(KapuaId scopeId);

    public DeviceEventQuery newQuery(KapuaId scopeId);
}
