package org.eclipse.kapua.broker.core.threadfactory;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordToken;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable wrapper used to perform a new login once the new thread is created and started.
 * In that way it is possible to associate the Apache Shiro context ({@link Subject}) or the Kapua session to the new runnable in a clean way.
 * TODO change the log level to debug for the bound/unbound context of logging line?  
 *
 */
public class KapuaRunnableWrapper implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);
	
	private AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
	private UsernamePasswordTokenFactory credentialsFactory = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);
	
	private Runnable runnable;
	private boolean useLocalShiro;
	private boolean cleanUpThreadContext;
	private String threadName;
	/**
	 * Only for test
	 */
	private ThreadLocalChecker tdc;
	
	/**
	 * 
	 * @param runnable
	 * @param threadName
	 * @param useLocalShiro tell to the wrapper if the runnable should be bound with Apache Shiro session.
	 * 		If the authentication/authorization implementation service are accessed locally (are in the same jvm and accessible by the broker code) and the implementation is using Apache Shiro, the camel runnable should be bound with the Apache Shiro subject.
	 * 		So this flag should be set to true. Otherwise the runnable will be bound to Kapua session.  
	 * @param cleanUpThreadContext tell to the wrapper if clean up the thread local when start. It cleans up the Apache Shiro context if the useLocalShiro is true, otherwise it cleans up the Kapua session.
	 */
	public KapuaRunnableWrapper(Runnable runnable, String threadName, boolean useLocalShiro, boolean cleanUpThreadContext) {
		this.runnable = runnable;
		this.threadName = threadName;
		this.useLocalShiro = useLocalShiro;
		this.cleanUpThreadContext = cleanUpThreadContext;
	}
	
	@Override
	public void run() {
		try {
			logger.info("Starting runnable {} inside Thread {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName()});
			if (cleanUpThreadContext) {
				logger.info("###1 - cleanup thread... may be the thread is already logged in");
				logout();
			}
			else {
				logger.info("###1 - NO cleanup thread needed...");
			}
			//check if already logged with sys user 
			logger.info("###2 - login...");
			UsernamePasswordToken authenticationCredentials = getAuthenticationCredentials();
			AccessToken accessToken = authenticationService.login(authenticationCredentials);
			logger.info("###3 - bound context");
			if (useLocalShiro) {
				org.apache.shiro.subject.Subject shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
				logger.info("Bound Shiro context to runnable {} inside Thread {} - {} - Shiro subject {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName(), shiroSubject.toString(), shiroSubject.hashCode()});
				shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
				shiroSubject.associateWith(runnable);
			}
			else {
				KapuaSession kapuaSession = new KapuaSession(accessToken, null, accessToken.getScopeId(), accessToken.getUserId(), authenticationCredentials.getUsername());
				logger.info("Bound Kapua context to runnable {} inside Thread {} - {} - Kapua session {} - {}", new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName(), kapuaSession.toString(), kapuaSession.getAccessToken().getTokenId()});
				KapuaSecurityUtils.setSession(kapuaSession);
			}
		}
		catch (KapuaException e) {
			logger.error("Cannot perform login... {}", e.getMessage(), e);
		}
		
		tdc = new ThreadLocalChecker(threadName, useLocalShiro);
		tdc.start();
		
		runnable.run();
	}
	
	@Override
	protected void finalize() throws Throwable {
		tdc.shutdown();
		logout();
		super.finalize();
	}
	
	private void logout() throws KapuaException {
		logger.info("Cleanup thread... {} - {}", new Object[]{Thread.currentThread().getId(), Thread.currentThread().getName()});
		authenticationService.logout();
		if (useLocalShiro) {
			org.apache.shiro.subject.Subject shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
			logger.warn("The current thread ({} {} - {}) is authenticated {} with user {}! - shiro id {} - {}", 
					new Object[]{this, Thread.currentThread().getId(), Thread.currentThread().getName(),
							shiroSubject.isAuthenticated(), shiroSubject.getPrincipal(), shiroSubject.toString(), shiroSubject.hashCode()});
			ThreadGroup tdg = Thread.currentThread().getThreadGroup();
			if (tdg!=null && tdg.getParent()!=null) {
				logger.warn("parent thread group of {} is {}", new Object[]{tdg.getName(), tdg.getParent().getName()});
			}
			else {
				logger.warn("Parent thread group of {} is null", tdg);
			}
			
			logger.info("Cleanup thread... unbound shiro context {} - {}", new Object[]{shiroSubject.toString(), shiroSubject.hashCode()});
			org.apache.shiro.util.ThreadContext.unbindSubject();
		}
		else {
			KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
			logger.info("Cleanup thread... clean kapua session", new Object[]{kapuaSession.toString(), kapuaSession.getAccessToken().getTokenId()});
			KapuaSecurityUtils.clearSession();
		}
		logger.info("Cleanup thread... DONE");
	}

	//TODO choose how create authentication credentials for the runnable
	private UsernamePasswordToken getAuthenticationCredentials() {
		String username = "kapua-sys";
		String password = "We!come12345";
		return credentialsFactory.newInstance(username, password.toCharArray());
	}
	
}

