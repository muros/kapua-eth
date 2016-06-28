package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class EdcBasePagingCursor extends BaseModel implements Serializable {

    private static final long serialVersionUID = 5814738657797246416L;

    public EdcBasePagingCursor() {
    }

    public Object getKeyOffset() {
        return get("keyOffset");
    }

    public void setKeyOffset(Object keyOffset) {
        set("keyOffset", keyOffset);
    }
}
