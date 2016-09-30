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

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessageFactory;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.service.device.call.message.DevicePosition;

public class TranslatorKuraKapuaUtils
{
    public static KapuaPosition translate(DevicePosition kuraPosition)
    {
        KapuaPosition kapuaPosition = null;

        if (kuraPosition != null) {
            KapuaLocator locator = KapuaLocator.getInstance();
            KapuaMessageFactory kapuaMessageFactory = locator.getFactory(KapuaMessageFactory.class);

            kapuaPosition = kapuaMessageFactory.newPosition();
            kapuaPosition.setAltitude(kuraPosition.getAltitude());
            kapuaPosition.setHeading(kuraPosition.getHeading());
            kapuaPosition.setLatitude(kuraPosition.getLatitude());
            kapuaPosition.setLongitude(kuraPosition.getLongitude());
            kapuaPosition.setPrecision(kuraPosition.getPrecision());
            kapuaPosition.setSatellites(kuraPosition.getSatellites());
            kapuaPosition.setSpeed(kuraPosition.getSpeed());
            kapuaPosition.setStatus(kuraPosition.getStatus());
            kapuaPosition.setTimestamp(kuraPosition.getTimestamp());
        }

        return kapuaPosition;
    }
}
