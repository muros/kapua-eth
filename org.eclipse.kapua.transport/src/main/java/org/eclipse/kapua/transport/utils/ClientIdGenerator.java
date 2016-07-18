package org.eclipse.kapua.transport.utils;

import java.util.Random;

public class ClientIdGenerator
{
    private static ClientIdGenerator instance          = new ClientIdGenerator();
    private static String            generatedIdFormat = "%s-d%-%d";

    private Random                   random;

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
