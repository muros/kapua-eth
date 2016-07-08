package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;

public interface Credential extends KapuaEntity
{
    public KapuaId getUserId();

    public CredentialType getCredentialType();

    public String getCredentialKey();
}
