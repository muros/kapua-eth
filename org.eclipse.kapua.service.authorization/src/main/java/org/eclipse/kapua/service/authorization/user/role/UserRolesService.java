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
package org.eclipse.kapua.service.authorization.user.role;

import java.util.Set;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;

public interface UserRolesService extends KapuaEntityService<UserRoles, UserRolesCreator> {

    /**
     * @since 1.0
     */
    public UserRoles create(UserRolesCreator creator)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public UserRoles find(KapuaId accountId, KapuaId entityId)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public UserRolesListResult query(KapuaQuery<UserRoles> query)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public long count(KapuaQuery<UserRoles> query)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public void delete(UserRoles entity)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public UserRoles findByUserId(KapuaId scopeId, KapuaId userId)
            throws KapuaException;

    /**
     * @since 1.0
     */
    public UserRolesListResult merge(Set<UserRolesCreator> newPermissions)
            throws KapuaException;
}
