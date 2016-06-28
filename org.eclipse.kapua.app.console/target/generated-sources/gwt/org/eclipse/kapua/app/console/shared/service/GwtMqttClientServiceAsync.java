package org.eclipse.kapua.app.console.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface GwtMqttClientServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtMqttClientService
     */
    void subscribe( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, java.lang.String brokerAddress, java.lang.String clientId, java.lang.String[] topics, AsyncCallback<Void> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.eclipse.kapua.app.console.shared.service.GwtMqttClientService
     */
    void unsubscribe( org.eclipse.kapua.app.console.shared.model.GwtXSRFToken xsfrToken, java.lang.String brokerAddress, java.lang.String clientId, java.lang.String[] topics, AsyncCallback<Void> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static GwtMqttClientServiceAsync instance;

        public static final GwtMqttClientServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (GwtMqttClientServiceAsync) GWT.create( GwtMqttClientService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "mqtt" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
