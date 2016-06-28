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

public interface GwtDataServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findTopicsTree( java.lang.String accountName, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtTopic> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findTopicsList( java.lang.String accountName, AsyncCallback<java.util.List<org.eclipse.kapua.app.console.shared.model.GwtTopic>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findAssets( com.extjs.gxt.ui.client.data.LoadConfig config, java.lang.String accountName, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtAsset>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findHeaders( com.extjs.gxt.ui.client.data.LoadConfig config, java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtTopic topic, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtHeader>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findNumberHeaders( com.extjs.gxt.ui.client.data.LoadConfig config, java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtTopic topic, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtHeader>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findHeaders( com.extjs.gxt.ui.client.data.LoadConfig config, java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtAsset asset, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtHeader>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findMessagesByTopic( org.eclipse.kapua.app.console.shared.model.EdcPagingLoadConfig loadConfig, java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtTopic topic, java.util.List<org.eclipse.kapua.app.console.shared.model.GwtHeader> headers, java.util.Date startDate, java.util.Date endDate, AsyncCallback<org.eclipse.kapua.app.console.shared.model.EdcPagingLoadResult<org.eclipse.kapua.app.console.shared.model.GwtMessage>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findLastMessageByTopic( java.lang.String accountName, int limit, AsyncCallback<java.util.List<org.eclipse.kapua.app.console.shared.model.GwtMessage>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findMessagesByTopic( java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtTopic topic, java.util.List<org.eclipse.kapua.app.console.shared.model.GwtHeader> metrics, java.util.Date startDate, java.util.Date endDate, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findMessagesByTopic( java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtTopic topic, java.util.List<org.eclipse.kapua.app.console.shared.model.GwtHeader> headers, java.util.Date startDate, java.util.Date endDate, java.util.Stack<org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor> cursors, int limit, int lastOffset, java.lang.Integer indexOffset, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findMessagesByAsset( org.eclipse.kapua.app.console.shared.model.EdcPagingLoadConfig loadConfig, java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtAsset asset, java.util.List<org.eclipse.kapua.app.console.shared.model.GwtHeader> headers, java.util.Date startDate, java.util.Date endDate, AsyncCallback<org.eclipse.kapua.app.console.shared.model.EdcPagingLoadResult<org.eclipse.kapua.app.console.shared.model.GwtMessage>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtDataService
     */
    void findMessagesByAsset( java.lang.String accountName, org.eclipse.kapua.app.console.shared.model.GwtAsset asset, java.util.List<org.eclipse.kapua.app.console.shared.model.GwtHeader> headers, java.util.Date startDate, java.util.Date endDate, java.util.Stack<org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor> cursors, int limit, int lastOffset, java.lang.Integer indexOffset, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtDataServiceAsync instance;

        public static final GwtDataServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtDataServiceAsync) GWT.create( GwtDataService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "data" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
