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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.service.KapuaService;

import com.google.inject.Provider;

public class KapuaServiceLoaderProvider<S extends KapuaService> implements Provider<S> 
{
	private Class<S> serviceClass;
	private S        service;
	
	public KapuaServiceLoaderProvider(Class<S> serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	public synchronized S get()
	{		
		if (service != null) { 
			return service;
		}
		
		ServiceLoader<S> serviceLoaders = ServiceLoader.load(serviceClass);
		Iterator<S> serviceLoaderIterator = serviceLoaders.iterator();
		while (serviceLoaderIterator.hasNext()) {
			service = serviceLoaderIterator.next();
			break;
		}

		if (service == null) {
			throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
		}
		
		return service;
	}	
}
