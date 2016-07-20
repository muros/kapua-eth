package org.eclipse.kapua.locator.internal;

import static org.junit.Assert.*;

import org.eclipse.kapua.locator.KapuaLocator;
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
