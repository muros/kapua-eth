/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
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
