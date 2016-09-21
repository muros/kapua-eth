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
package org.eclipse.kapua.test;

import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserQuery;

@TestService
public class UserFactoryMock implements UserFactory
{

    @Override
    public UserCreator newCreator(KapuaId scopedId, String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserQuery newQuery(KapuaId scopedId)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
