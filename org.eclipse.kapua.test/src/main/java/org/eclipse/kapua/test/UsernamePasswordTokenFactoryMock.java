package org.eclipse.kapua.test;

import org.eclipse.kapua.service.authentication.UsernamePasswordToken;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;

public class UsernamePasswordTokenFactoryMock implements UsernamePasswordTokenFactory
{

    @Override
    public UsernamePasswordToken newInstance(String username, char[] password)
    {
        return new UsernamePasswordTokenMock(username, password);
    }

}
