package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.commons.model.query.predicate.AbstractKapuaQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountQuery;

public class AccountQueryImpl extends AbstractKapuaQuery<Account> implements AccountQuery
{
    private AccountQueryImpl()
    {
        super();
    }

    public AccountQueryImpl(KapuaId scopeId)
    {
        this();
        setScopeId(scopeId);
    }

}
