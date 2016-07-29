package org.eclipse.kapua.service.authorization.user.role.shiro;

import java.math.BigInteger;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;

@Embeddable
public class RoleId implements KapuaId
{
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "role_id"))
    })
    private KapuaEid roleId;

    public RoleId()
    {
        super();
    }

    public RoleId(BigInteger id)
    {
        this();
        roleId = new KapuaEid(id);
    }

    @Override
    public BigInteger getId()
    {
        return roleId == null ? null : roleId.getId();
    }

}
