package org.eclipse.kapua.service.device.registry.event.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;

public class DeviceEventFactoryImpl implements DeviceEventFactory
{
    public DeviceEventCreator newCreator(KapuaId scopeId)
    {
    	DeviceEventCreatorImpl deviceEventCreatorImpl = new DeviceEventCreatorImpl(scopeId);
    	deviceEventCreatorImpl.setAction(KapuaMethod.CREATE);
        return deviceEventCreatorImpl;
    }

    @Override
    public DeviceEventQuery newQuery(KapuaId scopeId)
    {
        return new DeviceEventQueryImpl(scopeId);
    }

}