/**
 * Test class. It's used for debug purpose. This thread checks periodically if the Apache Shiro subject (or Kapua session depending on the useLocalShiro flag) is coherent with the original one.
 *
 */
class ThreadLocalChecker extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);
	
	private static long CHECK_INTERVAL = 15000;
	
	private String parentName;
	private boolean useLocalShiro;
	private org.apache.shiro.subject.Subject shiroSubject;
	private KapuaSession kapuaSession;
	private boolean isRunning;
	
	public ThreadLocalChecker(String parentName, boolean useLocalShiro) {
		this.parentName = parentName;
		this.useLocalShiro = useLocalShiro;
		isRunning = true;
	}
	
	@Override
	public void run() {
		if (useLocalShiro) {
			shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
			if (!shiroSubject.isAuthenticated()) {
				throw new RuntimeException("The thread " + parentName + " should be logged!");
			}
			logger.info("Orig Shiro sub for thread {} is {} - {}", new Object[]{parentName, shiroSubject.toString().substring(33), shiroSubject.hashCode()});
		}
		else {
			kapuaSession = KapuaSecurityUtils.getSession();
			if (kapuaSession == null || kapuaSession.getAccessToken() == null || kapuaSession.getAccessToken().getTokenId() == null) {
				throw new RuntimeException("No kapua session found for the thread " + parentName);
			}
			logger.info("Orig Kapua session for thread {} is {} - {}", new Object[]{parentName, kapuaSession.toString(), kapuaSession.getAccessToken().getTokenId()});
		}
		while(isRunning) {
			try {
				if (useLocalShiro) {
					org.apache.shiro.subject.Subject tmp = org.apache.shiro.SecurityUtils.getSubject();
					if (tmp == null || !tmp.equals(shiroSubject)) {
						logger.info("Thread {} orig Shiro sub {} - {} - Current {} - {}", new Object[]{parentName,
							shiroSubject.toString().substring(33), shiroSubject.hashCode(), (tmp!=null ? tmp.toString().substring(33) : "null"), (tmp!=null ? tmp.hashCode() : "null")});
						throw new RuntimeException("Wrong shiro subject (the subject had changed since last check)!");
					}
					else {
						logger.info("Shiro subject correct for Thread {} orig {} - {} - current {} - {}", new Object[]{parentName,
							shiroSubject.toString().substring(33), shiroSubject.hashCode(), (tmp!=null ? tmp.toString().substring(33) : "null"), (tmp!=null ? tmp.hashCode() : "null")});
					}
				}
				else {
					KapuaSession tmp = KapuaSecurityUtils.getSession();
					if (tmp == null || !tmp.equals(kapuaSession)) {
						logger.info("Thread {} original KapuaSession subject {} - {} - {} - {}", new Object[]{parentName,
							kapuaSession.toString(), kapuaSession.getAccessToken().getTokenId(), (tmp!=null ? tmp.toString() : "null"), (tmp!=null ? tmp.getAccessToken().getTokenId() : "null")});
						throw new RuntimeException("Wrong Kapua sessiont (the session had changed since last check)!");
					}
					else {
						logger.info("Kapua session valid for Thread {} original KapuaSession subject {} - {} - Current {} - {}", new Object[]{parentName,
							kapuaSession.toString(), kapuaSession.getAccessToken().getTokenId(), (tmp!=null ? tmp.toString() : "null"), (tmp!=null ? tmp.getAccessToken().getTokenId() : "null")});						
					}
				}
				Thread.sleep(CHECK_INTERVAL);
			} 
			catch (InterruptedException e) {
				logger.warn("Error waiting for check {}", e.getMessage(), e);
			}
		}
	}
	
	public void shutdown() {
		isRunning = false;
	}
	
}