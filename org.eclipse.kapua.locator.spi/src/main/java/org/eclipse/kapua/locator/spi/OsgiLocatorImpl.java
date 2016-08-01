package org.eclipse.kapua.locator.spi;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.spi.KapuaLocatorErrorCodes;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link KapuaLocator} implementation to be used in osgi environment.
 * It loads services via bundle context. The services should be exported or programmatically or via "declarative services".
 *
 */
public class OsgiLocatorImpl extends KapuaLocator {
	
	private static Logger logger = LoggerFactory.getLogger(OsgiLocatorImpl.class);
    
	private BundleContext bundleContext;
	
	public OsgiLocatorImpl() {
		bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
	}
	
	@Override
	public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
		try {
			ServiceReference<F> serviceReference = bundleContext.getServiceReference(factoryClass);
			if (serviceReference==null) {
				logger.error("Cannot find registered factory {}", factoryClass);
				throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, factoryClass);
			}
			return bundleContext.getService(serviceReference);
		}
		catch (IllegalStateException | SecurityException | IllegalArgumentException e) {
			logger.error("Error during factory lookup", e);
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
	    	return bundleContext.getService(serviceReference);
		}
		catch (IllegalStateException | SecurityException | IllegalArgumentException e) {
			logger.error("Error during service lookup", e);
			throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, serviceClass);
		}
	}

}