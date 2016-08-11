package org.eclipse.kapua.service.authentication.shiro;

import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.test.AccountFactoryMock;
import org.eclipse.kapua.test.AccountServiceMock;
import org.eclipse.kapua.test.AuthenticationServiceMock;
import org.eclipse.kapua.test.AuthorizationServiceMock;
import org.eclipse.kapua.test.PermissionFactoryMock;
import org.eclipse.kapua.test.TestLocatorImpl;
import org.eclipse.kapua.test.UserFactoryMock;
import org.eclipse.kapua.test.UserServiceMock;
import org.eclipse.kapua.test.UsernamePasswordTokenFactoryMock;

public class AuthShiroLocatorTest extends TestLocatorImpl
{
    public AuthShiroLocatorTest()
    {
        this.addServiceMock(AccountService.class, new AccountServiceMock());
        this.addServiceMock(UserService.class, new UserServiceMock());
        
        this.addFactoryMock(AccountFactory.class, new AccountFactoryMock());
        this.addFactoryMock(UserFactory.class, new UserFactoryMock());
    }
}
