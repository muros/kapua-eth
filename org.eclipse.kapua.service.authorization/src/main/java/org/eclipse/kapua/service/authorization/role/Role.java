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
package org.eclipse.kapua.service.authorization.role;

import java.util.Set;

import org.eclipse.kapua.model.KapuaEntity;

public interface Role extends KapuaEntity
{
    public static final String TYPE = "role";

    default public String getType()
    {
        return TYPE;
    }

    public void setName(String name);

    public String getName();

    public void setPermissions(Set<RolePermission> permissions);

    public Set<RolePermission> getPermissions();
}
