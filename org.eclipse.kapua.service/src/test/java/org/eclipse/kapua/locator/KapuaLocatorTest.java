/*******************************************************************************
 * Copyright (c) 2011, 2016 Red Hat and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package org.eclipse.kapua.locator;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.KapuaService;
import org.junit.Assert;
import org.junit.Test;

public class KapuaLocatorTest extends Assert {

    @Test
    public void shouldResolveLocatorNameFromSystemProperty() {
        // Given
        try {
            String givenClassName = TestLocator.class.getName();
            System.setProperty("locator.impl.class", givenClassName);

            // When
            String className = KapuaLocator.locatorClassName();

            // Then
            assertEquals(givenClassName, className);
        } finally {
            System.clearProperty("locator.impl.class");
        }
    }

    static class TestLocator extends KapuaLocator {
        @Override
        public <S extends KapuaService> S getService(Class<S> serviceClass) {
            return null;
        }

        @Override
        public <F extends KapuaEntityFactory> F getFactory(Class<F> factoryClass) {
            return null;
        }
    }

}
