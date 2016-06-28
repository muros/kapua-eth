package org.eclipse.kapua.app.console.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtSecurityTokenServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtSecurityTokenService
     */
    void generateSecurityToken( AsyncCallback<org.eclipse.kapua.app.console.shared.model.GwtXSRFToken> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtSecurityTokenServiceAsync instance;

        public static final GwtSecurityTokenServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtSecurityTokenServiceAsync) GWT.create( GwtSecurityTokenService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "xsrf" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
