package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

public class GwtUpdateList extends EdcBaseModel implements Serializable {

    private static final long serialVersionUID = -7953082858219194327L;

    public GwtUpdateList() {}

    public String getCurrentVersion() {
        return get("currentVersion");
    }

    public void setCurrentVersion(String currentVersion) {
        set("currentVersion", currentVersion);
    }

    public String getVersion() {
        return get("version");
    }

    public void setVersion(String version) {
        set("version", version);
    }

    public String getRequisite() {
        return get("requisite");
    }

    public void setRequisite(String requisite) {
        set("requisite", requisite);
    }
}
