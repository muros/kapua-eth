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
package org.eclipse.kapua;

import org.eclipse.kapua.model.id.KapuaId;

public class KapuaEntityNotFoundException extends KapuaException
{
	private static final long serialVersionUID = -4903038247732490215L;

	/**
     * KapuaEntityNotFoundException is thrown when a entity could not be loaded using the name provided.
     * 
     * @param entityType
     * @param entityName
     */
    public KapuaEntityNotFoundException(String entityType, String entityName)
    {
    	super(KapuaErrorCodes.ENTITY_NOT_FOUND, new Object[] { entityType, entityName });
    }

    /**
     * KapuaEntityNotFoundException is thrown when a entity could not be loaded using the id provided.
     * 
     * @param entityType
	 * @param entityId
	 */
    public KapuaEntityNotFoundException(String entityType, KapuaId entityId)
    {
    	super(KapuaErrorCodes.ENTITY_NOT_FOUND, new Object[] { entityType, entityId.getId() });
    }
}
