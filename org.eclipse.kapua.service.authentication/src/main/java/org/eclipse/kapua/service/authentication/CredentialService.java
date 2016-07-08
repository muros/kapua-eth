package org.eclipse.kapua.service.authentication;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;

public interface CredentialService extends KapuaEntityService<Credential, CredentialCreator>, KapuaUpdatableEntity
{
    public Credential findByUserId(KapuaId scopeId, KapuaId userId)
        throws KapuaException;
}
