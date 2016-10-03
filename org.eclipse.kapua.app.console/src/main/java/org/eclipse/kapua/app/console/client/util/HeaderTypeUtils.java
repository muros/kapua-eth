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
package org.eclipse.kapua.app.console.client.util;

import org.eclipse.kapua.app.console.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtHeader.GwtHeaderType;

import com.google.gwt.core.client.GWT;

public class HeaderTypeUtils
{

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    public static String format(GwtHeader header)
    {
        GwtHeaderType type = GwtHeaderType.valueOf(header.getType());
        switch (type) {
            case FLOAT:
                return MSGS.floatType();
            case INTEGER:
                return MSGS.integerType();
            case DOUBLE:
                return MSGS.doubleType();
            case LONG:
                return MSGS.longType();
            case BOOLEAN:
                return MSGS.booleanType();
            case BYTE_ARRAY:
                return MSGS.byteArrayType();
            default:
            case STRING:
                return MSGS.stringType();
        }

    }
}
