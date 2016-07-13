package org.eclipse.kapua.broker.core.threadfactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.apache.shiro.session.mgt.AbstractValidatingSessionManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;
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
  	private final static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
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
		s_logger.info("Created {} - [{}]", new Object[]{threadFactoryClassName, factoryStringId});
		
		metricThreadCreationRequest = metricsService.getCounter("kapua_executor_thread_factory", "thread_creation_request", "count");
	}

	public Thread newThread(Runnable runnable) {
		metricThreadCreationRequest.inc();
		String threadName = new StringBuilder().append(name)
				.append("_")
				.append(COUNTER.incrementAndGet()).toString();
		s_logger.info("Instantiate thread name [{}]... Shiro login in progress...", threadName);
		Subject subject;
		subject = tempLoginMethodToBeDeleted();
		s_logger.info("Instantiate thread name [{}]... Shiro login in progress... DONE", threadName);
		Thread answer = new Thread(tdg, runnable, threadName);
		answer.setDaemon(false);
		s_logger.info("Created thread [{}] -> [{}]", threadName, answer);
		subject.associateWith(runnable);
		s_logger.info("Shiro Subject associated with thread [{}] -> [{}]", threadName, answer);
		return answer;
	}
	
	//TODO remove this method with all shiro dependencies once the authorization token works fine
	private Subject tempLoginMethodToBeDeleted() {
		org.apache.shiro.mgt.SecurityManager securityManager = null;
		boolean forceCreation = false;
        try {
            securityManager = SecurityUtils.getSecurityManager();
        }
        catch (UnavailableSecurityManagerException e) {
            s_logger.warn("Handling UnavailableSecurityManagerException: {}", e.getMessage());
            forceCreation = true;
        }

        if (forceCreation) {
            s_logger.warn("Creating new DefaultSecurityManager");

            DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
            Collection<Realm> realms = new ArrayList<Realm>(); 
        	try {
    			realms.add(new org.eclipse.kapua.service.authentication.shiro.KapuaAuthenticatingRealm());
    			realms.add(new org.eclipse.kapua.service.authorization.shiro.KapuaAuthorizingRealm());
    		} catch (KapuaException e) {
    			//TODO add default realm???
    		}
            defaultSecurityManager.setRealms(realms);
            SecurityUtils.setSecurityManager(defaultSecurityManager);

            if (defaultSecurityManager.getSessionManager() instanceof AbstractSessionManager) {
                ((AbstractSessionManager) defaultSecurityManager.getSessionManager()).setGlobalSessionTimeout(-1);
                s_logger.info("Shiro global session timeout set to indefinite.");
            } else {
                s_logger.error("Cannot set Shiro global session timeout to indefinite.");
            }

            if (defaultSecurityManager.getSessionManager() instanceof AbstractValidatingSessionManager) {
                ((AbstractValidatingSessionManager) defaultSecurityManager.getSessionManager()).setSessionValidationSchedulerEnabled(false);
                s_logger.info("Shiro global session validator scheduler disabled.");
            } else {
                s_logger.error("Cannot disable Shiro session validator scheduler.");
            }

            securityManager = defaultSecurityManager;

        }

        PrincipalCollection principals = new SimplePrincipalCollection("kapua-sys", "kapuaRealm");

        Subject.Builder sb = new Subject.Builder(securityManager);
        sb = sb.authenticated(true)
               .principals(principals);

        Subject subject = sb.buildSubject();
        SubjectThreadState subjectThreadState = new SubjectThreadState(subject);
        subjectThreadState.bind();

        // Load account name
        String accountName = "kapua-sys";

        // update session
        Session session = subject.getSession();
        session.setAttribute("KAPUA_SESSION", new Serializable() {
		});
        // set infinite timeout
        session.setTimeout(-1);

        //
        return subject;
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