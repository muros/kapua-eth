package org.eclipse.kapua.commons.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.ComponentConfiguration;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigEntityFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.config.KapuaServiceConfigurationProvider;

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
public class AbstractKapuaConfigurableService <E extends KapuaConfigEntity, C extends KapuaConfigEntityCreator<E>, F extends KapuaConfigEntityFactory<E, C>> implements KapuaConfigurableService, Serializable
{
	private static final long serialVersionUID = 5267829733117676086L;

	private String domain = null;
	private String pid = null;
	private Class<C> clazz = null;
	private Class<F> clazzFactory = null;
	private KapuaLocator locator = null;
	
	protected AbstractKapuaConfigurableService(Class<C> clazz, Class<F> clazzFactory, String pid, String domain)
	{
		this.locator = KapuaLocator.getInstance();
		this.pid = pid;
		this.clazz = clazz;
		this.clazzFactory = clazzFactory;
		this.domain = domain;
	}

	public Tocd getConfigMetadata(KapuaId scopeId) throws KapuaException
	{
		KapuaServiceConfigurationProvider<E,C,F> configManager = locator.getServiceConfigProvider(KapuaServiceConfigurationProvider.class);
		return configManager.getServiceConfiguration(clazz, clazzFactory, pid).getDefinition();
	}
	
	public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException
	{
		AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance(this.domain, "read", scopeId));
		
		KapuaServiceConfigurationProvider<E,C,F> configManager = locator.getServiceConfigProvider(KapuaServiceConfigurationProvider.class);
		ComponentConfiguration compomentConfig = configManager.getServiceConfiguration(clazz, clazzFactory, pid);
		return compomentConfig == null ? null : compomentConfig.q();
	}
	
	public void setConfigValues(KapuaId scopeId, Map<String, Object> values) throws KapuaException
	{
		KapuaServiceConfigurationProvider configManager = locator.getServiceConfigProvider(KapuaServiceConfigurationProvider.class);
		Map<String, Object> cfg = new HashMap<String, Object>();
		cfg.put("scopeId", scopeId);
		configManager.setServiceConfiguration(clazz, clazzFactory, pid, cfg);
	}
}
