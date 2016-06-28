package org.eclipse.kapua.app.console.shared.service;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.shared.model.GwtAccountCreator;
import org.eclipse.kapua.app.console.shared.model.GwtAccountStringListItem;
import org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("account")
public interface GwtAccountService extends RemoteService
{

    /**
     * Creates a new Account based on the information provided in the supplied GwtAccountCreator. Each new account results in the creation of an Organization, a set of Users, and a provisioning step
     * to seed users into the MQTT broker instance.
     * 
     * @param gwtAccountCreator
     * @return
     * @throws GwtEdcDuplicateNameException
     *             if the operation could not be completed because a name conflict was detected
     * @throws GwtEdcException
     */
    public GwtAccount create(GwtXSRFToken xsfrToken, GwtAccountCreator gwtAccountCreator)
        throws GwtEdcException;

    /**
     * Returns a GwtAccount by its Id or null if an account with such Id does not exist.
     * 
     * @param id
     * @return
     */
    public GwtAccount find(String accountId)
        throws GwtEdcException;

    /**
     * Returns a GwtAccount by its acountName or null if an account with such acountName does not exist.
     *
     * @param acountName
     * @return GwtAccount
     */
    public GwtAccount findByAccountName(String accountName)
        throws GwtEdcException;

    /**
     * Get account info ad name values pairs
     * 
     * @param gwtAccount
     * @return
     * @throws KapuaException
     */
    public ListLoadResult<GwtGroupedNVPair> getAccountInfo(String gwtAccountId)
        throws GwtEdcException;

    /**
     * Updates GwtAccount PROPERTIES ONLY in the database and returns the refreshed/reloaded entity instance.
     * 
     * @param gwtAccount
     * @return
     * @throws KapuaException
     */
    public GwtAccount updateAccountProperties(GwtXSRFToken xsfrToken, GwtAccount gwtAccount)
        throws GwtEdcException;

    /**
     * Updates a GwtAccount in the database and returns the refreshed/reloaded entity instance.
     * 
     * @param gwtAccount
     * @return
     * @throws KapuaException
     */
    public GwtAccount update(GwtXSRFToken xsfrToken, GwtAccount gwtAccount)
        throws GwtEdcException;

    /**
     * Deletes the supplied GwtAccount.
     * 
     * @param gwtAccount
     * @throws GwtEdcException
     */
    public void delete(GwtXSRFToken xsfrToken, GwtAccount gwtAccount)
        throws GwtEdcException;

    /**
     * Lists GwtAccounts.
     * 
     * FIXME: Add query predicates, ordering and pagination.
     * 
     * @throws GwtEdcException
     */
    public ListLoadResult<GwtAccount> findAll(String scopeId)
        throws GwtEdcException;

    /**
     * Lists GwtAccounts child of the given accountId.
     * 
     * @param accountId The account id for which to find children
     * @param recoursive If true it list all child accounts. If false it list only the direct children
     * @return
     * @throws GwtEdcException
     */
    ListLoadResult<GwtAccount> findChildren(String accountId, boolean recoursive)
        throws GwtEdcException;

    /**
     * Lists all child of the given account id as a list of strings
     * 
     * @param scopeId The account id for which to find children
     * @param recoursive If true it list all child accounts. If false it list only the direct children
     * @return
     * @throws GwtEdcException
     */
    ListLoadResult<GwtAccountStringListItem> findChildrenAsStrings(String scopeId, boolean recoursive)
        throws GwtEdcException;

}
