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
package org.org.eclipse.kapua.service.device.management.bundle;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceBundleManagementService extends KapuaService
{
    public DeviceBundleListResult get(KapuaId scopeId, KapuaId deviceId)
        throws KapuaException;

    public void start(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException;

    public void stop(KapuaId scopeId, KapuaId deviceId, String bundleId)
        throws KapuaException;
}
