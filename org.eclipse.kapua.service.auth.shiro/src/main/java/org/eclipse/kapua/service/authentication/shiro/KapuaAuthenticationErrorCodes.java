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
package org.eclipse.kapua.service.authentication.shiro;

import org.eclipse.kapua.KapuaErrorCode;

public enum KapuaAuthenticationErrorCodes implements KapuaErrorCode
{
    INVALID_CREDENTIALS_TOKEN_PROVIDED,
    SUBJECT_ALREADY_LOGGED,
    INVALID_USERNAME,
    INVALID_CREDENTIALS,
    EXPIRED_CREDENTIALS,
    LOCKED_USERNAME,
    DISABLED_USERNAME,
    AUTHENTICATION_ERROR,

    CREDENTIAL_CRYPT_ERROR
}
