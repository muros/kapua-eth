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
package org.eclipse.kapua;

public enum KapuaRuntimeErrorCodes implements KapuaErrorCode
{
    SERVICE_LOCATOR_UNAVAILABLE,

    TRANSLATOR_NOT_FOUND,

    ENTITY_CREATOR_FACTORY_UNAVAILABLE,
    ENTITY_CREATOR_UNAVAILABLE,
    ENTITY_CREATION_ERROR,
}
