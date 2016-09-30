/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.v1.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountService;

import io.swagger.annotations.Api;


@Api
@Path("/accounts")
public class Accounts extends AbstractKapuaResource 
{
	private final KapuaLocator locator = KapuaLocator.getInstance();
	private final AccountService accountService = locator.getService(AccountService.class);
	private final AccountFactory accountFactory = locator.getFactory(AccountFactory.class);
	
    /**
     * Returns the list of all the Accounts visible to the currently connected user.
     *
     * @return The list of requested Account objects.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccountListResult getAccounts() {

        AccountListResult accountsResult = accountFactory.newAccountListResult();
        
        try {
        	KapuaSession session = KapuaSecurityUtils.getSession();
        	accountsResult = (AccountListResult) accountService.findChildsRecursively(session.getScopeId());
        } catch (Throwable t) {
            handleException(t);
        }
        return accountsResult;
    }

    /**
     * Returns the Account specified by the "id" path parameter.
     *
     * @param accountId The id of the Account requested.
     * @return The requested Account object.
     */
    @GET
    @Path("{accountId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Account getAccount(@PathParam("accountId") String accountId) {

        Account account = null;
        try {
        	KapuaId id = KapuaEid.parseShortId(accountId);
            account = accountService.find(id);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(account);
    }

    /**
     * Creates a new Account based on the information provided in AccountCreator parameter.
     *
     * @param accountCreator Provides the information for the new Account to be created.
     * @return The newly created Account object.
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Account postAccount(AccountCreator accountCreator) {

        Account account = null;
        try {
            account = accountService.create(accountCreator);
        } catch (Throwable t) {
            handleException(t);
        }
        return returnNotNullEntity(account);
    }

}
