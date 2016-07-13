package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface CredentialFactory extends KapuaObjectFactory
{
    public CredentialCreator newCreator(KapuaId scopeId, KapuaId userId, CredentialType credentialType, String credentialKey);

}
