package org.eclipse.kapua.broker.core.locator;

import org.aopalliance.intercept.MethodInterceptor;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.shiro.AuthenticationServiceShiroImpl;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.shiro.AuthorizationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;

public class BrokerModule extends AbstractModule {
	
	private static Logger logger = LoggerFactory.getLogger(BrokerModule.class);
	
	private MethodInterceptor methodInterceptor;
	
	public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
    	this.methodInterceptor = methodInterceptor;
	}
	
	@Override
	protected void configure() {
		try {
			bind(AuthenticationService.class).to(AuthenticationServiceShiroImpl.class).in(Scopes.SINGLETON);
			bind(AuthorizationService.class).to(AuthorizationServiceImpl.class).in(Scopes.SINGLETON);
			
			if (this.methodInterceptor != null) {
				bindInterceptor(
						Matchers.any(), 
						Matchers.annotatedWith(SecuredCall.class),
						this.methodInterceptor);
				logger.info("Method interceptor {} has been bound to SecuredCall class", this.methodInterceptor.getClass().getName());
			} else {
				logger.warn("Method interceptor bound to SecuredCall class: NONE");
			}
		}
		
		catch (Throwable t) {
			logger.error("Cannot inizialize binding {}", t.getMessage(), t);
		}
	}
	
}