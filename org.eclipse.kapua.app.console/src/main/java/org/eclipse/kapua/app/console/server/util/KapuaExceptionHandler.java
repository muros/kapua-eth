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
        // else if (t instanceof KapuaInvalidUsernamePasswordException) {
        //
        // // bad username/password
        // throw new GwtEdcException(GwtEdcErrorCode.INVALID_USERNAME_PASSWORD, t, ((EdcInvalidUsernamePasswordException) t).getRemainingLoginAttempts());
        // }
        // else if (t instanceof EdcLockedUserException) {
        //
        // throw new GwtEdcException(GwtEdcErrorCode.LOCKED_USER, t);
        // }
        // else if (t instanceof KapuaIllegalAccessException) {
        //
        // // not allowed
        // throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ACCESS, t);
        // }
        // else if (t instanceof KapuaDuplicateNameException) {
        //
        // // duplicate name
        // throw new GwtEdcException(GwtEdcErrorCode.DUPLICATE_NAME,
        // t,
        // ((KapuaDuplicateNameException) t).getDuplicateFieldName());
        // }
        // else if (t instanceof KapuaIllegalNullArgumentException) {
        //
        // // illegal null field value
        // throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_NULL_ARGUMENT,
        // t,
        // ((KapuaIllegalArgumentException) t).getIllegalArgumentName());
        // }
        // else if (t instanceof KapuaIllegalArgumentException) {
        //
        // // illegal field value
        // throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ARGUMENT,
        // t,
        // ((KapuaIllegalArgumentException) t).getIllegalArgumentName());
        // }
        // else if (t instanceof KapuaLastAdminException) {
        //
        // // attempt to remove last account administrator
        // throw new GwtEdcException(GwtEdcErrorCode.CANNOT_REMOVE_LAST_ADMIN, t, "administrator");
        // }
        // else if (t instanceof KapuaInvalidRuleQueryException) {
        //
        // // attempt to remove last account administrator
        // throw new GwtEdcException(GwtEdcErrorCode.INVALID_RULE_QUERY, t, t.getLocalizedMessage());
        // }
        // else if (t instanceof EdcSystemEventException) {
        //
        // // the operation was completed but a system event delivery failed.
        // throw new GwtEdcException(GwtEdcErrorCode.WARNING, t, t.getLocalizedMessage());
        // }
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
