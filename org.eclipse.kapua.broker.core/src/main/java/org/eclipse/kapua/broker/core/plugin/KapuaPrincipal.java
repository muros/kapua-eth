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
package org.eclipse.kapua.broker.core.plugin;

import java.security.Principal;

import org.apache.activemq.command.ConnectionId;

/**
 * Kapua {@link Principal} implementation
 *
 */
public class KapuaPrincipal implements Principal, java.io.Serializable {
    private static final long serialVersionUID = -3999313290528918167L;

    private String       name;
    private String       username;
    private Long         accountId;
    private String       clientId;
    private String       clientIp;
    private ConnectionId connectionId;

    /**
     * Create a KapuaPrincipal with the supplied name.
     */
    public KapuaPrincipal(String username, Long accountId, String clientId, String clientIp, ConnectionId connectionId) {
    	this.username     = username;
        this.accountId    = accountId;
        this.clientId     = clientId;
        this.clientIp     = clientIp;
        this.connectionId = connectionId;
        name = (new StringBuilder()).append(accountId != null ? accountId : "null").append(":").append(username).toString();
    }

    @Override
    public String getName() {
        return name;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public ConnectionId getConnectionId() {
        return connectionId;
    }

    public String getClientId() {
        return clientId;
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KapuaPrincipal other = (KapuaPrincipal) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
