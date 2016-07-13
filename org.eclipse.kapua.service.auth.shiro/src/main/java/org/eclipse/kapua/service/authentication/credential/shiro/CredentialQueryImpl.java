package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;

public class CredentialQueryImpl extends AbstractKapuaQuery<Credential> implements CredentialQuery
{
    private CredentialQueryImpl()
    {
        super();
    }

    public CredentialQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }
}
