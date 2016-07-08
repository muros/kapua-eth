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
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaNamedEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

/**
 * AccountService exposes APIs to manage Account objects. It includes APIs to create, update, find, list and delete Accounts.
 * Instances of the AccountService can be acquired through the ServiceLocator object.
 * 
 */
public interface AccountService extends KapuaEntityService<Account, AccountCreator>,
                                KapuaUpdatableEntityService<Account>,
                                KapuaNamedEntityService<Account>,
                                KapuaConfigurableService
{
    // FIXME - Add java doc
    public Account find(KapuaId accountId)
        throws KapuaException;

    // FIXME - Add java doc
    public AccountListResult findChildsRecursively(KapuaId accountId)
        throws KapuaException;
}
