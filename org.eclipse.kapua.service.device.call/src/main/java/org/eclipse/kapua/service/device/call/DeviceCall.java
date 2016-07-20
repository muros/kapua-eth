package org.eclipse.kapua.service.device.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.DeviceMessage;

@SuppressWarnings("rawtypes")
public interface DeviceCall<M extends DeviceMessage>
{
    public M create()
        throws KapuaException;

    public M read()
        throws KapuaException;

    public M discover()
        throws KapuaException;

    public M delete()
        throws KapuaException;

    public M execute()
        throws KapuaException;

    public M write()
        throws KapuaException;
}
