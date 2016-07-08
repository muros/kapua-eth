package org.eclipse.kapua.commons.security;

import java.util.concurrent.Callable;

public class KapuaSecurityUtils
{
    public static String                           MDC_USERNAME  = "username";

    private static final ThreadLocal<KapuaSession> threadSession = new ThreadLocal<KapuaSession>();

    public static KapuaSession getSession()
    {
        return threadSession.get();
    }

    public static void setSession(KapuaSession session)
    {
        threadSession.set(session);
    }

    public static void clearSession()
    {
        threadSession.remove();
    }

    public static <T> T doPriviledge(Callable<T> privilegedAction)
        throws Exception
    {
        T result = null;

        KapuaSession session = getSession();

        boolean created = false;
        if (session == null) {
            session = new KapuaSession();
            setSession(session);
            created = true;
        }
        session.setTrustedMode(true);
        try {
            result = privilegedAction.call();
        }
        finally {
            session.setTrustedMode(false);

            if (created) {
                clearSession();
                session = null;
            }
        }

        return result;
    }

}
