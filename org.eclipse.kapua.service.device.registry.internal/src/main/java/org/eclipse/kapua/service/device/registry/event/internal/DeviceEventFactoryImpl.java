/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.event.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;

public class DeviceEventFactoryImpl implements DeviceEventFactory
{
    public DeviceEventCreator newCreator(KapuaId scopeId, KapuaId deviceId)
    {
    	DeviceEventCreatorImpl deviceEventCreatorImpl = new DeviceEventCreatorImpl(scopeId);
        deviceEventCreatorImpl.setDeviceId(deviceId);
    	deviceEventCreatorImpl.setAction(KapuaMethod.CREATE);
        return deviceEventCreatorImpl;
    }

    @Override
    public DeviceEventQuery newQuery(KapuaId scopeId)
    {
        return new DeviceEventQueryImpl(scopeId);
    }

}
