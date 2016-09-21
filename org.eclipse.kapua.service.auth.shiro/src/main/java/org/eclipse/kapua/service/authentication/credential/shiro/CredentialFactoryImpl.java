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
package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialType;

public class CredentialFactoryImpl implements CredentialFactory
{
    public CredentialCreatorImpl newCreator(KapuaId scopeId, KapuaId userId, CredentialType credentialType, String credentialKey)
    {
        return new CredentialCreatorImpl(scopeId, userId, credentialType, credentialKey);
    }
}
