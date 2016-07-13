package org.eclipse.kapua.message;

public interface KapuaDestination
{
    public String getControlDestinationPrefix();

    public void setControlDestinationPrefix(String controlDestinationPrefix);

    public String getScopeNamespace();

    public void setScopeNamespace(String scopeNamespace);

    public String getClientId();

    public void setClientId(String clientId);

    public String toClientDestination();

    public void fromClientDestination(String destination);
}
