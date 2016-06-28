package org.eclipse.kapua.app.console.shared.model;

public class GwtAccountStringListItem extends GwtStringListItem
{
    private static final long serialVersionUID = 2825662240165195455L;

    public void setHasChildAccount(boolean hasChildAccount)
    {
        set("hasChildAccount", hasChildAccount);
    }

    public boolean hasChildAccount()
    {
        return (Boolean) get("hasChildAccount");
    }
}
