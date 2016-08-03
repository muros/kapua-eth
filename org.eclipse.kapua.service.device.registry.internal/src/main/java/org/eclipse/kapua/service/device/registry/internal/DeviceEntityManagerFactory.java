package org.eclipse.kapua.service.device.registry.internal;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;

public class DeviceEntityManagerFactory extends org.eclipse.kapua.commons.util.JpaUtils
{
    private static final String               PERSISTENCE_UNIT_NAME = "kapua-device";
    private static final String               DATASOURCE_NAME       = "kapua-dbpool";
    private static final Map<String, String>  s_uniqueConstraints   = new HashMap<>();

    private static DeviceEntityManagerFactory t                     = new DeviceEntityManagerFactory();

    private DeviceEntityManagerFactory()
    {
        super(PERSISTENCE_UNIT_NAME,
              DATASOURCE_NAME,
              s_uniqueConstraints);
    }

    public static EntityManager getEntityManager()
        throws KapuaException
    {
        return t.createEntityManager();
    }
}
