/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication;

import java.security.Principal;

import org.eclipse.kapua.model.id.KapuaId;

/**
 * Kapua {@link Principal} implementation
 * TODO it's an object used by both authorization and authentication... should leave it in authentication module?
 *
 */
public interface KapuaPrincipal extends Principal, java.io.Serializable {

    public String getName();
    
    public String getTokenId();
    
    public KapuaId getUserId();

    public KapuaId getAccountId();

    public String getClientIp();

    public String getClientId();

}
