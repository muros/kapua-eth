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
package org.eclipse.kapua.transport.utils;

import java.util.Random;

public class ClientIdGenerator
{
    private static final ClientIdGenerator instance          = new ClientIdGenerator();
    private static final String            generatedIdFormat = "%s-%d-%d";

    private Random                         random;

    private ClientIdGenerator()
    {
        random = new Random();
    }

    public static ClientIdGenerator getInstance()
    {
        return instance;
    }

    public static String next()
    {
        return next("");
    }

    public static String next(String prefix)
    {
        long timestamp = System.currentTimeMillis();
        long randomNumber;
        synchronized (instance.random) {
            randomNumber = Math.abs(instance.random.nextLong());
        }

        return String.format(generatedIdFormat,
                             prefix,
                             timestamp,
                             randomNumber);
    }
}
