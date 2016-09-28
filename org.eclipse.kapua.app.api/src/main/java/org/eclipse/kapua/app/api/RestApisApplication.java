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
 *******************************************************************************/
package org.eclipse.kapua.app.api;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

public class RestApisApplication extends ResourceConfig {

	public RestApisApplication() throws JAXBException {
		packages("org.eclipse.kapua.app.api", "org.eclipse.kapua.service.account", "org.eclipse.kapua.service.account.internal",
		         "org.eclipse.kapua.service.user", "org.eclipse.kapua.service.user.internal");

		// Bind media type to resource extension
		HashMap<String, MediaType> mappedMediaTypes = new HashMap<String, MediaType>();
		mappedMediaTypes.put("xml", MediaType.APPLICATION_XML_TYPE);
		mappedMediaTypes.put("json", MediaType.APPLICATION_JSON_TYPE);
		
		property(ServerProperties.MEDIA_TYPE_MAPPINGS, mappedMediaTypes);
		register(UriConnegFilter.class);
		register(JaxbContextResolver.class);
		register(KapuaEntityBodyWriter.class);
		register(KapuaListResultBodyWriter.class);

		// Hook the swagger-ui
		registerClasses(ApiListingResource.class, 
						SwaggerSerializers.class);
	}
}
