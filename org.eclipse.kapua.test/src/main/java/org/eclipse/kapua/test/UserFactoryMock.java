package org.eclipse.kapua.test;

import org.eclipse.kapua.locator.guice.TestService;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserQuery;

@TestService
public class UserFactoryMock implements UserFactory
{

    @Override
    public UserCreator newCreator(KapuaId scopedId, String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserQuery newQuery(KapuaId scopedId)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
