/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;



public class KapuaMetricInfo<T> {

    public static final Class<Float>  TYPE_FLOAT   = Float.class;
    public static final Class<String>  TYPE_STRING  = String.class;
    public static final Class<Integer>  TYPE_INTEGER  = Integer.class;
    public static final Class<Double>  TYPE_DOUBLE  = Double.class;
    public static final Class<Long>  TYPE_LONG   = Long.class;
    public static final Class<Boolean>  TYPE_BOOLEAN  = Boolean.class;
    public static final Class<byte[]>  TYPE_BYTE_ARRAY = byte[].class;

    private String name;
    private Class<T> type;
    private T value;
    private String uuid;

    public KapuaMetricInfo() {
    }

    public KapuaMetricInfo(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        this.value = null;
        this.uuid = "";
    }

    public KapuaMetricInfo(String name, Class<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.uuid = "";
    }

    public KapuaMetricInfo(String name, Class<T> type, String uuid) {
        this.name = name;
        this.type = type;
        this.value = null;
        this.uuid = uuid;
    }

    public KapuaMetricInfo(String name, Class<T> type, T value, String uuid) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isOfType(Class<?> type) {
        if(this.type == type)
            return true;
        return false;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static Class<?> getType(String type) {
        if(type.equals(TYPE_FLOAT.toString())) {
            return TYPE_FLOAT;
        } else if(type.equals(TYPE_STRING.toString())) {
            return TYPE_STRING;
        } else if(type.equals(TYPE_INTEGER.toString())) {
            return TYPE_INTEGER;
        } else if(type.equals(TYPE_DOUBLE.toString())) {
            return TYPE_DOUBLE;
        } else if(type.equals(TYPE_LONG.toString())) {
            return TYPE_LONG;
        } else if(type.equals(TYPE_BOOLEAN.toString())) {
            return TYPE_BOOLEAN;
        } else if(type.equals(TYPE_BYTE_ARRAY.toString())) {
            return TYPE_BYTE_ARRAY;
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}