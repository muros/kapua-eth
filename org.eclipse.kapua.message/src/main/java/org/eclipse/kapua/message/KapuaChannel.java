package org.eclipse.kapua.message;

import java.util.List;

public interface KapuaChannel extends Channel
{
    public List<String> getSemanticParts();

    public void setSemanticParts(List<String> semanticParts);
}
