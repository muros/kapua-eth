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
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;

/**
 * {@link KapuaLocator} implementation to be used in single classloader environment.
 * It loads services via {@link ServiceLoader}
 *
 */
public class LocatorImpl extends KapuaLocator
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
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass)
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
}
