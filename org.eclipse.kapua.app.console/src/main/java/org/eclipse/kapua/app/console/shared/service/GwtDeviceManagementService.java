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
package org.eclipse.kapua.app.console.shared.service;

import java.util.List;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandInput;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandOutput;
import org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.shared.model.GwtSnapshot;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("deviceManagement")
public interface GwtDeviceManagementService extends RemoteService
{
    //
    // Packages
    //
    /**
     * Returns the deployment packages installed on a Device.
     * 
     * @param device
     * @return
     */
    public List<GwtDeploymentPackage> findDeviceDeploymentPackages(GwtDevice device)
        throws GwtEdcException;

    /**
     * Uninstalls a deployment package from a device.
     * 
     * @param device
     * @param packageName
     * @throws GwtEdcException
     */
    public void uninstallDeploymentPackage(GwtXSRFToken xsfrToken, GwtDevice device, String packageName)
        throws GwtEdcException;

    //
    // Configurations
    //
    /**
     * Returns the configuration of a Device as the list of all the configurable components.
     * 
     * @param device
     * @return
     */
    public List<GwtConfigComponent> findDeviceConfigurations(GwtDevice device)
        throws GwtEdcException;

    /**
     * Updates the configuration of the provided component.
     * 
     * @param device
     * @param configComponent
     */
    public void updateComponentConfiguration(GwtXSRFToken xsrfToken, GwtDevice device, GwtConfigComponent configComponent)
        throws GwtEdcException;

    //
    // Snapshots
    //
    /**
     * 
     * @param device
     * @return
     * @throws GwtEdcException
     */
    public ListLoadResult<GwtSnapshot> findDeviceSnapshots(GwtDevice device)
        throws GwtEdcException;

    /**
     * 
     * @param device
     * @param snapshot
     * @throws GwtEdcException
     */
    public void rollbackDeviceSnapshot(GwtXSRFToken xsfrToken, GwtDevice device, GwtSnapshot snapshot)
        throws GwtEdcException;

    //
    // Bundles
    //
    public ListLoadResult<GwtGroupedNVPair> findBundles(GwtDevice device)
        throws GwtEdcException;

    public void startBundle(GwtXSRFToken xsfrToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException;

    public void stopBundle(GwtXSRFToken xsfrToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException;

    //
    // Commands
    //
    /**
     * Executes a command on a remote Device.
     * 
     * @param device
     * @param command
     * @return
     */
    public GwtDeviceCommandOutput executeCommand(GwtXSRFToken xsfrToken, GwtDevice device, GwtDeviceCommandInput commandInput)
        throws GwtEdcException;

}
