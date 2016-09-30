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
package org.eclipse.kapua.service.authorization.user.role;

import java.util.Set;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.role.Role;

public interface UserRoles extends KapuaEntity
{
    public static final String TYPE = "userRole";

    default public String getType()
    {
        return TYPE;
    }

    public void setUserId(KapuaId userId);

    public KapuaId getUserId();

    public void setRoles(Set<Role> roles);

    public Set<Role> getRoles();
}
