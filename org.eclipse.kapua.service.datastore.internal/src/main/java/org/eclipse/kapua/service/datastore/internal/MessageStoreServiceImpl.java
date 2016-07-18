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
package org.eclipse.kapua.service.datastore.internal;

import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.configuration.spi.AbstractKapuaConfigurableService;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.StorableQuery;
import org.eclipse.kapua.service.datastore.StorableResultList;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsMessageStoreServiceImpl;
import org.eclipse.kapua.service.datastore.internal.model.StorableIdImpl;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.StorableId;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;

public class MessageStoreServiceImpl extends AbstractKapuaConfigurableService implements MessageStoreService
{
    private static final long serialVersionUID = 4142282449826005424L;
    
    private EsMessageStoreServiceImpl esMessageStoreService;

    public MessageStoreServiceImpl()
    {
        // TODO pass a correct pid and a correct domain
        super("PID", "DOMAIN");
        esMessageStoreService = new EsMessageStoreServiceImpl(this);
    }

    @Override
    public StorableId store(KapuaId scopeId, MessageCreator messageCreator)
        throws KapuaException
    {
        //
        // Check Access
        // TODO temporary
        String scopeName = this.checkDataAccess(scopeId, MessageStoreServiceAction.CREATE);
        return new StorableIdImpl(esMessageStoreService.store(scopeName, messageCreator));
    }

    @Override
    public void delete(KapuaId scopeId, StorableId uuid)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Message find(KapuaId scopeId, StorableId id, MessageFetchStyle fetchStyle)
        throws KapuaException
    {
        //
        // Check Access
        String scopeName = this.checkDataAccess(scopeId, MessageStoreServiceAction.READ);
        return esMessageStoreService.find(scopeName, id.toString(), fetchStyle);
    }

    @Override
    public StorableResultList<Message> query(KapuaId scopeId, StorableQuery<Message> query)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

    // -----------------------------------------------------------------------------------------
    //
    // Private methods
    //
    // -----------------------------------------------------------------------------------------

    private String checkDataAccess(KapuaId scopeId, AccessAction action)
        throws KapuaException
    {
        //
        // Check Access
        KapuaLocator serviceLocator = KapuaLocator.getInstance();
        AccountService accountService = serviceLocator.getService(AccountService.class);
        AuthorizationService authorizationService = serviceLocator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = serviceLocator.getFactory(PermissionFactory.class);

        Account account = accountService.find(scopeId);

        // TODO add enum for actions
        Permission permission = permissionFactory.newPermission("data", action.key(), account.getId());
        authorizationService.checkPermission(permission);
        
        return account.getName();
    }

    @Override
    public Tocd getConfigMetadata()
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setConfigValues(KapuaId scopeId, Map<String, Object> values)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }
}
