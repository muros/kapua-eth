package org.eclipse.kapua.app.console.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtAuthorizationServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAuthorizationService
     */
    void login( org.eclipse.kapua.app.console.shared.model.GwtUser gwtUser, AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtSession> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAuthorizationService
     */
    void getCurrentSession( AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtSession> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAuthorizationService
     */
    void hasAccess( java.lang.String gwtPermission, AsyncCallback<java.lang.Boolean> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtAuthorizationService
     */
    void logout( AsyncCallback<Void> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtAuthorizationServiceAsync instance;

        public static final GwtAuthorizationServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtAuthorizationServiceAsync) GWT.create( GwtAuthorizationService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "auth" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
