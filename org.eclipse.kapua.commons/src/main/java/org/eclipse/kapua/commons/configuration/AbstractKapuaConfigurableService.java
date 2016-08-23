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
package org.eclipse.kapua.commons.configuration;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.query.predicate.AndPredicate;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.util.EntityManager;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.KapuaAttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

@SuppressWarnings("serial")
public abstract class AbstractKapuaConfigurableService implements KapuaConfigurableService, Serializable
{
    private String            domain           = null;
    private String            pid              = null;

    protected AbstractKapuaConfigurableService(String pid, String domain)
    {
        this.pid = pid;
        this.domain = domain;
    }

    @Override
    public Tocd getConfigMetadata()
        throws KapuaException
    {
        return null;
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.read, scopeId));

        AndPredicate predicate = new AndPredicate()
                .and(new AttributePredicate<String>("pid", this.pid, Operator.EQUAL))
                .and(new AttributePredicate<KapuaId>("scopeId", scopeId, Operator.EQUAL));
        
        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
        query.setPredicate(predicate);
        
        EntityManager em = ServiceConfigEntityFactory.getEntityManager();
        ServiceConfigListResult result = ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResultImpl(), query);
        if (result == null || result.size() == 0) 
            throw KapuaException.internalError("Record not found");
        
        return null; // TODO convert to map...
    }

    @Override
    public void setConfigValues(KapuaId scopeId, Map<String, Object> values)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.write, scopeId));

        AndPredicate predicate = new AndPredicate()
                .and(new AttributePredicate<String>("pid", this.pid, Operator.EQUAL))
                .and(new AttributePredicate<KapuaId>("scopeId", scopeId, Operator.EQUAL));
        
        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
        query.setPredicate(predicate);
        
        EntityManager em = ServiceConfigEntityFactory.getEntityManager();
        ServiceConfigListResult result = ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResultImpl(), query);
        if (result == null || result.size() == 0) 
        {
            ServiceConfigImpl serviceConfig = new ServiceConfigImpl(scopeId);
            serviceConfig.setPid(this.pid);
            ServiceDAO.create(em, serviceConfig);
            
            return;
        }
        
        ServiceConfig serviceConfig = result.get(0);
        //
        //Do updates here !
        //
        ServiceDAO.update(em, ServiceConfig.class, serviceConfig);
    }
}
