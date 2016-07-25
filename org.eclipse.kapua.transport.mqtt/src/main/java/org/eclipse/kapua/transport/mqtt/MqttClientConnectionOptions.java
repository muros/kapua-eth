package org.eclipse.kapua.transport.mqtt;

import java.net.URI;

import org.eclipse.kapua.transport.TransportClientConnectOptions;

public class MqttClientConnectionOptions implements TransportClientConnectOptions
{
    private String clientId;
    private String username;
    private char[] password;
    private URI    endpointURI;

    @Override
    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    @Override
    public String getUsername()
    {
        return username;
    }

    @Override
    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public char[] getPassword()
    {
        return password;
    }

    @Override
    public void setPassword(char[] password)
    {
        this.password = password;
    }

    @Override
    public URI getEndpointURI()
    {
        return endpointURI;
    }

    @Override
    public void setEndpointURI(URI endpointURI)
    {
        this.endpointURI = endpointURI;
    }

}
