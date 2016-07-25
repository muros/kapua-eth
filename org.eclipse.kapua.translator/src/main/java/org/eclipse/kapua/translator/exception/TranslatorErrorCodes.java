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
package org.eclipse.kapua.translator.exception;

import org.eclipse.kapua.KapuaErrorCode;

public enum TranslatorErrorCodes implements KapuaErrorCode
{
    INVALID_MESSAGE,

    INVALID_CHANNEL,
    INVALID_CHANNEL_CLASSIFIER,
    INVALID_CHANNEL_APP_NAME,
    INVALID_CHANNEL_APP_VERSION,

    INVALID_PAYLOAD,
}
