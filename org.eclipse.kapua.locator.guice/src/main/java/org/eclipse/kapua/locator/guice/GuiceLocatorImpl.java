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
package org.eclipse.kapua.locator.guice;

import com.google.inject.ConfigurationException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceLocatorImpl extends KapuaLocator
{
	
	private static final Logger logger = LoggerFactory.getLogger(GuiceLocatorImpl.class);
	
	private static Injector s_injector = null;
	static {
		try {
			s_injector = Guice.createInjector(new KapuaModule());
		}
		catch (Throwable e) {
			logger.error("Cannot instantiate injector {}", e.getMessage(), e);
			throw e;
		}
	}
	
    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass) {
        try {
            return s_injector.getInstance(serviceClass);
        } catch (ConfigurationException e) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
        }
    }

    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass)
    {
    	F kapuaEntityFactory = s_injector.getInstance(factoryClass);
        if (kapuaEntityFactory == null) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, factoryClass);
        }
    	
    	return kapuaEntityFactory;
    }
}
