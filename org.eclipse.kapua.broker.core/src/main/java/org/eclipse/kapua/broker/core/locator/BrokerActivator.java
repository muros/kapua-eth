package org.eclipse.kapua.broker.core.locator;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerActivator implements org.osgi.framework.BundleActivator {
	
	private static Logger logger = LoggerFactory.getLogger(BrokerActivator.class);

    public void start(org.osgi.framework.BundleContext context) throws Exception {
    	logger.info("Init registered domains...");
    	//available domain initialization
		
		//init dependency injection so we can delegate the ResourceLoader implementation to be injected where needed
		logger.info("Init dependency injection container...");
		BrokerDirectory brokerDirectory = new BrokerDirectory();
		//set interceptor if any (it should be used for security check when call methods
		MethodInterceptor methodInterceptor = null;
		brokerDirectory.setMethodInterceptor(methodInterceptor);
		
		brokerDirectory.init();
		logger.info("Init dependency injection container... DONE");
    }

    public void stop(org.osgi.framework.BundleContext context) throws Exception {
    }

}