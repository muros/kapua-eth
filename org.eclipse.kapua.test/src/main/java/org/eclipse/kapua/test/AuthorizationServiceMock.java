package org.eclipse.kapua.test;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;

public class AuthorizationServiceMock implements AuthorizationService
{

    @Override
    public boolean isPermitted(Permission permission)
        throws KapuaException
    {
        // Always true
        return true;
    }

    @Override
    public void checkPermission(Permission permission)
        throws KapuaException
    {
        // Never thorws
    }

}
