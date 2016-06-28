package org.eclipse.kapua.app.console.shared.service;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtUser;
import org.eclipse.kapua.app.console.shared.model.GwtUserCreator;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("user")
public interface GwtUserService extends RemoteService
{
    /**
     * Creates a new user under the account specified in the UserCreator.
     * 
     * @param gwtUserCreator
     * @return
     * @throws GwtEdcException
     */
    public GwtUser create(GwtXSRFToken xsfrToken, GwtUserCreator gwtUserCreator)
        throws GwtEdcException;

    /**
     * Updates an User in the database and returns the refreshed/reloaded entity instance.
     * 
     * @param gwtUser
     * @return
     * @throws GwtEdcException
     */
    public GwtUser update(GwtXSRFToken xsfrToken, GwtUser gwtUser)
        throws GwtEdcException;

    /**
     * Delete the supplied User.
     * 
     * @param gwtUser
     * @return
     * @throws GwtEdcException
     */
    public void delete(GwtXSRFToken xsfrToken, String accountId, GwtUser gwtUser)
        throws GwtEdcException;

    /**
     * Returns an User by its Id or null if an account with such Id does not exist.
     * 
     * @param userId
     * @return
     * @throws GwtEdcException
     * 
     */
    public GwtUser find(String accountId, String userId)
        throws GwtEdcException;

    /**
     * Returns the list of all User which belong to an account.
     * 
     * @param accountId
     * @return
     * @throws GwtEdcException
     * 
     * @gwt.typeArgs <com.eurotech.cloud.console.shared.model.GwtUser>
     */
    public ListLoadResult<GwtUser> findAll(String scopeIdStirng)
        throws GwtEdcException;

}
