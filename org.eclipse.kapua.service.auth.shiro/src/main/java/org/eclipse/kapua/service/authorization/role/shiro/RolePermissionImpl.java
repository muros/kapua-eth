package org.eclipse.kapua.service.authorization.role.shiro;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.shiro.PermissionImpl;

@Entity(name = "RolePermission")
@Table(name = "athz_role_permission")
public class RolePermissionImpl extends AbstractKapuaEntity implements RolePermission
{
    private static final long serialVersionUID = -4107313856966377197L;

    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "role_id"))
    })
    private KapuaEid          roleId;

    @Embedded
    private PermissionImpl    permission;

    public RolePermissionImpl()
    {
        super();
    }

    public RolePermissionImpl(KapuaId scopeId, String domain, Actions action, KapuaId targetScopeId)
    {
        super(scopeId);
        setPermission(new PermissionImpl(domain, action, targetScopeId));
    }

    @Override
    public void setRoleId(KapuaId roleId)
    {
        if (roleId != null) {
            this.roleId = new KapuaEid(roleId.getId());
        }
    }

    @Override
    public KapuaId getRoleId()
    {
        return roleId;
    }

    @Override
    public void setPermission(Permission permission)
    {
        this.permission = new PermissionImpl(permission.getDomain(),
                                             permission.getAction(),
                                             permission.getTargetScopeId());
    }

    @Override
    public Permission getPermission()
    {
        return permission;
    }

    public String toString()
    {
        return permission.toString();
    }
}
