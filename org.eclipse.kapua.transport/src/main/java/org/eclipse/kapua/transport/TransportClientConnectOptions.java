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
package org.eclipse.kapua.transport;

import java.net.URI;

public interface TransportClientConnectOptions
{
    public String getClientId();

    public void setClientId(String clientId);

    public String getUsername();

    public void setUsername(String username);

    public char[] getPassword();

    public void setPassword(char[] password);

    public URI getEndpointURI();

    public void setEndpointURI(URI endpontURI);

    // public X509Certificate getCertificate();
    //
    // public void setCertificate(X509Certificate certificate);
}
