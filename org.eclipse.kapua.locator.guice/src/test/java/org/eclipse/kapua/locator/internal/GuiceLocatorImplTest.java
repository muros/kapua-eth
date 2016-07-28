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
 *
 *******************************************************************************/
package org.eclipse.kapua.locator.internal;

import static org.junit.Assert.*;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.guice.GuiceLocatorImpl;
import org.eclipse.kapua.service.user.UserService;
import org.junit.Test;

public class GuiceLocatorImplTest {

	@Test
	public void test() 
	{
		KapuaLocator kl = GuiceLocatorImpl.getInstance();
		UserService  us = kl.getService(UserService.class);
		System.out.println("User service loadaed:"+us);
		assertNotNull(us);
	}

}
