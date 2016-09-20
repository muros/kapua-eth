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

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.metric.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Kapua executor {@link ThreadFactory} implementation
 *
 */
public class KapuaExecutorThreadFactory implements ThreadFactory {

	private static final Logger logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);
	
	//metrics
  	private static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
	private Counter metricThreadCreationRequest;

	private final static AtomicInteger COUNTER = new AtomicInteger();
	private ThreadGroup tdg;
	private String factoryStringId;
	private final String name;
	private final List<String> threadPoolNameMatcher;
	private boolean useLocalShiro;

	public KapuaExecutorThreadFactory(String name, List<String> threadPoolNameMatcher, boolean rootThreadGroup) {
		this.name = name;
		this.threadPoolNameMatcher = threadPoolNameMatcher;
		String threadFactoryClassName = KapuaExecutorThreadFactory.class.getName();
		threadFactoryClassName = threadFactoryClassName.substring(threadFactoryClassName.lastIndexOf('.') + 1);

		ThreadGroup currentTg = Thread.currentThread().getThreadGroup();
		if (rootThreadGroup) {
			while (currentTg.getParent() != null) {
				//without this check thread group will be system (but it is better if its parent is the main group, not the system group)
				//At the top of the figure's structure is the system thread group.
				//The JVM-created system group organizes JVM threads that deal with object finalization and other system tasks, and serves as the root thread group of an application's hierarchical thread-group structure.
				//Just below system is the JVM-created main thread group, which is system's subthread group (subgroup, for short).
				//main contains at least one thread—the JVM-created main thread that executes byte-code instructions in the main() method.
				if (currentTg.getParent().getParent() != null) {
					currentTg = currentTg.getParent();
				} 
				else {
					break;
				}
			}
		}
		tdg = new ThreadGroup(currentTg, name);
		
		factoryStringId = new StringBuilder().append(threadFactoryClassName)
				.append("[")
				.append(name)
				.append("]_")
				.append(hashCode()).toString();
		logger.info("Created {} - [{}]", new Object[]{threadFactoryClassName, factoryStringId});
		
		//TODO add configurations
		//read settings
		//for the meaning of this flag please see the KapuaRunnableWrapper
		useLocalShiro = true;
		
		logger.info("Configurations: ");
		logger.info("Use local Apache Shiro: {}", useLocalShiro);
		
		metricThreadCreationRequest = metricsService.getCounter("kapua_executor_thread_factory", "thread_creation_request", "count");
	}

	public Thread newThread(Runnable runnable) {
		metricThreadCreationRequest.inc();
		String threadName = new StringBuilder().append(name)
				.append("_")
				.append(COUNTER.incrementAndGet()).toString();
		logger.info("Instantiate new thread {} from thread {} ...", new Object[]{threadName, Thread.currentThread().getName()});
		Thread answer = new Thread(tdg, new KapuaRunnableWrapper(runnable, threadName), threadName);
		answer.setDaemon(false);
		logger.info("Instantiate new thread {} -> [{}] DONE", threadName, answer);
		return answer;
	}
	
	/**
	 * Check if the thread pool name starts with one of the reserved words (threadPoolNameMatcher).
	 * In that case the {@link KapuaExecutorThreadPoolFactory} returns a different thread factory object.
	 * @param threadPoolName
	 * @return
	 */
	public boolean threadPoolIsMatchingPattern(String threadPoolName) {
		if (threadPoolName!=null) {
			for (String str : threadPoolNameMatcher) {
				logger.info("Check the thread pool name [{}] for prefix [{}]", new Object[]{threadPoolName, str});
				if (threadPoolName.startsWith(str)) {
					logger.info("Thread pool name [{}] matching the prefix [{}]", new Object[]{threadPoolName, str});
					return true;
				}
			}
		}
		return false;
	}
	
	public String getName() {
		return name;
	}

	public String toString() {
		return factoryStringId;
	}

}