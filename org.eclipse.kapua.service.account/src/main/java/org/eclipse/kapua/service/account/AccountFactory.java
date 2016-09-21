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

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface AccountFactory extends KapuaObjectFactory
{
    public AccountCreator newAccountCreator(KapuaId scopeId, String name);
    
    public Account newAccount();
    
    public Organization newOrganization();

    public AccountQuery newQuery(KapuaId scopeId);
    
    public AccountListResult newAccountListResult();
}
