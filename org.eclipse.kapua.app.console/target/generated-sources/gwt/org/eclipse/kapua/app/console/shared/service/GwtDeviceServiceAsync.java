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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtDeviceServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void getNumOfDevices( long accountId, AsyncCallback<java.lang.Long> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void getNumOfDevicesConnected( java.lang.String accountId, AsyncCallback<java.util.Map<java.lang.String,java.lang.Integer>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDevice( java.lang.String scopeIdString, java.lang.String clientId, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtDevice> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDevices( com.extjs.gxt.ui.client.data.PagingLoadConfig loadConfig, java.lang.String scopeIdString, org.eclipse.kapua.app.console.shared.model.GwtDeviceQueryPredicates predicates, AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<org.eclipse.kapua.app.console.shared.model.GwtDevice>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void createDevice( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsrfToken, org.eclipse.kapua.app.console.shared.model.GwtDeviceCreator gwtDeviceCreator, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtDevice> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void updateAttributes( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice gwtDevice, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtDevice> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void deleteDevice( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, java.lang.String scopeIdString, java.lang.String clientId, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDeviceEvents( com.extjs.gxt.ui.client.data.PagingLoadConfig loadConfig, org.eclipse.kapua.app.console.shared.model.GwtDevice gwtDevice, java.util.Date startDate, java.util.Date endDate, AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<org.eclipse.kapua.app.console.shared.model.GwtDeviceEvent>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDeviceConfigurations( org.eclipse.kapua.app.console.shared.model.GwtDevice device, AsyncCallback<java.util.List<org.eclipse.kapua.app.console.shared.model.GwtConfigComponent>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void updateComponentConfiguration( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsrfToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, org.eclipse.kapua.app.console.shared.model.GwtConfigComponent configComponent, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDeviceDeploymentPackages( org.eclipse.kapua.app.console.shared.model.GwtDevice device, AsyncCallback<java.util.List<org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void uninstallDeploymentPackage( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, java.lang.String packageName, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void executeCommand( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandInput commandInput, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandOutput> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDeviceSnapshots( org.eclipse.kapua.app.console.shared.model.GwtDevice device, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtSnapshot>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void rollbackDeviceSnapshot( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, org.eclipse.kapua.app.console.shared.model.GwtSnapshot snapshot, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findDeviceProfile( java.lang.String scopeIdString, java.lang.String clientId, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void findBundles( org.eclipse.kapua.app.console.shared.model.GwtDevice device, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void startBundle( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair pair, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void stopBundle( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtDevice device, org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair pair, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDeviceService
     */
    void updateDeviceCertificateWithDefault( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, java.lang.String accountName, java.lang.String clientId, long accountId, AsyncCallback<Void> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtDeviceServiceAsync instance;

        public static final GwtDeviceServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtDeviceServiceAsync) GWT.create( GwtDeviceService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "device" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
