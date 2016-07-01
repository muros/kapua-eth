package org.eclipse.kapua.broker.core.threadfactory;

import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.RuntimeCamelException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.metrics.MetricsService;
import org.eclipse.kapua.broker.core.metrics.internal.MetricsServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Kapua executor {@link ThreadFactory} implementation
 * It bind the {@link Subject} to the Runnable object
 *
 */
public class KapuaExecutorThreadFactory implements ThreadFactory {

	private static final Logger s_logger = LoggerFactory.getLogger(KapuaExecutorThreadFactory.class);

	//metrics
	//TODO get the metric service through the service locator
  	private final static MetricsService metricsService = MetricsServiceBean.getInstance();
	private Counter metricThreadCreationRequest;
	private Counter metricThreadCreationError;

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
		s_logger.info("Created {} - [{}]", new Object[]{threadFactoryClassName, factoryStringId});
		
		metricThreadCreationRequest = metricsService.getCounter("kapua_executor_thread_factory", "thread_creation_request", "count");
		metricThreadCreationError   = metricsService.getCounter("kapua_executor_thread_factory", "thread_creation_error", "count");
	}

	public Thread newThread(Runnable runnable) {
		metricThreadCreationRequest.inc();
		String threadName = new StringBuilder().append(name)
				.append("_")
				.append(COUNTER.incrementAndGet()).toString();
//		try {
			s_logger.info("Instantiate thread name [{}]... Shiro login in progress...", threadName);
//			Subject subject;
//			subject = SubjectUtils.loginKapuaSysWithDefaultSecurityManager();
			s_logger.info("Instantiate thread name [{}]... Shiro login in progress... DONE", threadName);
			Thread answer = new Thread(tdg, runnable, threadName);
			answer.setDaemon(false);
			s_logger.info("Created thread [{}] -> [{}]", threadName, answer);
//			subject.associateWith(runnable);
			s_logger.info("Shiro Subject associated with thread [{}] -> [{}]", threadName, answer);
			return answer;
//		}
//		catch (KapuaException e) {
//			metricThreadCreationError.inc();
//			s_logger.error("Instantiate thread name [{}]... Shiro login in progress... ERROR {}", new Object[]{threadName, e.getMessage()}, e);
//			throw new RuntimeCamelException("Cannot perform shiro login! Error " + e.getMessage(), e);
//		}
	}
	
	public boolean isMatchingPattern(String threadPoolName) {
		if (threadPoolName!=null) {
			for (String str : threadPoolNameMatcher) {
				s_logger.info("Check the thread pool name [{}] for prefix [{}]", new Object[]{threadPoolName, str});
				if (threadPoolName.startsWith(str)) {
					s_logger.info("Thread pool name [{}] matching the prefix [{}]", new Object[]{threadPoolName, str});
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