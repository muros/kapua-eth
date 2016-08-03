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
` *     Red Hat - fixed class encapsulation, exception handling and contract
 *
 *******************************************************************************/
package org.eclipse.kapua.commons.util;

import java.math.BigInteger;

import org.eclipse.kapua.commons.model.id.KapuaEid;

public final class KapuaEidGenerator {

    private static final String NEW_UUID_SHORT = "SELECT UUID_SHORT()";

    private KapuaEidGenerator() {
    }

    /**
     * Generates new Kapua EID.
     *
     * @return new Kapua EID instance
     * @throws ExceptionInInitializerError if underlying persistence store cannot be initialized
     */
    public static KapuaEid generate() throws ExceptionInInitializerError {
        KapuaEid id = null;
        // try {
        // id = new KapuaEid(JpaUtils.generateUuidShort());
        id = new KapuaEid(new BigInteger(String.valueOf(System.currentTimeMillis())));
        // }
        // catch (KapuaException pe) {
        // throw new KapuaRuntimeException(KapuaCommonsErrorCodes.ID_GENERATION_ERROR, pe);
        // }

        try {
            Thread.sleep(1);
        }
        catch (Exception pe) {
            throw new KapuaRuntimeException(KapuaCommonsErrorCodes.ID_GENERATION_ERROR, pe);
        }
        finally {
            if(em != null) {
                JpaUtils.close(em);
        }
        }

        return id;
    }
}
