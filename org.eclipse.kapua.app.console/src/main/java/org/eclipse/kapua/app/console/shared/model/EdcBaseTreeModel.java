package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

import org.eclipse.kapua.app.console.client.util.EdcSafeHtmlUtils;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class EdcBaseTreeModel extends BaseTreeModel implements Serializable {

    private static final long serialVersionUID = 167866061149596287L;

    public EdcBaseTreeModel() {
        super();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <X> X set(String name, X value) {
        if (value instanceof String) {
            value = (X) EdcSafeHtmlUtils.htmlEscape((String) value);
        }

        return super.set(name, value);
    }

    @SuppressWarnings({ "unchecked" })
    public <X> X getUnescaped(String property) {
        X value = super.get(property);

        if (value instanceof String) {
            value = (X) EdcSafeHtmlUtils.htmlUnescape((String) value);
        }

        return value;
    }
}
