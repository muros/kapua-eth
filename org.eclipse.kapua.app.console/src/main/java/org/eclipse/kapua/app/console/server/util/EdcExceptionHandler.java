package org.eclipse.kapua.app.console.server.util;

import org.eclipse.kapua.EdcSystemEventException;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaErrorCode;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.shared.GwtEdcErrorCode;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.service.rule.KapuaInvalidRuleQueryException;
import org.eclipse.kapua.service.user.EdcInvalidUsernamePasswordException;
import org.eclipse.kapua.service.user.EdcLockedUserException;
import org.eclipse.kapua.service.user.KapuaLastAdminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdcExceptionHandler {

    private static final Logger s_logger = LoggerFactory.getLogger(EdcExceptionHandler.class);

    public static void handle(Throwable t) throws GwtEdcException {
        if (t instanceof KapuaUnauthenticatedException) {

            // sessions has expired
            throw new GwtEdcException(GwtEdcErrorCode.UNAUTHENTICATED, t);
        } else if (t instanceof EdcInvalidUsernamePasswordException) {

            // bad username/password
            throw new GwtEdcException(GwtEdcErrorCode.INVALID_USERNAME_PASSWORD, t, ((EdcInvalidUsernamePasswordException) t).getRemainingLoginAttempts());
        } else if (t instanceof EdcLockedUserException) {

            throw new GwtEdcException(GwtEdcErrorCode.LOCKED_USER, t);
        } else if (t instanceof KapuaIllegalAccessException) {

            // not allowed
            throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ACCESS, t);
        } else if (t instanceof KapuaDuplicateNameException) {

            // duplicate name
            throw new GwtEdcException(GwtEdcErrorCode.DUPLICATE_NAME,
                                      t,
                                      ((KapuaDuplicateNameException) t).getDuplicateFieldName());
        } else if (t instanceof KapuaIllegalNullArgumentException) {

            // illegal null field value
            throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_NULL_ARGUMENT,
                                      t,
                                      ((KapuaIllegalArgumentException) t).getIllegalArgumentName());
        } else if (t instanceof KapuaIllegalArgumentException) {

            // illegal field value
            throw new GwtEdcException(GwtEdcErrorCode.ILLEGAL_ARGUMENT,
                                      t,
                                      ((KapuaIllegalArgumentException) t).getIllegalArgumentName());
        } else if (t instanceof KapuaLastAdminException) {

            // attempt to remove last account administrator
            throw new GwtEdcException(GwtEdcErrorCode.CANNOT_REMOVE_LAST_ADMIN, t, "administrator");
        } else if (t instanceof KapuaInvalidRuleQueryException) {

            // attempt to remove last account administrator
            throw new GwtEdcException(GwtEdcErrorCode.INVALID_RULE_QUERY, t, t.getLocalizedMessage());
        } else if (t instanceof EdcSystemEventException) {

            // the operation was completed but a system event delivery failed.
            throw new GwtEdcException(GwtEdcErrorCode.WARNING, t, t.getLocalizedMessage());
        } else if (t instanceof KapuaException && ((KapuaException) t).getCode().equals(KapuaErrorCode.OVER_RULE_LIMIT)) {
            throw new GwtEdcException(GwtEdcErrorCode.OVER_RULE_LIMIT, t, t.getLocalizedMessage());
        } else if (t instanceof KapuaException && ((KapuaException) t).getCode().equals(KapuaErrorCode.INTERNAL_ERROR)) {
            s_logger.error("internal service error", t);
            throw new GwtEdcException(GwtEdcErrorCode.INTERNAL_ERROR, t, t.getLocalizedMessage());
        } else {

            // all others => log and throw internal error code
            s_logger.warn("RPC service non-application error", t);
            throw GwtEdcException.internalError(t, t.getLocalizedMessage());
        }
    }
}
