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
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

public class MetricColumn<T> {
    private T m_metric;
    private Long m_timestamp;
    private String m_row;
    private Class<T> m_class;


    public T get_metric() {
        return m_metric;
    }
    public void set_metric(T m_metric) {
        this.m_metric = m_metric;
    }
    public Long get_timestamp() {
        return m_timestamp;
    }
    public void set_timestamp(Long m_timestamp) {
        this.m_timestamp = m_timestamp;
    }
    public String get_row() {
        return m_row;
    }
    public void set_row(String m_row) {
        this.m_row = m_row;
    }
    public Class<T> get_class() {
        return m_class;
    }
    public void set_class(Class<T> m_class) {
        this.m_class = m_class;
    }

}
