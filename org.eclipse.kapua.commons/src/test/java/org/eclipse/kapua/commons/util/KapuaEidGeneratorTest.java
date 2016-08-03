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
 *     Red Hat - improved test coverage
 *
 *******************************************************************************/
package org.eclipse.kapua.commons.util;

import static org.junit.Assert.*;

import org.eclipse.kapua.KapuaException;
import org.junit.Test;

public class KapuaEidGeneratorTest {

	@Test(expected = ExceptionInInitializerError.class)
	public void shouldNotInitializeIfProviderIsNotAvailable() throws KapuaException {
		assertNotNull(KapuaEidGenerator.generate());
	}

}
