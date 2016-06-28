package org.eclipse.kapua.app.console.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtAccountServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void create( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtAccountCreator gwtAccountCreator, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtAccount> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void find( java.lang.String accountId, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtAccount> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void findByAccountName( java.lang.String accountName, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtAccount> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void getAccountInfo( java.lang.String gwtAccountId, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void updateAccountProperties( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtAccount gwtAccount, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtAccount> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void update( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtAccount gwtAccount, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtAccount> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void delete( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtAccount gwtAccount, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void findAll( java.lang.String scopeId, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtAccount>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void findChildren( java.lang.String accountId, boolean recoursive, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtAccount>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAccountService
     */
    void findChildrenAsStrings( java.lang.String scopeId, boolean recoursive, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtAccountStringListItem>> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtAccountServiceAsync instance;

        public static final GwtAccountServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtAccountServiceAsync) GWT.create( GwtAccountService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "account" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
