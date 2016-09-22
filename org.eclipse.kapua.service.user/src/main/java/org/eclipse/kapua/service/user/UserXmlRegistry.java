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
package org.eclipse.kapua.service.user;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class UserXmlRegistry {

    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final UserFactory factory = locator.getFactory(UserFactory.class);
    
    public User newUser()
    {
        return factory.newUser();
    }

    public UserCreator newUserCreator()
    {
        return factory.newCreator(null, null);
    }

    public UserListResult newAccountListResult()
    {
        return factory.newUserListResult();
    }
}
