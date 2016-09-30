/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.kapua.app.console.client.util.KapuaSafeHtmlUtils;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.NestedModelUtil;
import com.extjs.gxt.ui.client.data.RpcMap;

public class KapuaBaseModel extends BaseModel implements Serializable
{
    private static final long serialVersionUID = -240340584288457781L;

    public KapuaBaseModel()
    {
        super();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <X> X get(String property)
    {
        //
        // ! IMPORTANT NOTE !
        //
        // For the fact that BaseModelData is doing weird things if the key contains '[' and ']'
        // we cannot call the super.get(...).
        //
        // The code that follows is copy-pasted from the BaseModelData.class,
        // except for the fact the logic on the square brackets is skipped.
        //
        if (allowNestedValues && NestedModelUtil.isNestedProperty(property)) {
            return (X) NestedModelUtil.getNestedValue(this, property);
        }
        if (map == null) {
            return null;
        }

        return (X) map.get(property);
    }

    @SuppressWarnings({ "unchecked" })
    public <X> X getUnescaped(String property)
    {
        X value = get(property);

        if (value instanceof String) {
            value = (X) KapuaSafeHtmlUtils.htmlUnescape((String) value);
        }

        return value;
    }

    @Override
    public <X> X set(String name, X value)
    {
        return set(name, value, true);
    }

    @SuppressWarnings({ "unchecked" })
    public <X> X set(String name, X value, boolean htmlEscape)
    {
        if (htmlEscape && value != null && value instanceof String) {
            value = (X) KapuaSafeHtmlUtils.htmlEscape(((String) value));
        }

        //
        // ! IMPORTANT NOTE !
        //
        // For the fact that BaseModelData is doing weird things if the key contains '[' and ']'
        // we cannot call the super.set(...).
        //
        // The code that follows is copy-pasted from the BaseModel.class and the BaseModelData.class,
        // except for the fact the logic on the square brackets is skipped.
        //
        if (allowNestedValues && NestedModelUtil.isNestedProperty(name)) {
            return (X) NestedModelUtil.setNestedValue(this, name, value);
        }
        if (map == null) {
            map = new RpcMap();
        }

        X oldValue = (X) map.put(name, value);
        notifyPropertyChanged(name, value, oldValue);
        return oldValue;
    }

    @Override
    public void setProperties(Map<String, Object> properties)
    {
        for (String property : properties.keySet()) {
            set(property, properties.get(property));
        }
    }

}
