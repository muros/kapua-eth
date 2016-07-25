package org.eclipse.kapua.transport;

import java.net.URI;

public interface TransportClientConnectOptions
{
    public String getClientId();

    public void setClientId(String clientId);

    public String getUsername();

    public void setUsername(String username);

    public char[] getPassword();

    public void setPassword(char[] password);

    public URI getEndpointURI();

    public void setEndpointURI(URI endpontURI);

    // public X509Certificate getCertificate();
    //
    // public void setCertificate(X509Certificate certificate);
}
