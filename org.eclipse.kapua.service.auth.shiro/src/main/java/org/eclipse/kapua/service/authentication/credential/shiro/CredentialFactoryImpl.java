package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialType;

public class CredentialFactoryImpl implements CredentialFactory
{
    public CredentialCreatorImpl newCreator(KapuaId scopeId, KapuaId userId, CredentialType credentialType, String credentialKey)
    {
        return new CredentialCreatorImpl(scopeId, userId, credentialType, credentialKey);
    }
}
