package org.org.eclipse.kapua.client.pool.internal.utils;

import java.util.Random;

public class MqttClientIdGenerator
{
    private static MqttClientIdGenerator instance          = new MqttClientIdGenerator();
    private static String                generatedIdFormat = "%s-d%-%d";

    private Random                       random;

    private MqttClientIdGenerator()
    {
        random = new Random();
    }

    public static MqttClientIdGenerator getInstance()
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
