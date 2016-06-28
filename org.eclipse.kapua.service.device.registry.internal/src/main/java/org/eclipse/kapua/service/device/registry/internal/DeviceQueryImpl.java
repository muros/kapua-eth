package org.eclipse.kapua.service.device.registry.internal;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceQuery;

public class DeviceQueryImpl extends AbstractKapuaQuery<Device> implements DeviceQuery
{
    private DeviceQueryImpl()
    {
        super();
    }

    public DeviceQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }
}
