package org.eclipse.kapua.app.console.client.util;

import java.util.MissingResourceException;

import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.google.gwt.core.client.GWT;

public class MessageUtils {
    private static final ValidationMessages VMSGS = GWT.create(ValidationMessages.class);

    public static String get(String key) {
        try {
            return VMSGS.getString(key);
        } catch (MissingResourceException mre) {
            return "";
        }
    }

    public static String get(String key, Object... arguments) {
        try {
            String message = VMSGS.getString(key);
            if (arguments != null) {
                message = doFormat(message, arguments);
            }
            return message;
        } catch (MissingResourceException mre) {
            return "";
        }
    }

    private static String doFormat(String s, Object[] arguments) {
        // A very simple implementation of format
        int i = 0;
        while (i < arguments.length) {
            String delimiter = "{" + i + "}";
            while (s.contains(delimiter)) {
                s = s.replace(delimiter, String.valueOf(arguments[i]));
            }
            i++;
        }
        return s;
    }
}
