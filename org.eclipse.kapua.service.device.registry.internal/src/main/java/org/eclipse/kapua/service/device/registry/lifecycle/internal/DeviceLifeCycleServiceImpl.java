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
package org.eclipse.kapua.service.device.registry.lifecycle.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifeCycleService;

public class DeviceLifeCycleServiceImpl implements DeviceLifeCycleService
{

    @Override
    public void birth(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void death(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void missing(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void applications(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }
}
