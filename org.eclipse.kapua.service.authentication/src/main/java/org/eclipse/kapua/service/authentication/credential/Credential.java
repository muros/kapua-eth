package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;

public interface Credential extends KapuaEntity
{
    public static final String TYPE = "credential";

    default public String getType()
    {
        return TYPE;
    }

    public KapuaId getUserId();

    public CredentialType getCredentialType();

    public String getCredentialKey();
}
