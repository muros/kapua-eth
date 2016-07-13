package org.eclipse.kapua.service.authentication.credential.shiro;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialType;

public class CredentialImpl extends AbstractKapuaEntity implements Credential
{
    private static final long serialVersionUID = -7921424688644169175L;

    @XmlElement(name = "userId")
    @Embedded
    @AttributeOverrides({
                          @AttributeOverride(name = "eid", column = @Column(name = "user_id", updatable = false, nullable = false))
    })
    private KapuaEid          userId;

    @XmlElement(name = "credentialType")
    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type", updatable = false, nullable = false)
    private CredentialType    credentialType;

    @XmlElement(name = "credentialKey")
    @Basic
    @Column(name = "credential_key", updatable = false, nullable = false)
    private String            credentialKey;

    public CredentialImpl(KapuaId scopeId, KapuaId userId, CredentialType credentialType, String credentialKey)
    {
        super(scopeId);
        this.userId = (KapuaEid) userId;
        this.credentialType = credentialType;
        this.credentialKey = credentialKey;
    }

    @Override
    public KapuaId getUserId()
    {
        return userId;
    }

    @Override
    public CredentialType getCredentialType()
    {
        return credentialType;
    }

    @Override
    public String getCredentialKey()
    {
        return credentialKey;
    }

}
