package org.eclipse.kapua.test;

import org.eclipse.kapua.service.authentication.UsernamePasswordToken;

public class UsernamePasswordTokenMock implements UsernamePasswordToken
{
    private String username;
    private char[] password;
    
    public UsernamePasswordTokenMock(String username, char[] password)
    {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public String getUsername()
    {
        return this.username;
    }

    @Override
    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public char[] getPassword()
    {
        return this.password;
    }

    @Override
    public void setPassword(char[] password)
    {
        this.password = password;
    }

}
