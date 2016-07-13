package org.eclipse.kapua.service.authentication.credential.shiro;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.commons.model.AbstractKapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialType;

public class CredentialCreatorImpl extends AbstractKapuaEntityCreator<Credential> implements CredentialCreator
{
    private static final long serialVersionUID = -5020680413729882095L;

    @XmlElement(name = "userId")
    private KapuaId           userId;

    @XmlElement(name = "credentialType")
    private CredentialType    credentialType;

    @XmlElement(name = "credentialKey")
    private String            credentialKey;

    public CredentialCreatorImpl(KapuaId scopeId, KapuaId userId, CredentialType credentialType, String credentialKey)
    {
        super(scopeId);

        this.userId = userId;
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
    public String getCredentialPlainKey()
    {
        return credentialKey;
    }

}
