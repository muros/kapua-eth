package org.eclipse.kapua.broker.core.threadfactory;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;
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
				} else {
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
		
		metricThreadCreationRequest = metricsService.getCounter("kapua_executor_thread_factory", "thread_creation_request", "count");
	}

	public Thread newThread(Runnable runnable) {
		metricThreadCreationRequest.inc();
		String threadName = new StringBuilder().append(name)
				.append("_")
				.append(COUNTER.incrementAndGet()).toString();
		logger.info("Instantiate thread name [{}]...", threadName);
		Thread answer = new Thread(tdg, new RunnableWrapper(runnable), threadName);
		answer.setDaemon(false);
		logger.info("Instantiate thread name [{}] -> [{}] DONE", threadName, answer);
		return answer;
	}
	
	public boolean isMatchingPattern(String threadPoolName) {
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