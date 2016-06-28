/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
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
package org.eclipse.kapua.commons.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DataConfiguration;

public abstract class AbstractKapuaConfig<K extends KapuaConfigKey>
{
    protected DataConfiguration config;

    protected AbstractKapuaConfig(Configuration config)
    {
        this.config = new DataConfiguration(config);
    }

    public <T> T get(Class<T> cls, K key)
    {
        return config.get(cls, key.key());
    }

    public <T> T get(Class<T> cls, K key, T defaultValue)
    {
        return config.get(cls, key.key(), defaultValue);
    }

    public <T> List<T> getList(Class<T> cls, K key)
    {
        return config.getList(cls, key.key());
    }

    public <V> Map<String, V> getMap(Class<V> valueType, K prefixKey, String regex)
    {
        Map<String, V> map = new HashMap<String, V>();
        Configuration subsetConfig = config.subset(prefixKey.key());
        for (Iterator<String> it = subsetConfig.getKeys(); it.hasNext();) {
            String key = it.next();
            if (Pattern.matches(regex, key)) {
                map.put(key, ((DataConfiguration) subsetConfig).get(valueType, key));
            }
        }
        return map;
    }

    public <V> Map<String, V> getMap(Class<V> valueType, K prefixKey)
    {
        Map<String, V> map = new HashMap<String, V>();
        Configuration subsetConfig = config.subset(prefixKey.key());
        for (Iterator<String> it = subsetConfig.getKeys(); it.hasNext();) {
            String key = it.next();
            map.put(key, ((DataConfiguration) subsetConfig).get(valueType, key));
        }
        return map;
    }

    public int getInt(K key)
    {
        return config.getInt(key.key());
    }

    public boolean getBoolean(K key)
    {
        return config.getBoolean(key.key());
    }

    public String getString(K key)
    {
        return config.getString(key.key());
    }

    public long getLong(K key)
    {
        return config.getLong(key.key());
    }

    public float getFloat(K key)
    {
        return config.getFloat(key.key());
    }

    public double getDouble(K key)
    {
        return config.getDouble(key.key());
    }
}
