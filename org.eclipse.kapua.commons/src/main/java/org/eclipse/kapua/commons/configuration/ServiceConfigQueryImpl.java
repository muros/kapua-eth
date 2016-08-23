package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;

public class ServiceConfigQueryImpl extends AbstractKapuaQuery<ServiceConfig> implements ServiceConfigQuery
{

    private ServiceConfigQueryImpl()
    {
        super();
    }

    public ServiceConfigQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }
}
