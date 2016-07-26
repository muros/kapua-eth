/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
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
package org.eclipse.kapua.service.device.registry.event.internal;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;

public class DeviceEventDAO extends ServiceDAO
{

    public static DeviceEvent create(EntityManager em, DeviceEventCreator deviceEventCreator)
    {
        DeviceEvent deviceConnection = new DeviceEventImpl(deviceEventCreator.getScopeId());
        deviceConnection.setDeviceId(deviceEventCreator.getDeviceId());
        deviceConnection.setReceivedOn(deviceEventCreator.getReceivedOn());
        deviceConnection.setSentOn(deviceEventCreator.getSentOn());
        deviceConnection.setResource(deviceEventCreator.getResource());
        deviceConnection.setEventMessage(deviceEventCreator.getEventMessage());
        deviceConnection.setPosition(deviceEventCreator.getPosition());

        return ServiceDAO.create(em, deviceConnection);
    }

    public static DeviceEvent update(EntityManager em, DeviceEvent deviceConnection)
        throws KapuaException
    {
        DeviceEventImpl deviceConnectionImpl = (DeviceEventImpl) deviceConnection;
        return ServiceDAO.update(em, DeviceEventImpl.class, deviceConnectionImpl);
    }

    public static DeviceEvent find(EntityManager em, KapuaId deviceEventId)
    {
        return em.find(DeviceEventImpl.class, deviceEventId);
    }

    public static DeviceEventListResult query(EntityManager em, KapuaQuery<DeviceEvent> query)
        throws KapuaException
    {
        return ServiceDAO.query(em, DeviceEvent.class, DeviceEventImpl.class, new DeviceEventListResultImpl(), query);
    }

    public static long count(EntityManager em, KapuaQuery<DeviceEvent> query)
        throws KapuaException
    {
        return ServiceDAO.count(em, DeviceEvent.class, DeviceEventImpl.class, query);
    }

    public static void delete(EntityManager em, KapuaId deviceEventId)
    {
        ServiceDAO.delete(em, DeviceEventImpl.class, deviceEventId);
    }

}
