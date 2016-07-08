package org.eclipse.kapua.commons.security;

import java.io.Serializable;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.AccessToken;

public class KapuaSession implements Serializable
{
    private static final long serialVersionUID = -3831904230950408142L;

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

    public void setTrustedMode(boolean trustedMode)
    {
        this.trustedMode = trustedMode;
    }

    public boolean isTrustwedMode()
    {
        return trustedMode;
    }
}
