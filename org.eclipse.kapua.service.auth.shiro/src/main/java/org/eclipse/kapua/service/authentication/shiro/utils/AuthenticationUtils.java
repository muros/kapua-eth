package org.eclipse.kapua.service.authentication.shiro.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.service.authentication.shiro.KapuaAuthenticationErrorCodes;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class AuthenticationUtils
{
    public static String cryptCredential(String plainValue)
        throws KapuaException
    {
        //
        // Argument validator
        ArgumentValidator.notEmptyOrNull(plainValue, "plainValue");

        //
        // Do crypt
        String cryptedValue = null;
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            String salt = BCrypt.gensalt(12, random);
            cryptedValue = BCrypt.hashpw(plainValue, salt);
        }
        catch (NoSuchAlgorithmException e) {
            throw new KapuaRuntimeException(KapuaAuthenticationErrorCodes.CREDENTIAL_CRYPT_ERROR, e, (Object[]) null);
        }

        return cryptedValue;
    }
}
