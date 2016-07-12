package org.eclipse.kapua.service.device.call.message;

public enum KapuaResponseCode
{
    ACCEPTED(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_ERROR(500);

    private int code;

    KapuaResponseCode(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static KapuaResponseCode fromResponseCode(String responseCode)
    {
        return fromResponseCode(Integer.valueOf(responseCode));
    }

    public static KapuaResponseCode fromResponseCode(int responseCode)
    {
        KapuaResponseCode result = null;
        for (KapuaResponseCode krc : KapuaResponseCode.values()) {
            if (krc.getCode() == responseCode) {
                result = krc;
                break;
            }
        }

        return result;
    }
}
