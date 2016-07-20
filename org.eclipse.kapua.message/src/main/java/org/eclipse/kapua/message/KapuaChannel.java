package org.eclipse.kapua.message;

public interface KapuaChannel extends Channel
{
    public String getClientId();

    public void setClientId(String clientId);

    public String[] getSemanticParts();

    public void setSemanticParts(String[] semanticParts);
}
