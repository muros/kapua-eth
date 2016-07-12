package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface CredentialFactory extends KapuaObjectFactory
{
    public CredentialCreator newInstance(KapuaId scopeId, KapuaId id, CredentialType password, String rawPassword);

}
