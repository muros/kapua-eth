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
