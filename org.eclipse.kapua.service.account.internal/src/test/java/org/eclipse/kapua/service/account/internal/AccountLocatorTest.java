package org.eclipse.kapua.service.account.internal;

import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.test.AuthenticationServiceMock;
import org.eclipse.kapua.test.AuthorizationServiceMock;
import org.eclipse.kapua.test.PermissionFactoryMock;
import org.eclipse.kapua.test.TestLocatorImpl;
import org.eclipse.kapua.test.UserServiceMock;
import org.eclipse.kapua.test.UsernamePasswordTokenFactoryMock;

public class AccountLocatorTest extends TestLocatorImpl
{
    public AccountLocatorTest()
    {
        this.addServiceMock(AuthenticationService.class, new AuthenticationServiceMock());
        this.addServiceMock(AuthorizationService.class, new AuthorizationServiceMock());
        this.addServiceMock(UserService.class, new UserServiceMock());
        
        this.addFactoryMock(UsernamePasswordTokenFactory.class, new UsernamePasswordTokenFactoryMock());
        this.addFactoryMock(PermissionFactory.class, new PermissionFactoryMock());
    }
}
