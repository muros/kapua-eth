package org.eclipse.kapua.service.generator.id;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.KapuaService;

public interface IdGeneratorService extends KapuaService
{
    public KapuaId generate()
        throws KapuaException;
}
