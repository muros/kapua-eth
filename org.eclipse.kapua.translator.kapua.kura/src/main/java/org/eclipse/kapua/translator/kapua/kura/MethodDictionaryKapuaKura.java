package org.eclipse.kapua.translator.kapua.kura;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.service.device.call.kura.KuraMethod;
import org.eclipse.kapua.service.device.management.KapuaMethod;

public class MethodDictionaryKapuaKura
{
    private static Map<KapuaMethod, KuraMethod> dictionary;

    static {
        dictionary = new HashMap<>();

        dictionary.put(KapuaMethod.READ, KuraMethod.GET);
        dictionary.put(KapuaMethod.CREATE, KuraMethod.POST);
        dictionary.put(KapuaMethod.WRITE, KuraMethod.PUT);
        dictionary.put(KapuaMethod.DELETE, KuraMethod.DEL);
        dictionary.put(KapuaMethod.EXECUTE, KuraMethod.EXEC);
    }

    public static KuraMethod get(KapuaMethod kapuaMethod)
    {
        return dictionary.get(kapuaMethod);
    }
}
