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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.KapuaService;

/**
 * AuthenticationService exposes APIs to manage User object under an Account.
 * It includes APIs to create, update, find, list and delete Users.
 * Instances of the UserService can be acquired through the ServiceLocator.
 */
public interface AuthenticationService extends KapuaService
{
    public AccessToken login(AuthenticationCredentials authenticationToken)
        throws KapuaException;

    public void logout()
        throws KapuaException;
    
    public AccessToken getToken(String tokenId) 
    	throws KapuaException;

}
