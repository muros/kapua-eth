package org.eclipse.kapua.test;

import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.Organization;

@TestService
public class AccountFactoryMock implements AccountFactory
{

    @Override
    public AccountCreator newAccountCreator(KapuaId scopeId, String name)
    {
        AccountCreatorMock accountCreatorMock = new AccountCreatorMock();
        accountCreatorMock.setScopeId(scopeId);
        accountCreatorMock.setName(name);
        return accountCreatorMock;
    }

    @Override
    public AccountQuery newQuery(KapuaId scopeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public Account newAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Organization newOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountListResult newAccountListResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
