package org.eclipse.kapua.service.authentication.credential;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

public interface CredentialService extends KapuaEntityService<Credential, CredentialCreator>, KapuaUpdatableEntityService<Credential>
{
    public CredentialListResult findByUserId(KapuaId scopeId, KapuaId userId)
        throws KapuaException;
}
