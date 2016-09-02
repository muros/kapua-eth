package org.eclipse.kapua.translator.kura.kapua;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.service.device.call.kura.KuraMethod;
import org.eclipse.kapua.service.device.management.KapuaMethod;

public class MethodDictionaryKuraKapua
{
    private static Map<KuraMethod, KapuaMethod> dictionary;

    static {
        dictionary = new HashMap<>();

        dictionary.put(KuraMethod.GET, KapuaMethod.READ);
        dictionary.put(KuraMethod.POST, KapuaMethod.CREATE);
        dictionary.put(KuraMethod.PUT, KapuaMethod.WRITE);
        dictionary.put(KuraMethod.DEL, KapuaMethod.DELETE);
        dictionary.put(KuraMethod.EXEC, KapuaMethod.EXECUTE);
    }

    public static KapuaMethod get(KuraMethod kapuaMethod)
    {
        return dictionary.get(kapuaMethod);
    }
}
