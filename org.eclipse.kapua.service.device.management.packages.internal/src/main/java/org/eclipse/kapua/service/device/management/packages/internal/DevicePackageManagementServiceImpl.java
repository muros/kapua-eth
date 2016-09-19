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
package org.eclipse.kapua.service.device.management.packages.internal;

import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestChannel;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestPayload;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponseMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageResponsePayload;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;

public class DevicePackageManagementServiceImpl implements DevicePackageManagementService
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DevicePackages getInstalled(KapuaId scopeId, KapuaId deviceId, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.read, scopeId));

        //
        // Prepare the request
        PackageRequestChannel deployRequestChannel = new PackageRequestChannel();
        deployRequestChannel.setAppName(PackageAppProperties.APP_NAME);
        deployRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
        deployRequestChannel.setMethod(KapuaMethod.READ);

        PackageRequestPayload deployRequestPayload = new PackageRequestPayload();

        PackageRequestMessage deployRequestMessage = new PackageRequestMessage();
        deployRequestMessage.setScopeId(scopeId);
        deployRequestMessage.setDeviceId(deviceId);
        deployRequestMessage.setCapturedOn(new Date());
        deployRequestMessage.setPayload(deployRequestPayload);
        deployRequestMessage.setChannel(deployRequestChannel);

        //
        // Do get
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
        PackageResponseMessage responseMessage = (PackageResponseMessage) deviceApplicationCall.send();

        //
        // Parse the response
        PackageResponsePayload responsePayload = responseMessage.getPayload();

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(responsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, responsePayload.getBody());

        }

        DevicePackages deviceDPListResult = null;
        try {
            deviceDPListResult = XmlUtil.unmarshal(body, DevicePackagesImpl.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, body);

        }

        //
        // Create event
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
        deviceEventCreator.setDeviceId(deviceId);
        deviceEventCreator.setPosition(responseMessage.getPosition());
        deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
        deviceEventCreator.setSentOn(responseMessage.getSentOn());
        deviceEventCreator.setResource(PackageAppProperties.APP_NAME.getValue());
        deviceEventCreator.setAction(KapuaMethod.READ);
        deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
        deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());

        deviceEventService.create(deviceEventCreator);

        return deviceDPListResult;
    }

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Override
    // public void install(KapuaId scopeId, KapuaId deviceId, String deploymentPackageName, byte[] deviceDeploymentPackage, Long timeout)
    // throws KapuaException
    // {
    // //
    // // Argument Validation
    // ArgumentValidator.notNull(scopeId, "scopeId");
    // ArgumentValidator.notNull(deviceId, "deviceId");
    // ArgumentValidator.notNull(deploymentPackageName, "deploymentPackageName");
    // ArgumentValidator.notNull(deviceDeploymentPackage, "deviceDeploymentPackage");
    //
    // //
    // // Check Access
    // KapuaLocator locator = KapuaLocator.getInstance();
    // AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
    // PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
    // authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));
    //
    // //
    // // Prepare the request
    // PackageRequestChannel deployRequestChannel = new PackageRequestChannel();
    // deployRequestChannel.setAppName(PackageAppProperties.APP_NAME);
    // deployRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
    // deployRequestChannel.setMethod(KapuaMethod.EXECUTE);
    // deployRequestChannel.setInstall(true);
    //
    // PackageRequestPayload deployRequestPayload = new PackageRequestPayload();
    // deployRequestPayload.setDeployInstallFileName(deploymentPackageName);
    // deployRequestPayload.setBody(deviceDeploymentPackage);
    //
    // PackageRequestMessage deployRequestMessage = new PackageRequestMessage();
    // deployRequestMessage.setScopeId(scopeId);
    // deployRequestMessage.setDeviceId(deviceId);
    // deployRequestMessage.setCapturedOn(new Date());
    // deployRequestMessage.setPayload(deployRequestPayload);
    // deployRequestMessage.setChannel(deployRequestChannel);
    //
    // //
    // // Do exec
    // DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
    // PackageResponseMessage responseMessage = (PackageResponseMessage) deviceApplicationCall.send();
    //
    // //
    // // Create event
    // DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
    // DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
    //
    // DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
    // deviceEventCreator.setDeviceId(deviceId);
    // deviceEventCreator.setPosition(responseMessage.getPosition());
    // deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
    // deviceEventCreator.setSentOn(responseMessage.getSentOn());
    // deviceEventCreator.setResource(PackageAppProperties.APP_NAME.getValue());
    // deviceEventCreator.setAction(KapuaMethod.EXECUTE);
    // deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
    // deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());
    //
    // deviceEventService.create(deviceEventCreator);
    // }
    //
    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Override
    // public void install(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageUrl, Long timeout)
    // throws KapuaException
    // {
    // //
    // // Argument Validation
    // ArgumentValidator.notNull(scopeId, "scopeId");
    // ArgumentValidator.notNull(deviceId, "deviceId");
    // ArgumentValidator.notNull(deviceDeploymentPackageUrl, "deviceDeploymentPackageUrl");
    //
    // //
    // // Check Access
    // KapuaLocator locator = KapuaLocator.getInstance();
    // AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
    // PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
    // authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));
    //
    // //
    // // Prepare the request
    // PackageRequestChannel deployRequestChannel = new PackageRequestChannel();
    // deployRequestChannel.setAppName(PackageAppProperties.APP_NAME);
    // deployRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
    // deployRequestChannel.setMethod(KapuaMethod.EXECUTE);
    //
    // PackageRequestPayload deployRequestPayload = new PackageRequestPayload();
    // deployRequestPayload.setDeployInstallUrl(deviceDeploymentPackageUrl);
    //
    // PackageRequestMessage deployRequestMessage = new PackageRequestMessage();
    // deployRequestMessage.setScopeId(scopeId);
    // deployRequestMessage.setDeviceId(deviceId);
    // deployRequestMessage.setCapturedOn(new Date());
    // deployRequestMessage.setPayload(deployRequestPayload);
    // deployRequestMessage.setChannel(deployRequestChannel);
    //
    // //
    // // Do exec
    // DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
    // PackageResponseMessage responseMessage = (PackageResponseMessage) deviceApplicationCall.send();
    //
    // //
    // // Create event
    // DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
    // DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
    //
    // DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
    // deviceEventCreator.setDeviceId(deviceId);
    // deviceEventCreator.setPosition(responseMessage.getPosition());
    // deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
    // deviceEventCreator.setSentOn(responseMessage.getSentOn());
    // deviceEventCreator.setResource(PackageAppProperties.APP_NAME.getValue());
    // deviceEventCreator.setAction(KapuaMethod.EXECUTE);
    // deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
    // deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());
    //
    // deviceEventService.create(deviceEventCreator);
    //
    // }
    //
    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Override
    // public void uninstall(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageId, Long timeout)
    // throws KapuaException
    // {
    // //
    // // Argument Validation
    // ArgumentValidator.notNull(scopeId, "scopeId");
    // ArgumentValidator.notNull(deviceId, "deviceId");
    // ArgumentValidator.notNull(deviceDeploymentPackageId, "deviceDeploymentPackageId");
    //
    // //
    // // Check Access
    // KapuaLocator locator = KapuaLocator.getInstance();
    // AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
    // PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
    // authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));
    //
    // //
    // // Prepare the request
    // PackageRequestChannel deployRequestChannel = new PackageRequestChannel();
    // deployRequestChannel.setAppName(PackageAppProperties.APP_NAME);
    // deployRequestChannel.setVersion(PackageAppProperties.APP_VERSION);
    // deployRequestChannel.setMethod(KapuaMethod.EXECUTE);
    //
    // PackageRequestPayload deployRequestPayload = new PackageRequestPayload();
    // deployRequestPayload.setDeployUninstallFileName(deviceDeploymentPackageId);
    //
    // PackageRequestMessage deployRequestMessage = new PackageRequestMessage();
    // deployRequestMessage.setScopeId(scopeId);
    // deployRequestMessage.setDeviceId(deviceId);
    // deployRequestMessage.setCapturedOn(new Date());
    // deployRequestMessage.setPayload(deployRequestPayload);
    // deployRequestMessage.setChannel(deployRequestChannel);
    //
    // //
    // // Do exec
    // DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
    // PackageResponseMessage responseMessage = (PackageResponseMessage) deviceApplicationCall.send();
    //
    // //
    // // Create event
    // DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
    // DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);
    //
    // DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
    // deviceEventCreator.setDeviceId(deviceId);
    // deviceEventCreator.setPosition(responseMessage.getPosition());
    // deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
    // deviceEventCreator.setSentOn(responseMessage.getSentOn());
    // deviceEventCreator.setResource(PackageAppProperties.APP_NAME.getValue());
    // deviceEventCreator.setAction(KapuaMethod.EXECUTE);
    // deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
    // deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());
    //
    // deviceEventService.create(deviceEventCreator);
    // }
}
