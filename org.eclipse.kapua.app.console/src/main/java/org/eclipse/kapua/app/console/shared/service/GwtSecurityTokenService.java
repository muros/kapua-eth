package org.eclipse.kapua.app.console.shared.service;

import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * This is the XSRF service interface to obtain a valid securityToken.
 */
@RemoteServiceRelativePath("xsrf")
public interface GwtSecurityTokenService extends RemoteService {


    public GwtXSRFToken generateSecurityToken();


}