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
package org.eclipse.kapua.locator.internal;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class KapuaModule extends AbstractModule 
{
	private static final Logger s_logger = LoggerFactory.getLogger(KapuaModule.class);
	
	private static final String SERVICE_RESOURCE = "locator.services";
	private static final String COMMENT_PREFIX   = "#";
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void configure() 
	{
		BufferedReader br = null;
		try {
			
			URL servicesUrl = ResourceUtils.getResource(SERVICE_RESOURCE);
			String services = ResourceUtils.readResource(servicesUrl);
			br = new BufferedReader( new StringReader(services));		
			
			String serviceNameTrimmed = null; 
			for (String serviceName = br.readLine(); serviceName != null; serviceName = br.readLine()) {
				
				serviceNameTrimmed = serviceName.trim(); 
				if (serviceNameTrimmed.length() == 0 || serviceNameTrimmed.startsWith(COMMENT_PREFIX)) {
					continue;
				}
				try {
					Class<?> kapuaObject = Class.forName(serviceNameTrimmed);
					if (KapuaService.class.isAssignableFrom(kapuaObject)) {

						bind(kapuaObject).toProvider(new KapuaServiceLoaderProvider(kapuaObject));
						s_logger.info("Bound Kapua service {}", serviceNameTrimmed);
					}
					else if (KapuaObjectFactory.class.isAssignableFrom(kapuaObject)) {

						bind(kapuaObject).toProvider(new KapuaFactoryLoaderProvider(kapuaObject));
						s_logger.info("Bound Kapua factory {}", serviceNameTrimmed);
					}
				}
				catch (Exception e) {
					s_logger.error("Cannot load Kapua service/factory "+serviceNameTrimmed, e);
				}
			}
		}
		catch (Exception e) {
			throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, 
											"Cannot load "+SERVICE_RESOURCE, 
											e);
		}
		finally {
			if (br != null) try { br.close(); } catch (Exception e) {}
		}
	}
}
