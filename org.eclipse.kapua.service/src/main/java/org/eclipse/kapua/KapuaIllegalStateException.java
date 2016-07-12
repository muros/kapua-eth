package org.eclipse.kapua;

public class KapuaIllegalStateException extends KapuaRuntimeException
{
    private static final long serialVersionUID = -912672615903975466L;

    public KapuaIllegalStateException(String message)
    {
        super(KapuaErrorCodes.ILLEGAL_STATE, null, message);
    }

}
