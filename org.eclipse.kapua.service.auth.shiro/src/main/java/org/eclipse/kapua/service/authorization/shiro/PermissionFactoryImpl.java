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
package org.eclipse.kapua.service.authorization.shiro;

import java.math.BigInteger;
import java.util.StringTokenizer;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;

public class PermissionFactoryImpl implements PermissionFactory
{
    @Override
    public Permission newPermission(String domain, String action, KapuaId targetScopeId)
    {
        return new PermissionImpl(domain, action, targetScopeId);
    }

    @Override
    public Permission parseString(String stringPermission)
        throws KapuaException
    {
        StringTokenizer st = new StringTokenizer(stringPermission, ":");
        int iTokensCount = st.countTokens();
        if (iTokensCount < 1 || iTokensCount > 3) {
            throw new KapuaAuthorizationException(KapuaAuthorizationErrorCodes.INVALID_STRING_PERMISSION, null, stringPermission);
        }

        //
        // Build the new Permission
        String domain = st.nextToken();

        String action = null;
        if (iTokensCount > 1) {
            action = st.nextToken();
        }

        KapuaId scopeTargetId = null;
        if (iTokensCount > 2) {
            try {
                BigInteger kapuaId = new BigInteger(st.nextToken());
                scopeTargetId = new KapuaEid(kapuaId);
            }
            catch (IllegalArgumentException iae) {
                throw new KapuaAuthorizationException(KapuaAuthorizationErrorCodes.INVALID_STRING_PERMISSION, iae, stringPermission);
            }
        }

        Permission p = new PermissionImpl(domain, action, scopeTargetId);
        return p;
    }

}
