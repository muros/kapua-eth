/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.shiro.utils.AuthenticationUtils;

public class CredentialDAO extends ServiceDAO
{

    public static Credential create(EntityManager em, CredentialCreator credentialCreator)
        throws KapuaException
    {
        //
        // Create User
        CredentialImpl credentialImpl = new CredentialImpl(credentialCreator.getScopeId(),
                                                           credentialCreator.getUserId(),
                                                           credentialCreator.getCredentialType(),
                                                           AuthenticationUtils.cryptCredential(credentialCreator.getCredentialPlainKey()));

        return ServiceDAO.create(em, credentialImpl);
    }

    public static Credential update(EntityManager em, Credential credential)
        throws KapuaException
    {
        //
        // Update user
        CredentialImpl credentialImpl = (CredentialImpl) credential;

        return ServiceDAO.update(em, CredentialImpl.class, credentialImpl);
    }

    public static void delete(EntityManager em, KapuaId credentialId)
    {
        ServiceDAO.delete(em, CredentialImpl.class, credentialId);
    }

    public static Credential find(EntityManager em, KapuaId credentialId)
    {
        return em.find(CredentialImpl.class, credentialId);
    }

    public static CredentialListResult query(EntityManager em, KapuaQuery<Credential> credentialQuery)
        throws KapuaException
    {
        return ServiceDAO.query(em, Credential.class, CredentialImpl.class, new CredentialListResultImpl(), credentialQuery);
    }

    public static long count(EntityManager em, KapuaQuery<Credential> credentialQuery)
        throws KapuaException
    {
        return ServiceDAO.count(em, Credential.class, CredentialImpl.class, credentialQuery);
    }

}
