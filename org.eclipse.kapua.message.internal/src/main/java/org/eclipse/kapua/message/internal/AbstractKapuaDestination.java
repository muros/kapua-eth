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
package org.eclipse.kapua.message.internal;

import org.eclipse.kapua.message.KapuaDestination;

/**
 * Models a topic for messages posted to the EDC platform.
 * Topic are expected to be in the form of "account/asset/<application_specific>";
 * system topic starts with the $EDC account.
 */
public abstract class AbstractKapuaDestination implements KapuaDestination
{
    private String controlDestinationPrefix;
    private String scopeNamespace;
    private String clientId;

    public AbstractKapuaDestination(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public AbstractKapuaDestination(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        this.controlDestinationPrefix = controlDestinationPrefix;
        this.scopeNamespace = scopeNamespace;
        this.clientId = clientId;
    }

    @Override
    public String getControlDestinationPrefix()
    {
        return controlDestinationPrefix;
    }

    @Override
    public void setControlDestinationPrefix(String controlDestinationPrefix)
    {
        this.controlDestinationPrefix = controlDestinationPrefix;
    }

    @Override
    public String getScopeNamespace()
    {
        return scopeNamespace;
    }

    @Override
    public void setScopeNamespace(String scopeNamespace)
    {
        this.scopeNamespace = scopeNamespace;
    }

    @Override
    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }
}
