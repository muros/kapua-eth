/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat - improved tests coverage
 *
 *******************************************************************************/
package org.eclipse.kapua.locator.internal;

import static org.junit.Assert.*;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.guice.GuiceLocatorImpl;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.user.UserService;
import org.junit.Test;

public class GuiceLocatorImplTest {

	KapuaLocator locator = GuiceLocatorImpl.getInstance();

	@Test
	public void shouldLoadUserService()
	{
		UserService  us = locator.getService(UserService.class);
		System.out.println("User service loadaed:"+us);
		assertNotNull(us);
	}

	@Test
	public void shouldNotLoadNotRegisteredService() {
		MyService myService = locator.getService(MyService.class);
		assertNull(myService);
	}

	static interface MyService extends KapuaService {}

}