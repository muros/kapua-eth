package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public interface CredentialCreator extends KapuaEntityCreator<Credential>
{
    public KapuaId getUserId();

    public CredentialType getCredentialType();

    public String getCredentialKey();
}
