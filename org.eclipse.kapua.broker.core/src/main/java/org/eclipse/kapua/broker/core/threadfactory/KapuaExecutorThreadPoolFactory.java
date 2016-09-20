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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.camel.impl.DefaultThreadPoolFactory;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.util.concurrent.CamelThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kapua thread pool factory implementation.
 * The behavior of this thread pool factory is transparent so
 *  - it uses the {@link DefaultThreadPoolFactory} for instantiating {@link ExecutorService} for camel internal use) if 
 *  - if the request for a thread pool is coming form a {@link ThreadFactory} of {@link CamelThreadFactory} and its name starts with the kapuaThreadPoolPatternName specified in the constructor the {@link ThreadFactory} is replaced by the {@link KapuaExecutorThreadFactory}
 *
 */
public class KapuaExecutorThreadPoolFactory extends DefaultThreadPoolFactory {
	
	private static final Logger s_logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);
	
	private KapuaExecutorThreadFactory kapuaThreadFactory;
	
	/**
	 * 
	 * @param kapuaThreadPoolPatternName the start name of the kapua thread pool. 
	 */
	public KapuaExecutorThreadPoolFactory(KapuaExecutorThreadFactory kapuaThreadFactory) {
		super();
		this.kapuaThreadFactory = kapuaThreadFactory;
	}
	
	@Override
	public ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
		s_logger.info("Creating ExecutorService for thread factory {}", threadFactory);
		return super.newCachedThreadPool(replaceThreadFactoryIfNeeed(threadFactory));
	}
	
	@Override
    public ExecutorService newThreadPool(ThreadPoolProfile profile, ThreadFactory factory) {
        // allow core thread timeout is default false if not configured
        boolean allow = profile.getAllowCoreThreadTimeOut() != null ? profile.getAllowCoreThreadTimeOut() : false;
        return newThreadPool(profile.getPoolSize(), 
                             profile.getMaxPoolSize(), 
                             profile.getKeepAliveTime(),
                             profile.getTimeUnit(),
                             profile.getMaxQueueSize(),
                             allow,
                             profile.getRejectedExecutionHandler(),
                             factory);
    }

    public ExecutorService newThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit, int maxQueueSize, boolean allowCoreThreadTimeOut,
                                         RejectedExecutionHandler rejectedExecutionHandler, ThreadFactory threadFactory) throws IllegalArgumentException {
    	s_logger.info("Creating ExecutorService for thread factory {}", threadFactory);
    	return super.newThreadPool(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, maxQueueSize, allowCoreThreadTimeOut, rejectedExecutionHandler, replaceThreadFactoryIfNeeed(threadFactory));
    }
    
    @Override
    public ScheduledExecutorService newScheduledThreadPool(ThreadPoolProfile profile, ThreadFactory threadFactory) {
    	s_logger.info("Creating ScheduledExecutorService for thread factory {}", threadFactory);
    	return super.newScheduledThreadPool(profile, replaceThreadFactoryIfNeeed(threadFactory));
    }
    
    private ThreadFactory replaceThreadFactoryIfNeeed(ThreadFactory threadFactory) {
    	s_logger.info("Creating ExecutorService... Override thread factory {} with {}", new Object[]{threadFactory, kapuaThreadFactory});
    	if (threadFactory instanceof CamelThreadFactory) {
			CamelThreadFactory camelThreadFactory = (CamelThreadFactory) threadFactory;
			String name = camelThreadFactory.getName();
			if (kapuaThreadFactory.threadPoolIsMatchingPattern(name)) {
				s_logger.info("Replace thread factory for thread factory name {} with KapuaThreadFactory implementation", name);
				return kapuaThreadFactory;
			}
			else {
				s_logger.info("Leave thread factory for thread factory name {} to its original implementation (CamelThreadFactory)", name);
				return threadFactory;
			}
		}
    	else {
    		s_logger.warn("Requested thread pool for a NOT CamelThreadFactory instance ({})! Leave the thread factory to its original implementation", threadFactory);
    		return threadFactory;
    	}
    }

}