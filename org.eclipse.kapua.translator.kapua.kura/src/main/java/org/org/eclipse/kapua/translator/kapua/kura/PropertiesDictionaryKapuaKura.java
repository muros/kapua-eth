package org.org.eclipse.kapua.translator.kapua.kura;

import org.eclipse.kapua.service.device.call.message.kura.proto.KuraPayloadProto.KuraPayload.KuraMetric;
import org.org.eclipse.kapua.service.device.management.command.internal.CommandProperties;

public class PropertiesDictionaryKapuaKura
{

    // private static Map<KapuaMethod, KuraMethod> dictionary;
    //
    // static {
    // dictionary = new HashMap<KapuaMethod, KuraMethod>();
    //
    // dictionary.put(KapuaMethod.READ, KuraMethod.GET);
    // dictionary.put(KapuaMethod.CREATE, KuraMethod.POST);
    // dictionary.put(KapuaMethod.WRITE, KuraMethod.PUT);
    // dictionary.put(KapuaMethod.DELETE, KuraMethod.DEL);
    // dictionary.put(KapuaMethod.EXECUTE, KuraMethod.EXEC);
    // }

    public static KuraMetric get(CommandProperties kapuaMethod)
    {
        return dictionary.get(kapuaMethod);
    }

}
