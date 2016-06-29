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
package org.org.eclipse.kapua.service.device.management.deploy;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DeviceDeployManagementService extends KapuaService
{
    public DeviceDeploymentPackageListResult get(KapuaId scopeId, KapuaId deviceId)
        throws KapuaException;

    public void install(KapuaId scopeId, KapuaId deviceId, String name, byte[] deviceDeploymentPackage)
        throws KapuaException;

    public void install(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageUrl)
        throws KapuaException;

    public void uninstall(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageId)
        throws KapuaException;
}
