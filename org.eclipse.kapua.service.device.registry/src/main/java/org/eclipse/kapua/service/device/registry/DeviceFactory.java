/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.registry;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface DeviceFactory extends KapuaObjectFactory
{
    public DeviceCreator newCreator(KapuaId scopeId, String clientId);

    public Device newDevice();
    
    public DeviceQuery newQuery(KapuaId scopeId);
    
    public DeviceListResult newDeviceListResult();
}
