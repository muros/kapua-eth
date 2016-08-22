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
 *     Red Hat - loading test services
 *
 *******************************************************************************/
package org.eclipse.kapua.locator.spi;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.ImmutableList;
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
        List<S> serviceCandidates = ImmutableList.copyOf(ServiceLoader.load(serviceClass).iterator());

        if(serviceCandidates.isEmpty()) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
        }  else if(serviceCandidates.size() > 1) {
            for (S service : serviceCandidates) {
                if (service.getClass().isAnnotationPresent(TestService.class)) {
                    return service;
                }
            }
        }

        return serviceCandidates.get(0);
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
