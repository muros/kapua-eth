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
package org.eclipse.kapua.configuration.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.ComponentConfiguration;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.Domain;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.config.KapuaServiceConfigurationProvider;

public class AbstractKapuaConfigurableService implements KapuaConfigurableService, Serializable
{
    private static final long serialVersionUID = 5267829733117676086L;

    private Domain            domain           = null;
    private String            pid              = null;

    protected AbstractKapuaConfigurableService(String pid, Domain domain)
    {
        this.pid = pid;
        this.domain = domain;
    }

    @Override
    public Tocd getConfigMetadata()
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaServiceConfigurationProvider configProvider = locator.getService(KapuaServiceConfigurationProvider.class);
        return configProvider.getServiceConfiguration(pid).getDefinition();
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.read, scopeId));

        KapuaServiceConfigurationProvider configProvider = locator.getService(KapuaServiceConfigurationProvider.class);
        ComponentConfiguration compomentConfig = configProvider.getServiceConfiguration(pid);
        return compomentConfig == null ? null : compomentConfig.q();
    }

    @Override
    public void setConfigValues(KapuaId scopeId, Map<String, Object> values)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        KapuaServiceConfigurationProvider configProvider = locator.getService(KapuaServiceConfigurationProvider.class);
        Map<String, Object> cfg = new HashMap<String, Object>();
        cfg.put("scopeId", scopeId);
        configProvider.setServiceConfiguration(pid, cfg);
    }
}
