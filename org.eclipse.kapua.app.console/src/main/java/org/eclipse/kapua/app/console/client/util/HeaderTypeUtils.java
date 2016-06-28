package org.eclipse.kapua.app.console.client.util;

import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtHeader.GwtHeaderType;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.google.gwt.core.client.GWT;

public class HeaderTypeUtils {

    private static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);

    public static String format(GwtHeader header) {
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
