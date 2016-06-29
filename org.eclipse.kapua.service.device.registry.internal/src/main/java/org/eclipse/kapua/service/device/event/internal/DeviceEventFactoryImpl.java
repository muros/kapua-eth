package org.eclipse.kapua.service.device.event.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.event.DeviceEventQuery;

public class DeviceEventFactoryImpl implements DeviceEventFactory
{
    public DeviceEventCreator newCreator(KapuaId scopeId)
    {
        return new DeviceEventCreatorImpl(scopeId);
    }

    @Override
    public DeviceEventQuery newQuery(KapuaId scopeId)
    {
        return new DeviceEventQueryImpl(scopeId);
    }

}
