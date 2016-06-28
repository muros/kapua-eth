package org.eclipse.kapua.app.console.shared.service;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtUser;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("auth")
public interface GwtAuthorizationService extends RemoteService
{

    public GwtSession login(GwtUser gwtUser)
        throws GwtEdcException;

    /**
     * Return the currently authenticated user or null if no session has been established.
     */
    public GwtSession getCurrentSession()
        throws GwtEdcException;

    /**
     * Checks whether the current Subject is granted the supplied permission;
     * returns true if access is granted, false otherwise.
     * <b>The API does not perform any access control check and it is meant for internal use.</b>
     *
     * @param permission
     * @return
     * @throws GwtEdcException
     */
    public Boolean hasAccess(String gwtPermission)
        throws GwtEdcException;

    public void logout()
        throws GwtEdcException;

}
