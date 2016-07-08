package org.eclipse.kapua.client.utils;

import java.util.Random;

public class KapuaClientIdGenerator
{
    private static KapuaClientIdGenerator instance          = new KapuaClientIdGenerator();
    private static String                generatedIdFormat = "%s-d%-%d";

    private Random                       random;

    private KapuaClientIdGenerator()
    {
        random = new Random();
    }

    public static KapuaClientIdGenerator getInstance()
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
        long random;
        synchronized (instance.random) {
            random = Math.abs(instance.random.nextLong());
        }

        return String.format(generatedIdFormat,
                             prefix,
                             timestamp,
                             random);
    }
}
