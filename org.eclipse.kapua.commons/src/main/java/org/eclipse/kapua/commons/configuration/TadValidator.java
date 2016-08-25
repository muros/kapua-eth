package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.model.config.metatype.Tad;

public class TadValidator
{
    /**
     * @param tad
     * @param value
     * @return null if no validation is present
     *         "" if no problem is detected
     *         "..." if the validation has failed return a localized string (not empty)
     */
    public static String validate(Tad tad, String value)
    {
        // FIXME Provide validation implementation
        return null; // null means no validation
    }
}
