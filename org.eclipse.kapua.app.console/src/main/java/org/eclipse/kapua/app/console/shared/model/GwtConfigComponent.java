package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GwtConfigComponent extends EdcBaseModel implements Serializable {

    private static final long serialVersionUID = -6388356998309026758L;

    private List<GwtConfigParameter> m_parameters;

    public GwtConfigComponent() {
        m_parameters = new ArrayList<GwtConfigParameter>();
    }

    public String getComponentId() {
        return get("componentId");
    }

    public String getUnescapedComponentId() {
        return getUnescaped("componentId");
    }

    public void setComponentId(String componentId) {
        set("componentId", componentId);
    }

    public String getComponentName() {
        return get("componentName");
    }

    public String getUnescapedComponentName() {
        return getUnescaped("componentName");
    }

    public void setComponentName(String componentName) {
        set("componentName", componentName);
    }

    public String getComponentDescription() {
        return get("componentDescription");
    }

    public void setComponentDescription(String componentDescription) {
        set("componentDescription", componentDescription);
    }

    public String getComponentIcon() {
        return get("componentIcon");
    }

    public void setComponentIcon(String componentIcon) {
        set("componentIcon", componentIcon);
    }

    public List<GwtConfigParameter> getParameters() {
        return m_parameters;
    }

    public void setParameters(List<GwtConfigParameter> parameters) {
        m_parameters = parameters;
    }

    public GwtConfigParameter getParameter(String id) {
        for (GwtConfigParameter param : m_parameters) {
            if (param.getId().equals(id)) {
                return param;
            }
        }
        return null;
    }
}
