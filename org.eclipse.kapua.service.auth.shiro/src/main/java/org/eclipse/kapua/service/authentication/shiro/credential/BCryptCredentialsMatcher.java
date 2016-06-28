package org.eclipse.kapua.service.authentication.shiro.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptCredentialsMatcher implements CredentialsMatcher
{

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo)
    {
        //
        // Token data
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String tokenUsername = token.getUsername();
        String tokenPassword = new String(token.getPassword());

        //
        // Info data
        SimpleAuthenticationInfo info = (SimpleAuthenticationInfo) authenticationInfo;
        String infoUsername = (String) info.getPrincipals().getPrimaryPrincipal();
        String infoPassword = (String) info.getCredentials();

        //
        // Match token with info
        boolean credentialMatch = false;
        if (tokenUsername.equals(infoUsername)) {
            if (BCrypt.checkpw(tokenPassword, infoPassword)) {
                credentialMatch = true;

                // FIXME: if true cache token password for authentication performance improvement
            }
        }

        return credentialMatch;
    }

}
