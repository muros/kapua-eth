package org.eclipse.kapua.test;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RolePermission;

public class PermissionFactoryMock implements PermissionFactory
{

    @Override
    public Permission newPermission(String domain, Actions action, KapuaId targetScopeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RolePermission newRolePermission(KapuaId scopeId, String domain, Actions action, KapuaId targetScopeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Permission parseString(String stringPermission)
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
