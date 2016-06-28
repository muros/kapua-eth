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
package org.eclipse.kapua.service.config;

import java.util.Map;

import org.eclipse.kapua.model.config.ComponentConfiguration;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;

public interface KapuaServiceConfigurationProvider<E extends KapuaConfigEntity, C extends KapuaConfigEntityCreator<E>, F extends KapuaConfigEntityFactory<E, C>> 
{
	public ComponentConfiguration getServiceConfiguration(Class<C> clazz, Class<F> clazzFactory, String pid);

	public void setServiceConfiguration(Class<C> clazz, Class<F> clazzFactory, String pid, Map<String, Object> values);
}
