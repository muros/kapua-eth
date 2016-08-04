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
package org.eclipse.kapua.service.user.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.util.EntityManager;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserListResult;

public class UserDAO extends ServiceDAO
{
    public static User create(EntityManager em, UserCreator userCreator)
        throws KapuaException
    {
        //
        // Create User
        UserImpl userImpl = new UserImpl(userCreator.getScopeId(),
                                         userCreator.getName());

        userImpl.setDisplayName(userCreator.getDisplayName());
        userImpl.setEmail(userCreator.getEmail());
        userImpl.setPhoneNumber(userCreator.getPhoneNumber());

        return ServiceDAO.create(em, userImpl);
    }

    public static User update(EntityManager em, User user)
        throws KapuaException
    {
        //
        // Update user
        UserImpl userImpl = (UserImpl) user;

        return ServiceDAO.update(em, UserImpl.class, userImpl);
    }

    public static void delete(EntityManager em, KapuaId userId)
    {
        ServiceDAO.delete(em, UserImpl.class, userId);
    }

    public static User find(EntityManager em, KapuaId userId)
    {
        return em.find(UserImpl.class, userId);
    }

    public static User findByName(EntityManager em, String name)
    {
        return ServiceDAO.findByName(em, UserImpl.class, name);
    }

    public static UserListResult query(EntityManager em, KapuaQuery<User> userPermissionQuery)
        throws KapuaException
    {
        return ServiceDAO.query(em, User.class, UserImpl.class, new UserListResultImpl(), userPermissionQuery);
    }

    public static long count(EntityManager em, KapuaQuery<User> userPermissionQuery)
        throws KapuaException
    {
        return ServiceDAO.count(em, User.class, UserImpl.class, userPermissionQuery);
    }

}
