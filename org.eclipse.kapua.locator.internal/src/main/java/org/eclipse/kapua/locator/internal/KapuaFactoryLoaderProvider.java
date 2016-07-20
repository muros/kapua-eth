package org.eclipse.kapua.locator.internal;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.model.KapuaObjectFactory;

import com.google.inject.Provider;

public class KapuaFactoryLoaderProvider<F extends KapuaObjectFactory> implements Provider<F> 
{
	private Class<F> factoryClass;
	private F        factory;
	
	public KapuaFactoryLoaderProvider(Class<F> serviceClass) {
		this.factoryClass = serviceClass;
	}
	
	public synchronized F get()
	{		
		if (factory != null) { 
			return factory;
		}
		
		ServiceLoader<F> serviceLoaders = ServiceLoader.load(factoryClass);
		Iterator<F> serviceLoaderIterator = serviceLoaders.iterator();
		while (serviceLoaderIterator.hasNext()) {
			factory = serviceLoaderIterator.next();
			break;
		}

		if (factory == null) {
			throw new KapuaRuntimeException(KapuaLocatorErrorCodes.FACTORY_UNAVAILABLE, factoryClass);
		}
		
		return factory;
	}	
}
