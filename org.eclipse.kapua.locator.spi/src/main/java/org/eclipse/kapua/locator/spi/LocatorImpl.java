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
package org.eclipse.kapua.locator.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.config.KapuaConfigEntity;
import org.eclipse.kapua.model.config.KapuaConfigEntityCreator;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.config.KapuaConfigEntityFactory;
import org.eclipse.kapua.service.config.KapuaServiceConfigurationProvider;

public class LocatorImpl implements KapuaLocator
{
    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass)
    {
        ServiceLoader<S> serviceLoaders = ServiceLoader.load(serviceClass);

        S kapuaService = null;
        Iterator<S> serviceLoaderIterator = serviceLoaders.iterator();
        while (serviceLoaderIterator.hasNext()) {
            kapuaService = serviceLoaderIterator.next();
            break;
        }

        if (kapuaService == null) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
        }

        return kapuaService;
    }

    @Override
    public <F extends KapuaEntityFactory> F getFactory(Class<F> factoryClass)
    {
        ServiceLoader<F> factoryLoaders = ServiceLoader.load(factoryClass);

        F kapuaEntityFactory = null;
        Iterator<F> factoryLoaderIterator = factoryLoaders.iterator();
        while (factoryLoaderIterator.hasNext()) {
            kapuaEntityFactory = factoryLoaderIterator.next();
            break;
        }

        if (kapuaEntityFactory == null) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.FACTORY_UNAVAILABLE, factoryClass);
        }

        return kapuaEntityFactory;
    }

	@Override
    public <E extends KapuaConfigEntity, C extends KapuaConfigEntityCreator<E>, F extends KapuaConfigEntityFactory<E, C>, P extends KapuaServiceConfigurationProvider<E,C,F>> P getServiceConfigProvider(Class<P> serviceClass)
    {
	    ServiceLoader<P> serviceConfigManagerLoaders = ServiceLoader.load(serviceClass);

        P serviceConfigManager = null;
        Iterator<P> serviceConfigManagerLoadersIterator = serviceConfigManagerLoaders.iterator();
        while (serviceConfigManagerLoadersIterator.hasNext()) {
        	serviceConfigManager = serviceConfigManagerLoadersIterator.next();
            break;
        }

        if (serviceConfigManager == null) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_CONFIG_SPI_UNAVAILABLE, serviceClass);
        }

        return serviceConfigManager;
	}
}
