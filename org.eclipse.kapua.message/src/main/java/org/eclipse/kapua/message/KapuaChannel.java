package org.eclipse.kapua.message;

public interface KapuaChannel
{
    public String getMessageClassification();

    public void setMessageClassification(String messageClassification);

    public String getScope();

    public void setScope(String scope);

    public String getClientId();

    public void setClientId(String clientId);
}
