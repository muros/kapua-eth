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

import java.util.List;
import java.util.ServiceLoader;

import com.google.common.collect.ImmutableList;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.service.KapuaService;

import com.google.inject.Provider;

public class KapuaServiceLoaderProvider<S extends KapuaService> implements Provider<S> 
{
	private final Class<S> serviceClass;
	private final S service;

	public KapuaServiceLoaderProvider(Class<S> serviceClass, S service) {
		this.serviceClass = serviceClass;
		this.service = service;
	}

	public KapuaServiceLoaderProvider(Class<S> serviceClass) {
		this(serviceClass, null);
	}

	public KapuaServiceLoaderProvider(S service) {
		this(null, service);
	}

	// Implemented operations

	public synchronized S get()
	{		
		if (service != null) { 
			return service;
		}

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

}
