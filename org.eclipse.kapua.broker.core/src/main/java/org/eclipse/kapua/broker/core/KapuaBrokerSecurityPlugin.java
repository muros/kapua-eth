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
package org.eclipse.kapua.broker.core;

import java.net.URL;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.eclipse.kapua.broker.core.plugin.KapuaSecurityBrokerFilter;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Install {@link KapuaSecurityBrokerFilter} into amq filter chain plugin
 * 
 * Is called by amq broker by configuring plugin tag inside broker tag into activemq.xml
 * 
 * <plugins>
 *     <bean xmlns="http://www.springframework.org/schema/beans" id="edcFilter" class="com.eurotech.cloud.broker.EdcBrokerPlugin"/>  
 * </plugins>
 *
 */
public class KapuaBrokerSecurityPlugin implements BrokerPlugin 
{	        
	private static Logger s_logger = LoggerFactory.getLogger(KapuaBrokerSecurityPlugin.class);
	
	public KapuaBrokerSecurityPlugin() {
	}
	
    public Broker installPlugin(Broker broker) throws Exception 
    {            
    	s_logger.info(">> installPlugin {}", KapuaBrokerSecurityPlugin.class.getName());
        try {
        	//initialize shiro context for broker plugin from shiro ini file
            URL shiroIniUrl = getClass().getResource("/shiro.ini");
            String shiroIniStr = ResourceUtils.readResource(shiroIniUrl);
            Ini shiroIni = new Ini();
            shiroIni.load(shiroIniStr);

            IniSecurityManagerFactory factory = new IniSecurityManagerFactory(shiroIni);
  	      	org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
  	      	SecurityUtils.setSecurityManager(securityManager);
  	      	
      		// install the filters
      		broker = new KapuaSecurityBrokerFilter(broker);
            
            return broker;
        }
        catch (Throwable t) {
        	s_logger.error("Error in plugin installation.", t);
        	throw (SecurityException) new SecurityException(t);
        }
    }

}