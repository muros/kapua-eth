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
package org.eclipse.kapua.service.device.registry.lifecycle.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaBirthPayload;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceCredentialsMode;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifeCycleService;

public class DeviceLifeCycleServiceImpl implements DeviceLifeCycleService
{

    @Override
    public void birth(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        KapuaBirthPayload payload = new KapuaBirthPayload(message.getKapuaPayload());
        KapuaTopic topic = message.getKapuaTopic();
        String accountName = topic.getAccount();
        String clientId = topic.getAsset();

        //
        // Account find
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(accountName);
        KapuaId scopeId = account.getId();

        //
        // Device update
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(scopeId,
                                                             clientId);

        if (device == null) {
            DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);

            DeviceCreator deviceCreator = deviceFactory.newCreator(scopeId,
                                                                   clientId);

            deviceCreator.setDisplayName(payload.getDisplayName());
            deviceCreator.setSerialNumber(payload.getSerialNumber());
            deviceCreator.setModelId(payload.getModelId());
            deviceCreator.setImei(payload.getModemImei());
            deviceCreator.setImsi(payload.getModemImsi());
            deviceCreator.setIccid(payload.getModemIccid());
            deviceCreator.setBiosVersion(payload.getBiosVersion());
            deviceCreator.setFirmwareVersion(payload.getFirmwareVersion());
            deviceCreator.setOsVersion(payload.getOsVersion());
            deviceCreator.setJvmVersion(payload.getJvmVersion());
            deviceCreator.setOsgiFrameworkVersion(payload.getOsgiFrameworkVersion());
            deviceCreator.setApplicationIdentifiers(payload.getApplicationIdentifiers());
            deviceCreator.setAcceptEncoding(payload.getAcceptEncoding());
            deviceCreator.setCredentialsMode(DeviceCredentialsMode.LOOSE);

            device = deviceRegistryService.create(deviceCreator);
        }
        else {
            device.setDisplayName(payload.getDisplayName());
            device.setSerialNumber(payload.getSerialNumber());
            device.setModelId(payload.getModelId());
            device.setImei(payload.getModemImei());
            device.setImsi(payload.getModemImsi());
            device.setIccid(payload.getModemIccid());
            device.setBiosVersion(payload.getBiosVersion());
            device.setFirmwareVersion(payload.getFirmwareVersion());
            device.setOsVersion(payload.getOsVersion());
            device.setJvmVersion(payload.getJvmVersion());
            device.setOsgiVersion(payload.getOsgiFrameworkVersion());
            device.setApplicationIdentifiers(payload.getApplicationIdentifiers());
            device.setAcceptEncoding(payload.getAcceptEncoding());

            deviceRegistryService.update(device);
        }

        //
        // Event create
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);

        deviceEventCreator.setDeviceId(device.getId());
        deviceEventCreator.setEventMessage(payload.toDisplayString());
        deviceEventCreator.setEventType("BIRTH");
        deviceEventCreator.setReceivedOn(message.getTimestamp());
        deviceEventCreator.setSentOn(payload.getTimestamp());

        if (payload.getPosition() != null)
            deviceEventCreator.setPosition(payload.getPosition());

        deviceEventService.create(deviceEventCreator);
    }

    @Override
    public void death(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        KapuaPayload payload = message.getKapuaPayload();
        KapuaTopic topic = message.getKapuaTopic();
        String accountName = topic.getAccount();
        String clientId = topic.getAsset();

        //
        // Account find
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(accountName);
        KapuaId scopeId = account.getId();

        //
        // Device update
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(scopeId,
                                                             clientId);

        //
        // Event create
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);

        deviceEventCreator.setDeviceId(device.getId());
        deviceEventCreator.setEventMessage(payload.toDisplayString());
        deviceEventCreator.setEventType("DEATH");
        deviceEventCreator.setReceivedOn(message.getTimestamp());
        deviceEventCreator.setSentOn(payload.getTimestamp());

        if (payload.getPosition() != null)
            deviceEventCreator.setPosition(payload.getPosition());

        deviceEventService.create(deviceEventCreator);
    }

    @Override
    public void missing(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        KapuaPayload payload = message.getKapuaPayload();
        KapuaTopic topic = message.getKapuaTopic();
        String accountName = topic.getAccount();
        String clientId = topic.getAsset();

        //
        // Account find
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(accountName);
        KapuaId scopeId = account.getId();

        //
        // Device update
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(scopeId,
                                                             clientId);

        //
        // Event create
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);

        deviceEventCreator.setDeviceId(device.getId());
        deviceEventCreator.setEventMessage(payload.toDisplayString());
        deviceEventCreator.setEventType("MISSING");
        deviceEventCreator.setReceivedOn(message.getTimestamp());
        deviceEventCreator.setSentOn(payload.getTimestamp());

        if (payload.getPosition() != null)
            deviceEventCreator.setPosition(payload.getPosition());

        deviceEventService.create(deviceEventCreator);

    }

    @Override
    public void applications(KapuaId connectionId, KapuaMessage message)
        throws KapuaException
    {
        KapuaPayload payload = message.getKapuaPayload();
        KapuaTopic topic = message.getKapuaTopic();
        String accountName = topic.getAccount();
        String clientId = topic.getAsset();

        //
        // Account find
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(accountName);
        KapuaId scopeId = account.getId();

        //
        // Device update
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(scopeId,
                                                             clientId);

        //
        // Event create
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);

        deviceEventCreator.setDeviceId(device.getId());
        deviceEventCreator.setEventMessage(payload.toDisplayString());
        deviceEventCreator.setEventType("APPLICATION");
        deviceEventCreator.setReceivedOn(message.getTimestamp());
        deviceEventCreator.setSentOn(payload.getTimestamp());

        if (payload.getPosition() != null)
            deviceEventCreator.setPosition(payload.getPosition());

        deviceEventService.create(deviceEventCreator);
    }
}
