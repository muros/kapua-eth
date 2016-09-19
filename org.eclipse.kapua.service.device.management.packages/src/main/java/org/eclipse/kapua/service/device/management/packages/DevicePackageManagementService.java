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
package org.eclipse.kapua.service.device.management.packages;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface DevicePackageManagementService extends KapuaService
{
    public DevicePackages getInstalled(KapuaId scopeId, KapuaId deviceId, Long timeout)
        throws KapuaException;

    // public void download(KapuaId scopeId, KapuaId deviceId, DevicePackageDownloadRequest deployInstallRequest, Long timeout);
    //
    // public void downloadStatus(KapuaId scopeId, KapuaId deviceId, Long timeout);
    //
    // public void install(KapuaId scopeId, KapuaId deviceId, DevicePackageInstallRequest deployInstallRequest, Long timeout);
    //
    // public void installStatus(KapuaId scopeId, KapuaId deviceId, Long timeout);
    //
    // public void uninstall(KapuaId scopeId, KapuaId deviceId, DevicePackageUninstallRequest deployUninstallRequest, Long timeout)
    // throws KapuaException;
    //
    // public void uninstallStatus(KapuaId scopeId, KapuaId deviceId, Long timeout);
}
