package org.eclipse.kapua.app.console.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtUserServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtUserService
     */
    void create( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtUserCreator gwtUserCreator, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtUser> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtUserService
     */
    void update( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, org.eclipse.kapua.app.console.shared.model.GwtUser gwtUser, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtUser> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtUserService
     */
    void delete( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, java.lang.String accountId, org.eclipse.kapua.app.console.shared.model.GwtUser gwtUser, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtUserService
     */
    void find( java.lang.String accountId, java.lang.String userId, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtUser> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtUserService
     */
    void findAll( java.lang.String scopeIdStirng, AsyncCallback<com.extjs.gxt.ui.client.data.ListLoadResult<org.eclipse.kapua.app.console.shared.model.GwtUser>> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtUserServiceAsync instance;

        public static final GwtUserServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtUserServiceAsync) GWT.create( GwtUserService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "user" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
