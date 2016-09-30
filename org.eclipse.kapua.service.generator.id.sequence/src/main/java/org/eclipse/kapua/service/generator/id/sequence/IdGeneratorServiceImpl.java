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
package org.eclipse.kapua.service.generator.id.sequence;

import java.math.BigInteger;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.generator.id.IdGeneratorService;

public class IdGeneratorServiceImpl implements IdGeneratorService
{
    private static long seed = System.currentTimeMillis();

    @Override
    public synchronized KapuaId generate()
        throws KapuaException
    {
        return new KapuaEid(BigInteger.valueOf(seed++));
    }
}
