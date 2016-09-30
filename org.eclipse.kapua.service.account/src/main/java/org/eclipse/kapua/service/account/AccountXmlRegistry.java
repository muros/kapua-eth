/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
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
