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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandInput;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandOutput;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCreator;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceEvent;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.shared.model.GwtSnapshot;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("device")
public interface GwtDeviceService extends RemoteService
{
    /**
     * Returns count of all devices.
     * 
     * @param accountId
     * @return
     * @throws GwtEdcException
     */
    public long getNumOfDevices(long accountId)
        throws GwtEdcException;

    /**
     * Returns count of all devices by connection status.
     * 
     * @param accountId
     * @return
     * @throws GwtEdcException
     */
    public Map<String, Integer> getNumOfDevicesConnected(String accountId)
        throws GwtEdcException;

    /**
     * Finds device given its clientId
     * 
     * @param accountId
     * @param clientId
     * @return
     */
    public GwtDevice findDevice(String scopeIdString, String clientId)
        throws GwtEdcException;

    /**
     * Finds devices in an account with query
     * 
     * @param loadConfig
     * @param accountId
     * @param predicates
     * @return
     */
    public PagingLoadResult<GwtDevice> findDevices(PagingLoadConfig loadConfig, String scopeIdString, GwtDeviceQueryPredicates predicates)
        throws GwtEdcException;

    /**
     * Creates a device entry for the account
     * 
     * @param xsrfToken
     * @param gwtDeviceCreator
     * @return
     * @throws GwtEdcException
     */
    public GwtDevice createDevice(GwtXSRFToken xsrfToken, GwtDeviceCreator gwtDeviceCreator)
        throws GwtEdcException;

    /**
     * Updates a device entity with the given gwtDevice new values for custom attributes, display name and associated tag
     * 
     * @param gwtDevice
     * @return
     * @throws GwtEdcException
     */
    public GwtDevice updateAttributes(GwtXSRFToken xsfrToken, GwtDevice gwtDevice)
        throws GwtEdcException;

    /**
     * Deletes a device
     * 
     * @param accountId
     * @param clientId
     */
    public void deleteDevice(GwtXSRFToken xsfrToken, String scopeIdString, String clientId)
        throws GwtEdcException;

    //
    // Device Management Methods
    //
    /**
     * Returns a list of device history events for a specified device within a specified date range.
     * 
     * @param gwtDevice the device to return the history of
     * @param startDateMillis the start of the date range in milliseconds since epoch (Date.getTime())
     * @param endDateMillis the end of the date range in milliseconds since epoch (Date.getTime())
     * @return
     * @throws GwtEdcException
     */
    public PagingLoadResult<GwtDeviceEvent> findDeviceEvents(PagingLoadConfig loadConfig, GwtDevice gwtDevice, Date startDate, Date endDate)
        throws GwtEdcException;

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

    /**
     * Executes a command on a remote Device.
     * 
     * @param device
     * @param command
     * @return
     */
    public GwtDeviceCommandOutput executeCommand(GwtXSRFToken xsfrToken, GwtDevice device, GwtDeviceCommandInput commandInput)
        throws GwtEdcException;

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

    public ListLoadResult<GwtGroupedNVPair> findDeviceProfile(String scopeIdString, String clientId)
        throws GwtEdcException;

    public ListLoadResult<GwtGroupedNVPair> findBundles(GwtDevice device)
        throws GwtEdcException;

    public void startBundle(GwtXSRFToken xsfrToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException;

    public void stopBundle(GwtXSRFToken xsfrToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException;

    //
    // Update certificate device
    //
    public void updateDeviceCertificateWithDefault(GwtXSRFToken xsfrToken, String accountName,
                                                   String clientId, long accountId)
        throws GwtEdcException;

}
