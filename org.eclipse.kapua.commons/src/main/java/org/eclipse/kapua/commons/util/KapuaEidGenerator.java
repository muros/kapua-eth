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
package org.eclipse.kapua.commons.util;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.model.id.KapuaEid;

public class KapuaEidGenerator
{
    private static final String NEW_UUID_SHORT = "SELECT UUID_SHORT()";

    public static KapuaEid generate()
    {
        KapuaEid id = null;
        EntityManager em = null;
        try {
            em = JpaUtils.getEntityManager();

            Query q = em.createNativeQuery(NEW_UUID_SHORT);
            BigInteger bigIntId = (BigInteger) q.getSingleResult();

            id = new KapuaEid(bigIntId);
        }
        catch (Exception pe) {
            throw new KapuaRuntimeException(KapuaCommonsErrorCodes.ID_GENERATION_ERROR, pe);
        }
        finally {
            JpaUtils.close(em);
        }

        return id;
    }
}
