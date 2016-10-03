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
package org.eclipse.kapua.service.authorization.permission;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.role.RolePermission;

public interface PermissionFactory extends KapuaObjectFactory
{
    public Permission newPermission(String domain, Actions action, KapuaId targetScopeId);

    public RolePermission newRolePermission(KapuaId scopeId, String domain, Actions action, KapuaId targetScopeId);

    public Permission parseString(String stringPermission)
        throws KapuaException;
}
