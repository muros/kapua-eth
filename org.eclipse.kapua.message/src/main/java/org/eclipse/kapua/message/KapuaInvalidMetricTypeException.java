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
package org.eclipse.kapua.message;

public class KapuaInvalidMetricTypeException extends Exception {

    private static final long serialVersionUID = 2030908612099893963L;

    private int    m_type;
    private String m_className;

    public KapuaInvalidMetricTypeException(int type) {
        m_type  = type;
    }


    public KapuaInvalidMetricTypeException(String className) {
        m_className = className;
    }


    /**
     * Returns the code for the unrecognized EdcHeader type.
     * @return
     */
    public int getValueTypeOrdinal() {
        return m_type;
    }


    /**
     * Returns the class for the unrecognized EdcHeader type.
     * @return
     */
    public String getValueTypeClassName() {
        return m_className;
    }
}
