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
package org.eclipse.kapua.app.console.server.util;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.shared.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.shared.GwtKapuaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KapuaExceptionHandler {

    private static final Logger s_logger = LoggerFactory.getLogger(KapuaExceptionHandler.class);

    public static void handle(Throwable t)
            throws GwtKapuaException {
        if (t instanceof KapuaUnauthenticatedException) {

            // sessions has expired
            throw new GwtKapuaException(GwtKapuaErrorCode.UNAUTHENTICATED, t);
        }

        else if (t instanceof KapuaException && ((KapuaException) t).getCode().equals(KapuaErrorCodes.INTERNAL_ERROR)) {
            s_logger.error("internal service error", t);
            throw new GwtKapuaException(GwtKapuaErrorCode.INTERNAL_ERROR, t, t.getLocalizedMessage());
        } else {

            // all others => log and throw internal error code
            s_logger.warn("RPC service non-application error", t);
            throw GwtKapuaException.internalError(t, t.getLocalizedMessage());
        }
    }
}
