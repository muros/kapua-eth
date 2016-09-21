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
package org.eclipse.kapua.broker.core.threadfactory;

import org.apache.shiro.util.ThreadContext;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.locator.KapuaLocator;
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
public class KapuaRunnableWrapper implements Runnable
{

    private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);

    private AuthenticationService        authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
    private UsernamePasswordTokenFactory credentialsFactory    = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);

    private Runnable           runnable;
    private String             threadName;
    /**
     * Only for test
     */
    private ThreadLocalChecker tdc;

    /**
     * 
     * @param runnable
     * @param threadName
     */
    public KapuaRunnableWrapper(Runnable runnable, String threadName)
    {
        this.runnable = runnable;
        this.threadName = threadName;
    }

    @Override
    public void run()
    {
        try {
            // The Shiro subject is inheritable so if a new thread is created from an already logged thread the new thread will be logged as the parent.
            // The Kapua session is not inheritable and in any case, for security reason it's better to reset the trusted mode flag once a new thread is created from an already logged one.
            // So as first step we can unbound the Shiro subject from the thread context
            logger.info("Starting runnable {} inside Thread {} - {}", new Object[] { this, Thread.currentThread().getId(), Thread.currentThread().getName() });
            org.apache.shiro.subject.Subject shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
            boolean isAuthenticated = shiroSubject.isAuthenticated();
            if (isAuthenticated) {
                org.apache.shiro.subject.Subject sbj = ThreadContext.unbindSubject();
                logger.info("### - thread parent already authenticated. unbind previous thread context... {} - {}", new Object[] { sbj.toString(), sbj.hashCode() });
            }
            logger.info("### - login...");
            UsernamePasswordToken authenticationCredentials = getAuthenticationCredentials();
            authenticationService.login(authenticationCredentials);
            logger.info("### - login... DONE");
            shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
            shiroSubject.associateWith(this);
            logger.info("Bounding Shiro context to runnable {} inside Thread {} - {} - Shiro subject {} - {}",
                        new Object[] { this, Thread.currentThread().getId(), Thread.currentThread().getName(), shiroSubject.toString(), shiroSubject.hashCode() });
            // if (isAuthenticated) {
            // logger.info("### - thread parent already authenticated. Cloning Kapua session for the new one...");
            // // for security reason create a copy of the kapua session object (so if from a thread the fag "trustedMode" will be set, the other threads aren't affected)
            // KapuaSession kapuaSession = (KapuaSession) shiroSubject.getSession().getAttribute(KapuaSession.KAPUA_SESSION_KEY);
            // KapuaSession kapuaSessionCopy = KapuaSession.createFrom(kapuaSession);
            // KapuaSecurityUtils.setSession(kapuaSessionCopy);
            // logger.info("### - thread parent already authenticated. Cloning Kapua session for the new one... DONE (old {} - new {})", new Object[] { kapuaSession.toString(),
            // kapuaSessionCopy.toString() });
            // }
            logger.info("DONE Bounding Shiro context to runnable {} inside Thread {} - Shiro subject {}", new Object[] { this, Thread.currentThread().getId(), shiroSubject.toString() });
        }
        catch (

        KapuaException e) {
            logger.error("Cannot perform login... {}", e.getMessage(), e);
        }

        tdc = new ThreadLocalChecker(threadName);
        tdc.start();

        runnable.run();
    }

    @Override
    protected void finalize() throws Throwable
    {
        tdc.shutdown();
        logout();
        super.finalize();
    }

    private void logout() throws KapuaException
    {
        logger.info("Cleanup thread... {} - {}", new Object[] { Thread.currentThread().getId(), Thread.currentThread().getName() });
        authenticationService.logout();
        org.apache.shiro.subject.Subject shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
        logger.warn("The current thread ({} {} - {}) is authenticated {} with user {}! - shiro id {} - {}",
                    new Object[] { this, Thread.currentThread().getId(), Thread.currentThread().getName(),
                                   shiroSubject.isAuthenticated(), shiroSubject.getPrincipal(), shiroSubject.toString(), shiroSubject.hashCode() });
        ThreadGroup tdg = Thread.currentThread().getThreadGroup();
        if (tdg != null && tdg.getParent() != null) {
            logger.warn("parent thread group of {} is {}", new Object[] { tdg.getName(), tdg.getParent().getName() });
        }
        else {
            logger.warn("Parent thread group of {} is null", tdg);
        }

        logger.info("Cleanup thread... unbound shiro context {} - {}", new Object[] { shiroSubject.toString(), shiroSubject.hashCode() });
        org.apache.shiro.util.ThreadContext.unbindSubject();
        logger.info("Cleanup thread... DONE");
    }

    // TODO choose how create authentication credentials for the runnable
    private UsernamePasswordToken getAuthenticationCredentials()
    {
        String username = "kapua-sys";
        String password = "kapua-password";
        return credentialsFactory.newInstance(username, password.toCharArray());
    }

}

/**
 * Test class. It's used for debug purpose. This thread checks periodically if the Apache Shiro subject (or Kapua session depending on the useLocalShiro flag) is coherent with the original one.
 *
 */
class ThreadLocalChecker extends Thread
{

    private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);

    private static long CHECK_INTERVAL = 15000;

    private String                           parentName;
    private org.apache.shiro.subject.Subject shiroSubject;
    private boolean                          isRunning;

    public ThreadLocalChecker(String parentName)
    {
        this.parentName = parentName;
        isRunning = true;
    }

    @Override
    public void run()
    {
        shiroSubject = org.apache.shiro.SecurityUtils.getSubject();
        if (!shiroSubject.isAuthenticated()) {
            throw new RuntimeException("The thread " + parentName + " should be logged!");
        }
        logger.info("Orig Shiro sub for thread {} is {} - {}", new Object[] { parentName, shiroSubject.toString().substring(33), shiroSubject.hashCode() });
        while (isRunning) {
            try {
                org.apache.shiro.subject.Subject tmp = org.apache.shiro.SecurityUtils.getSubject();
                if (tmp == null || !tmp.equals(shiroSubject)) {
                    logger.info("Thread {} orig Shiro sub {} - {} - Current {} - {}", new Object[] { parentName,
                                                                                                     shiroSubject.toString().substring(33), shiroSubject.hashCode(), (tmp != null ? tmp.toString().substring(33) : "null"),
                                                                                                     (tmp != null ? tmp.hashCode() : "null") });
                    throw new RuntimeException("Wrong shiro subject (the subject had changed since last check)!");
                }
                else {
                    logger.info("Shiro subject correct for Thread {} orig {} - {} - current {} - {}", new Object[] { parentName,
                                                                                                                     shiroSubject.toString().substring(33), shiroSubject.hashCode(), (tmp != null ? tmp.toString().substring(33) : "null"),
                                                                                                                     (tmp != null ? tmp.hashCode() : "null") });
                }
                Thread.sleep(CHECK_INTERVAL);
            }
            catch (InterruptedException e) {
                logger.warn("Error waiting for check {}", e.getMessage(), e);
            }
        }
    }

    public void shutdown()
    {
        isRunning = false;
    }

}
