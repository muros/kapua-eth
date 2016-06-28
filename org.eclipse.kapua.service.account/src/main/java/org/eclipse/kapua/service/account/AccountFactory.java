/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.account;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.config.KapuaConfigEntityFactory;

public interface AccountFactory extends KapuaConfigEntityFactory<Configuration, ConfigurationCreator>
{
    public AccountCreator newAccountCreator(KapuaId scopeId, String name);

    public AccountQuery newQuery(KapuaId scopeId);

    public Configuration newConfiguration(ConfigurationCreator creator);

    public ConfigurationCreator newConfigurationCreator(KapuaId scopeId);
}
