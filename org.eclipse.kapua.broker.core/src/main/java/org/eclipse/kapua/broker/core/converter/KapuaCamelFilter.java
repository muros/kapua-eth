package org.eclipse.kapua.broker.core.converter;

import org.apache.camel.Exchange;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.listener.AbstractListener;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;

public class KapuaCamelFilter extends AbstractListener
{
    public KapuaCamelFilter()
    {
        super("filter");
    }

    public void bindSession(Exchange exchange, Object value) throws KapuaException
    {
        Subject subject = exchange.getIn().getHeader(MessageConstants.HEADER_KAPUA_SHIRO_SUBJECT, Subject.class);
        KapuaSecurityUtils.setSession((KapuaSession) subject.getSession().getAttribute(KapuaSession.KAPUA_SESSION_KEY));
        ThreadContext.bind(subject);
    }

    public void unbindSession(Exchange exchange, Object value) throws KapuaException
    {
        ThreadContext.unbindSubject();
        KapuaSecurityUtils.clearSession();
    }

}
