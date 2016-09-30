/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.app.api.v1.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.app.api.v1.resources.model.ErrorBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractKapuaResource 
{
    private static final Logger s_logger = LoggerFactory.getLogger(AbstractKapuaResource.class);
    
    protected <T> T returnNotNullEntity(T entity) {
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return entity;
    }

    protected void handleException(Throwable t) {

        WebApplicationException wae = null;
        
        
        // TODO manage exceptions
        // ...
        ///////
        
    	s_logger.error("Internal Error", t);
        wae = newWebApplicationException(t, Response.Status.INTERNAL_SERVER_ERROR);

        s_logger.debug("Error Processing Request", t);
        if (wae != null) {
            throw wae;
        }
    }
  
    protected WebApplicationException newWebApplicationException(Throwable t, Response.Status status) 
    {
        String message = t.getMessage();

        Response response = Response.status(status).entity(new ErrorBean(status, message)).build();
        return new WebApplicationException(response);
    }
    
    protected WebApplicationException newWebApplicationException(Response.Status status, String message) 
    {
        Response response = Response.status(status).entity(new ErrorBean(status, message)).build();
        return new WebApplicationException(response);
    }
}
