package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

public interface CredentialFactory extends KapuaEntityFactory
{
    public CredentialCreator newInstance(KapuaId scopeId, KapuaId id, CredentialType password, String rawPassword);

}
