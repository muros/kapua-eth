package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.KapuaErrorCode;

public enum KapuaConfigurationErrorCodes implements KapuaErrorCode {
    
    INTERNAL_ERROR,
    ILLEGAL_ARGUMENT,
    OPERATION_NOT_ALLOWED,
    CONFIGURATION_ATTRIBUTE_INVALID,
    CONFIGURATION_REQUIRED_ATTRIBUTE_MISSING;
}
