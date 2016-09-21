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
package org.eclipse.kapua.commons.security;

import java.io.Serializable;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AccessToken;

public class KapuaSession implements Serializable
{
    private static final long serialVersionUID = -3831904230950408142L;
    
    public final static String KAPUA_SESSION_KEY = "KapuaSession";

    private AccessToken       accessToken;
    private KapuaId           runAsScopeId;
    private KapuaId           scopeId;
    private KapuaId           userId;
    private String            username;

    private boolean           trustedMode      = false;

    public KapuaSession()
    {
        super();
    }
    
    /**
     * creates a {@link KapuaSession} copy with trusted mode flag set to false 
     * @param kapuaSession
     * @return
     */
    public static KapuaSession createFrom(KapuaSession kapuaSession) {
    	return new KapuaSession(kapuaSession.getAccessToken(),
    			kapuaSession.getRunAsScopeId(),
    			kapuaSession.getScopeId(),
    			kapuaSession.getUserId(),
    			kapuaSession.getUsername());
    }

    public KapuaSession(AccessToken accessToken,
                        KapuaId runAsScopeId,
                        KapuaId scopeId,
                        KapuaId userId,
                        String username)
    {
        this();
        this.accessToken = accessToken;
        this.runAsScopeId = runAsScopeId;
        this.scopeId = scopeId;
        this.userId = userId;
        this.username = username;
    }

    public AccessToken getAccessToken()
    {
        return accessToken;
    }

    public KapuaId getRunAsScopeId()
    {
        return runAsScopeId;
    }

    public KapuaId getScopeId()
    {
        return scopeId;
    }

    public KapuaId getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    final void setTrustedMode(boolean trustedMode)
    {
        this.trustedMode = trustedMode;
    }
    
    public final boolean isTrustedMode()
    {
        return trustedMode;
    }
}
