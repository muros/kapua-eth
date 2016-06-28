package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.model.id.KapuaId;

public interface KapuaSession
{
    public AccessToken getTokenInfo();

    public KapuaId getScopeId();

    public KapuaId getCurrentUserId();

    public String getCurrentUsername();
}
