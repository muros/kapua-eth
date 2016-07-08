package org.eclipse.kapua.service.authentication.shiro.credential;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authentication.Credential;
import org.eclipse.kapua.service.user.User;

public class KapuaSimpleAuthenticationInfo extends SimpleAuthenticationInfo
{
    private static final long serialVersionUID = -8682457531010599453L;

    private User              user;
    private Credential        credential;
    private Account           account;

    public KapuaSimpleAuthenticationInfo(User user,
                                         Credential credential,
                                         Account account,
                                         String realmName)
    {
        super(user.getName(),
              credential.getCredentialKey(),
              realmName);
        this.user = user;
        this.credential = credential;
        this.account = account;
    }

    public User getUser()
    {
        return user;
    }

    public Account getAccount()
    {
        return account;
    }

}