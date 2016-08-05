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

import org.eclipse.kapua.KapuaRuntimeException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class KapuaEidGeneratorTest extends Assert {

	// Connection to MariaDb sometime hangs instead of throwing exception and blocks build process.
	// We should find a more reliable way of testing this after build is stable.
	@Ignore
	@Test(expected = KapuaRuntimeException.class)
	public void shouldValidateThatGenerationIsNotPossible() {
		try {
			KapuaEidGenerator.generate();
		} catch (KapuaRuntimeException e) {
			assertEquals(e.getCode(), KapuaCommonsErrorCodes.ID_GENERATION_ERROR);
			return;
		}
		fail();
	}

}
