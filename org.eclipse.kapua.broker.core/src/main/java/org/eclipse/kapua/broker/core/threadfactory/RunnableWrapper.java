package org.eclipse.kapua.broker.core.threadfactory;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable wrapper used to perform a new login once the new thread is created and started.
 * In that way we can associate the shiro context ({@link Subject}) to the new runnable in a clean way.
 *
 */
public class RunnableWrapper implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);
	
	private AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
	private UsernamePasswordTokenFactory credentialsFactory = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);
	
	private Runnable runnable;
	private boolean boundShiroContext;
	
	public RunnableWrapper(Runnable runnable, boolean boundShiroContext) {
		this.runnable = runnable;
		this.boundShiroContext = boundShiroContext;
	}
	
	@Override
	public void run() {
		try {
			logger.info("Starting runnable {} inside Thread {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName()});
			AccessToken accessToken = authenticationService.login(getAuthenticationCredentials());
			//if the auth service is local and is implemented by shiro the runnable must be bound with the shiro subject
			if (boundShiroContext) {
				logger.info("Bound Shiro context to runnable {} inside Thread {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName()});
				org.apache.shiro.subject.Subject shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
				shiroSubject.associateWith(runnable);
			}
			//otherwise keep kapua session bound with the thread context
			else {
				logger.info("Bound Kapua session to runnable {} inside Thread {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName()});
				KapuaSession session = new KapuaSession(accessToken, accessToken.getScopeId(), accessToken.getScopeId(), accessToken.getId(), "kapua-sys");
				KapuaSecurityUtils.setSession(session);
			}
		}
		catch (KapuaException e) {
			logger.error("Cannot perform login... {}", e.getMessage(), e);
		}
		runnable.run();
	}

	//TODO choose how create authentication credentials for the runnable
	private AuthenticationCredentials getAuthenticationCredentials() {
		String username = "kapua-sys";
		String password = "We!come12345";
		return credentialsFactory.newInstance(username, password.toCharArray());
	}
	
}