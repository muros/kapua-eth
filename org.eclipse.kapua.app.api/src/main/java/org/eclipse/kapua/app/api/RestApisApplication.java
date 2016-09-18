/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.app.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.UriConnegFilter;

public class RestApisApplication extends ResourceConfig {

	public RestApisApplication() throws JAXBException {
		packages("org.eclipse.kapua.app.api, org.eclipse.kapua.service.account, org.eclipse.kapua.service.account.internal");

		HashMap<String, MediaType> mappedMediaTypes = new HashMap<String, MediaType>();
		mappedMediaTypes.put("xml", MediaType.APPLICATION_XML_TYPE);
		mappedMediaTypes.put("json", MediaType.APPLICATION_JSON_TYPE);
		property(ServerProperties.MEDIA_TYPE_MAPPINGS, mappedMediaTypes);
		register(UriConnegFilter.class);
	}

	public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
		final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
		Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
		namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
		moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
		return moxyJsonConfig.resolver();
	}
}
