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
package org.eclipse.kapua.broker.core.plugin;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.security.AuthorizationMap;
import org.apache.activemq.security.SecurityContext;

/**
 * Kapua security context implementation of amq broker {@link SecurityContext}
 *
 */
public class KapuaSecurityContext extends SecurityContext {
    private KapuaPrincipal   principal;
    private Set<Principal>   principals;

    private AuthorizationMap authMap;
    private boolean          hasDataView;
    private boolean          hasDataManage;
    private boolean          hasDeviceView;
    private boolean          hasDeviceManage;

    public KapuaSecurityContext(KapuaPrincipal     principal,
                              AuthorizationMap authMap) {
        super(principal.getName());

        this.principal  = principal;
        principals = new HashSet<Principal>();
        principals.add(principal);

        this.authMap = authMap;
    }

    public Principal getMainPrincipal() {
        return principal;
    }

    public Set<Principal> getPrincipals() {
        return principals;
    }

    public AuthorizationMap getAuthorizationMap() {
        return authMap;
    }

    public boolean hasDataView() {
        return hasDataView;
    }

    public boolean hasDataManage() {
        return hasDataManage;
    }

    public boolean hasDeviceView() {
        return hasDeviceView;
    }

    public boolean hasDeviceManage() {
        return hasDeviceManage;
    }

}