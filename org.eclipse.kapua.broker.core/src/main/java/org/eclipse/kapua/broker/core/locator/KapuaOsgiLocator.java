package org.eclipse.kapua.broker.core.locator;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.spi.KapuaLocatorErrorCodes;
import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.KapuaService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
//import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KapuaOsgiLocator extends KapuaLocator {
	
	private static Logger logger = LoggerFactory.getLogger(KapuaOsgiLocator.class);
    
	private BundleContext bundleContext;
	
	public KapuaOsgiLocator() {
		bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
	}
	
	@Override
	public <F extends KapuaEntityFactory> F getFactory(Class<F> factoryClass) {
		//TODO check the exception type thrown
//    	ServiceReference<F>[] serviceReferences;
//		try {
//			serviceReferences = (ServiceReference<F>[]) bundleContext.getAllServiceReferences(factoryClass.getName(), null);
//		} catch (InvalidSyntaxException e) {
//			logger.error("InvalidSyntaxException", e);
//			throw new RuntimeException();
//		}
//		if (serviceReferences==null || serviceReferences.length<=0) {
//			throw new RuntimeException();
//		}
//		ServiceReference<F> serviceReference = serviceReferences[0];
		
		try {
			ServiceReference<F> serviceReference = bundleContext.getServiceReference(factoryClass);
			if (serviceReference==null) {
				logger.error("Cannot find registered factory {}", factoryClass);
				throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, factoryClass);
			}
			F service = bundleContext.getService(serviceReference);
			return service;
		}
		catch (IllegalStateException | SecurityException | IllegalArgumentException e) {
			logger.error("Erro during factory lookup", e);
			throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, factoryClass);
		}
	}
	
	@Override
	public <S extends KapuaService> S getService(Class<S> serviceClass) {
		try {
			ServiceReference<S> serviceReference = bundleContext.getServiceReference(serviceClass);
			if (serviceReference==null) {
				logger.error("Cannot find registered service {}", serviceClass);
				throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
			}
	    	S service = bundleContext.getService(serviceReference);
			return service;
		}
		catch (IllegalStateException | SecurityException | IllegalArgumentException e) {
			logger.error("Erro during service lookup", e);
			throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
		}
	}

}