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
 *
 *******************************************************************************/
package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

/**
 * Credential service definition.
 * 
 * @since 1.0
 *
 */
public interface CredentialService extends KapuaEntityService<Credential, CredentialCreator>, KapuaUpdatableEntityService<Credential>
{

    /**
     * Return the credential list result looking by user identifier (and also scope identifier)
     * 
     * @param scopeId
     * @param userId
     * @return
     * @throws KapuaException
     */
    public CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId)
        throws KapuaException;
}
