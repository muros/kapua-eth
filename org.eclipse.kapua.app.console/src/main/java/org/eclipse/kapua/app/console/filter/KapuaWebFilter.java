package org.eclipse.kapua.app.console.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;

/**
 * {@link ShiroFilter} override. Used to intercept the http request execute and plug into thread context the Kapua session
 *
 */
public class KapuaWebFilter extends ShiroFilter {
	
	protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain)
            throws IOException, ServletException {
		//bind kapua session
		
		//TODO workaround to fix the null kapua session on webconsole requests. to be removed and substitute with getToken or another solution?
		Subject shiroSubject = SecurityUtils.getSubject();
        KapuaSession kapuaSession = (KapuaSession)shiroSubject.getSession().getAttribute("KapuaSession");
        KapuaSecurityUtils.setSession(kapuaSession);
		
        super.executeChain(request, response, origChain);

        //unbind kapua session
        KapuaSecurityUtils.clearSession();
    }

}