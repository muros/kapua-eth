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
package org.eclipse.kapua.broker.core.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is needed by {@link EdcSecurityBrokerFilter} to handle a vm connection.<br>
 * Indeed this bundle is instantiated during the broker startup then if {@link EdcSecurityBrokerFilter} try to instantiate a connection receive an error from the broker. (the vm factory couldn't reach the broker)
 * Then this class is needed to instantiate only a connection to be useful for the filter when it need ({@link EdcSecurityBrokerFilter#addConnection(org.apache.activemq.broker.ConnectionContext, org.apache.activemq.command.ConnectionInfo) add connection} and {@link EdcSecurityBrokerFilter#removeConnection(org.apache.activemq.broker.ConnectionContext, org.apache.activemq.command.ConnectionInfo, Throwable) remove connection}).
 *
 * NOTE:
 * with virtual topic support the destinations are removed! The message destination will be coded inside send method!
 */
public class JmsAssistantProducerPool extends GenericObjectPool<JmsAssistantProducerWrapper> {
	
	private static Logger s_logger = LoggerFactory.getLogger(JmsAssistantProducerPool.class);
	
	public enum DESTINATIONS {
		EDC_SERVICE,
		/**
		 * To be used to send messages without known destination.
		 * Otherwise the inactive monitor will not be able to remove the destination because it has a producer!
		 */
		NO_DESTINATION
	}
	
	private static Map<DESTINATIONS, JmsAssistantProducerPool> pools;
	
	static {
		pools = new HashMap<JmsAssistantProducerPool.DESTINATIONS, JmsAssistantProducerPool>();
		s_logger.info("Create pools for broker assistants (amq server instance)");
		s_logger.info("Create Service pool...");
		//TODO parameter to be added to configuration
//		pools.put(DESTINATIONS.EDC_SERVICE, 
//				new JmsAssistantProducerPool(new JmsAssistantProducerWrapperFactory(KapuaEnvironmentConfig.getInstance().getString(KapuaEnvironmentConfigKeys.SERVICE_QUEUE_NAME))));
		pools.put(DESTINATIONS.EDC_SERVICE, 
				new JmsAssistantProducerPool(new JmsAssistantProducerWrapperFactory("KapuaService")));
		s_logger.info("Create NoDestination pool...");
		pools.put(DESTINATIONS.NO_DESTINATION, 
				new JmsAssistantProducerPool(new JmsAssistantProducerWrapperFactory(null)));
		s_logger.info("Create pools... done.");
	}
	
	protected JmsAssistantProducerPool(JmsAssistantProducerWrapperFactory factory) {
		super(factory);
		//TODO parameter to be added to configuration
//        int totalMaxSize = KapuaEnvironmentConfig.getInstance().getString(KapuaEnvironmentConfigKeys.POOL_TOTAL_MAX_SIZE);
//        int maxSize = KapuaEnvironmentConfig.getInstance().getString(KapuaEnvironmentConfigKeys.POOL_MAX_SIZE);
//        int minSize = KapuaEnvironmentConfig.getInstance().getString(KapuaEnvironmentConfigKeys.POOL_MIN_SIZE);
		int totalMaxSize = 25;
        int maxSize = 25;
        int minSize = 10;
        
        GenericObjectPoolConfig jmsPoolConfig = new GenericObjectPoolConfig();
        jmsPoolConfig.setMaxTotal(totalMaxSize);
        jmsPoolConfig.setMaxIdle(maxSize);
        jmsPoolConfig.setMinIdle(minSize);
        s_logger.info("Set test on return to true for JmsAssistantProducerPool");
        jmsPoolConfig.setTestOnReturn(true);
        s_logger.info("Set test on borrow to true for JmsAssistantProducerPool");
        jmsPoolConfig.setTestOnBorrow(true);
        s_logger.info("Set block when exausted to true for JmsAssistantProducerPool");
        jmsPoolConfig.setBlockWhenExhausted(true);

        setConfig(jmsPoolConfig);
	}
	
	public static JmsAssistantProducerPool getIOnstance(DESTINATIONS destination) {
		return pools.get(destination);
	}
	
	public static void closePools() {
		if (pools!=null) {
			s_logger.info("Close Service pool...");
			pools.get(DESTINATIONS.EDC_SERVICE).close();
			s_logger.info("Close NoDestination pool...");
			pools.get(DESTINATIONS.NO_DESTINATION).close();
			s_logger.info("Close pools... done.");
		}
		else {
			s_logger.warn("Cannot close producer pools... Pools not initialized!");
		}
	}
	
}