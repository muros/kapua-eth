package org.eclipse.kapua.service.device.event.internal;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.event.DeviceEvent;
import org.eclipse.kapua.service.device.event.DeviceEventQuery;

public class DeviceEventQueryImpl extends AbstractKapuaQuery<DeviceEvent> implements DeviceEventQuery
{
    private DeviceEventQueryImpl()
    {
        super();
    }

    public DeviceEventQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }
}
