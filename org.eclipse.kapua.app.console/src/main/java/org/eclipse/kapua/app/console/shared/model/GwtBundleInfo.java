package org.eclipse.kapua.app.console.shared.model;



import java.io.Serializable;

public class GwtBundleInfo extends EdcBaseModel implements Serializable {

    private static final long serialVersionUID = -7285859217584861659L;

    public void setName(String name) {
        set("name", name);
    }

    public String getName() {
        return (String) get("name");
    }

    public void setVersion(String version) {
        set("version", version);
    }

    public String getVersion() {
        return (String) get("version");
    }
}
