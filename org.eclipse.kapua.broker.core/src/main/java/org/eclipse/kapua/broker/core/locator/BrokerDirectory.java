package org.eclipse.kapua.broker.core.locator;

import org.aopalliance.intercept.MethodInterceptor;
import org.eclipse.kapua.locator.KapuaServiceLoader;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class BrokerDirectory implements KapuaServiceLoader {
	
	private static Logger logger = LoggerFactory.getLogger(BrokerDirectory.class);

    private Injector injector;
    private MethodInterceptor methodInterceptor;

    public MethodInterceptor getMethodInterceptor() {
    	return this.methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
    	this.methodInterceptor = methodInterceptor;
    }
    
    public void init() {
		logger.trace("Creating Guice Injector ...");
		
		BrokerModule brokerModule = new BrokerModule();
		brokerModule.setMethodInterceptor(methodInterceptor);
		this.injector = Guice.createInjector(brokerModule);
		
		logger.trace("...Injector created");
    }
    
    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
    	return this.injector.getInstance(factoryClass);
    }
    
    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass) {
    	return this.injector.getInstance(serviceClass);
    }

}