package org.eclipse.kapua.service.account;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class AccountXmlRegistry {

	private final KapuaLocator locator = KapuaLocator.getInstance();
	private final AccountFactory factory = locator.getFactory(AccountFactory.class);
	
	public Account newAccount()
	{
		return factory.newAccount();
	}

	public Organization newOrganization()
	{
		return factory.newOrganization();
	}

	public AccountCreator newAccountCreator()
	{
		return factory.newAccountCreator(null, null);
	}

	public AccountListResult newAccountListResult()
	{
		return factory.newAccountListResult();
	}
}
