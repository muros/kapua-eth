package org.eclipse.kapua.service.device.registry.connection.internal;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;

public class DeviceConnectionQueryImpl extends AbstractKapuaQuery<DeviceConnection> implements DeviceConnectionQuery
{
    private DeviceConnectionQueryImpl()
    {
        super();
    }

    public DeviceConnectionQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }
}
