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
package org.eclipse.kapua.locator;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.KapuaService;

/**
 * Interface to load KapuaService instances in a given environment.
 * implementations of the KapuaServiceLocator can decide whether to return
 * local instances or proxies to remote instances.
 */
public interface KapuaLocator
{
    public static KapuaLocator getInstance()
    {
        ServiceLoader<KapuaLocator> serviceLocatorLoaders = ServiceLoader.load(KapuaLocator.class);

        KapuaLocator kapuaServiceLocator = null;
        Iterator<KapuaLocator> serviceLocatorLoaderIterator = serviceLocatorLoaders.iterator();
        while (serviceLocatorLoaderIterator.hasNext()) {
            kapuaServiceLocator = serviceLocatorLoaderIterator.next();
            break;
        }

        if (kapuaServiceLocator == null) {
            throw new KapuaRuntimeException(KapuaRuntimeErrorCodes.SERVICE_LOCATOR_UNAVAILABLE);
        }

        return kapuaServiceLocator;
    }

    /**
     * Returns an instance of a KapuaService implementing the provided KapuaService class.
     * 
     * @param serviceClass - class of the service whose instance is required.
     * @return
     */
    public <S extends KapuaService> S getService(Class<S> serviceClass);

    /**
     * Returns an instance of a KapuaEntityFactory implementing the provided KapuaFactory class.
     * 
     * @param factoryClass - class of the factory whose instance is required.
     * @return
     */
    public <F extends KapuaEntityFactory> F getFactory(Class<F> factoryClass);
}
