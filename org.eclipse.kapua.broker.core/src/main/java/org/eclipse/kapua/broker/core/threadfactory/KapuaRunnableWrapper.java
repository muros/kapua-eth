/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordToken;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable wrapper used to perform a new login once the new thread is created and started.<BR>
 * In that way it is possible to associate the Apache Shiro context ({@link Subject}) and the Kapua session to the new runnable in a clean way.<BR>
 * TODO change the log level to debug for the bound/unbound context of logging line?
 *
 */
public class KapuaRunnableWrapper implements Runnable
{

    private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);

    private AuthenticationService        authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
    private UsernamePasswordTokenFactory credentialsFactory    = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);

    private Runnable runnable;
    private String   threadName;

    /**
     * Constructs a new Kapua Runnable wrapper
     * 
     * @param runnable runnable to wrap
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
            logger.info("Starting runnable {} inside Thread {} - {} Thread name {}", new Object[] { this, Thread.currentThread().getId(), Thread.currentThread().getName(), threadName });
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
            logger.info("Bounding Shiro context to runnable {} inside Thread {} - {} - Shiro subject {} - {} Thread name {}",
                        new Object[] { this, Thread.currentThread().getId(), Thread.currentThread().getName(), shiroSubject.toString(), shiroSubject.hashCode(), threadName });
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

        runnable.run();
    }

    @Override
    protected void finalize() throws Throwable
    {
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
