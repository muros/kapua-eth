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
package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountQuery;

public class AccountFactoryImpl implements AccountFactory
{
    @Override
    public AccountCreator newAccountCreator(KapuaId scopeId, String name)
    {
        return new AccountCreatorImpl(scopeId, name);
    }

    @Override
    public AccountQuery newQuery(KapuaId scopeId)
    {
        return new AccountQueryImpl(scopeId);
    }
}
