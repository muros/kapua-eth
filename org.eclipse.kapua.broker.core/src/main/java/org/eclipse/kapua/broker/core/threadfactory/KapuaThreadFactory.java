package org.eclipse.kapua.broker.core.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;

import com.codahale.metrics.Counter;

/**
 * Thread factory injected into {@link Executors} to allow to set the thread name (useful for log purpose)<br>
 * Thread name is composed by (name)_(Thread_number)
 * 
 */
public class KapuaThreadFactory implements ThreadFactory {
	
	//metrics
  	private final static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
	private Counter metricThreadCreationRequest;

	private String name;
	private ThreadGroup tdg;
	private AtomicInteger currentThreadIndex;

	public KapuaThreadFactory(String name, boolean rootThreadGroup) {
		this.name = name;
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
		currentThreadIndex = new AtomicInteger(-1);
		
		metricThreadCreationRequest = metricsService.getCounter("kapua_thread_factory", "thread_creation_request", "count");
	}

	public Thread newThread(Runnable r) {
		int id = currentThreadIndex.addAndGet(1);
		metricThreadCreationRequest.inc();
		return new Thread(tdg, r, new StringBuilder().append(name)
				.append("_")
				.append(id).toString());
	}
	
}