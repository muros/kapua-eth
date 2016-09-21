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
package org.eclipse.kapua.locator.guice;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

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

	private static final String SERVICE_TEST_RESOURCE = "locator.test.services";

	private static final String COMMENT_PREFIX   = "#";
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void configure() 
	{
		BufferedReader br = null;
		try {
			List<URL> servicesDefinitions = Arrays.asList(ResourceUtils.getResource(SERVICE_RESOURCE), ResourceUtils.getResource(SERVICE_TEST_RESOURCE));

			for (URL servicesUrl : servicesDefinitions) {
				if (servicesUrl != null) {
					String services = ResourceUtils.readResource(servicesUrl);
					br = new BufferedReader(new StringReader(services));

					String trimmedServiceLine = null;
					for (String serviceName = br.readLine(); serviceName != null; serviceName = br.readLine()) {

						trimmedServiceLine = serviceName.trim();
						if (trimmedServiceLine.length() == 0 || trimmedServiceLine.startsWith(COMMENT_PREFIX)) {
							continue;
						}

						try {
							Class<?> kapuaObject = Class.forName(trimmedServiceLine);
							if (KapuaService.class.isAssignableFrom(kapuaObject)) {
								bind(kapuaObject).toProvider(new KapuaServiceLoaderProvider(kapuaObject));
								s_logger.info("Bound Kapua service {}", trimmedServiceLine);
							} else if (KapuaObjectFactory.class.isAssignableFrom(kapuaObject)) {
								bind(kapuaObject).toProvider(new KapuaFactoryLoaderProvider(kapuaObject));
								s_logger.info("Bound Kapua factory {}", trimmedServiceLine);
							}
						} catch (Exception e) {
							s_logger.error("Cannot load Kapua service/factory " + trimmedServiceLine, e);
						}
						 catch (Throwable e) {
								s_logger.error("Cannot load Kapua service/factory " + trimmedServiceLine, e);
						throw e;	
						 }
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, 
											"Cannot load "+SERVICE_RESOURCE, 
											e);
		}
		finally {
			if (br != null) try { br.close(); } catch (Exception e) {}
		}
	}

}
