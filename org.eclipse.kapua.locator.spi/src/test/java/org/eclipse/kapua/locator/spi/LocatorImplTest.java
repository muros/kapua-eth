/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - loading test services
 *
 *******************************************************************************/
package org.eclipse.kapua.locator.spi;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.KapuaService;
import org.junit.Assert;
import org.junit.Test;

public class LocatorImplTest {

    @Test
    public void shouldLoadService() {
        MyService service = KapuaLocator.getInstance().getService(MyService.class);
        Assert.assertNotNull(service);
    }

    @Test
    public void shouldLoadTestService() {
        MyTestableService service = KapuaLocator.getInstance().getService(MyTestableService.class);
        Assert.assertTrue(service instanceof TestMyTestableService);
    }

    // Tests classes

    interface MyService extends KapuaService {

    }

    public static class MyServiceImpl implements MyService {

    }

    interface MyTestableService extends KapuaService {

    }

    public static class MyTestableServiceImpl implements MyTestableService {

    }

    @TestService
    public static class TestMyTestableService implements MyTestableService {

    }

}
