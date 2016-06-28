package org.eclipse.kapua.app.console.shared.model;

import org.eclipse.kapua.app.console.client.util.ImageUtils;

import com.google.gwt.resources.client.ImageResource;

public class GwtEdcLanguage extends EdcBaseModel
{
    private static final long serialVersionUID = 8581623970869404902L;

    public GwtEdcLanguage(String id,
                          ImageResource flag,
                          String name)
    {
        setId(id);
        setFlag(flag);
        setFlagToString(flag);
        setName(name);
    }

    public String getId()
    {
        return (String) get("id");
    }

    public void setId(String id)
    {
        set("id", id);
    }

    public ImageResource getFlag()
    {
        return (ImageResource) get("flag");
    }

    public void setFlag(ImageResource flag)
    {
        set("flag", flag);
    }

    public void setFlagToString(ImageResource flag)
    {
        set("flagToString",
            ImageUtils.toHTML(getFlag(),
                              getId(),
                              16),
            false);
    }

    public String getName()
    {
        return (String) get("name");
    }

    public void setName(String name)
    {
        set("name", name);
    }

}
