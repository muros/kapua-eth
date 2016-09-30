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
package org.eclipse.kapua.transport.mqtt;

import java.net.URI;

import org.eclipse.kapua.transport.TransportClientConnectOptions;

public class MqttClientConnectionOptions implements TransportClientConnectOptions
{
    private String clientId;
    private String username;
    private char[] password;
    private URI    endpointURI;

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

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public char[] getPassword()
    {
        return password;
    }

    @Override
    public void setPassword(char[] password)
    {
        this.password = password;
    }

    @Override
    public URI getEndpointURI()
    {
        return endpointURI;
    }

    @Override
    public void setEndpointURI(URI endpointURI)
    {
        this.endpointURI = endpointURI;
    }

}
