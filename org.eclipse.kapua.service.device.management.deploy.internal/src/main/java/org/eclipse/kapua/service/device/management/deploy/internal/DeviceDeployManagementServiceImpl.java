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
package org.eclipse.kapua.service.device.management.deploy.internal;

import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeployManagementService;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackageListResult;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestChannel;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestPayload;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponsePayload;
import org.eclipse.kapua.service.device.registry.event.DeviceEventCreator;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;

public class DeviceDeployManagementServiceImpl implements DeviceDeployManagementService
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeviceDeploymentPackageListResult get(KapuaId scopeId, KapuaId deviceId, Long timeout)
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
        DeployRequestChannel deployRequestChannel = new DeployRequestChannel();
        deployRequestChannel.setAppName(DeployAppProperties.APP_NAME);
        deployRequestChannel.setVersion(DeployAppProperties.APP_VERSION);
        deployRequestChannel.setMethod(KapuaMethod.READ);

        DeployRequestPayload deployRequestPayload = new DeployRequestPayload();

        DeployRequestMessage deployRequestMessage = new DeployRequestMessage();
        deployRequestMessage.setScopeId(scopeId);
        deployRequestMessage.setDeviceId(deviceId);
        deployRequestMessage.setCapturedOn(new Date());
        deployRequestMessage.setPayload(deployRequestPayload);
        deployRequestMessage.setSemanticChannel(deployRequestChannel);

        //
        // Do get
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
        DeployResponseMessage responseMessage = (DeployResponseMessage) deviceApplicationCall.send();

        //
        // Parse the response
        DeployResponsePayload responsePayload = responseMessage.getPayload();

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(responsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, responsePayload.getBody());

        }

        DeviceDeploymentPackageListResult deviceDPListResult = null;
        try {
            deviceDPListResult = XmlUtil.unmarshal(body, DeviceDeploymentPackageListResult.class);
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
        deviceEventCreator.setResource(DeployAppProperties.APP_NAME.getValue());
        deviceEventCreator.setAction(KapuaMethod.READ);
        deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
        deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());

        deviceEventService.create(deviceEventCreator);

        return deviceDPListResult;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void install(KapuaId scopeId, KapuaId deviceId, String deploymentPackageName, byte[] deviceDeploymentPackage, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(deploymentPackageName, "deploymentPackageName");
        ArgumentValidator.notNull(deviceDeploymentPackage, "deviceDeploymentPackage");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        DeployRequestChannel deployRequestChannel = new DeployRequestChannel();
        deployRequestChannel.setAppName(DeployAppProperties.APP_NAME);
        deployRequestChannel.setVersion(DeployAppProperties.APP_VERSION);
        deployRequestChannel.setMethod(KapuaMethod.EXECUTE);
        deployRequestChannel.setInstall(true);

        DeployRequestPayload deployRequestPayload = new DeployRequestPayload();
        deployRequestPayload.setDeployInstallFileName(deploymentPackageName);
        deployRequestPayload.setBody(deviceDeploymentPackage);

        DeployRequestMessage deployRequestMessage = new DeployRequestMessage();
        deployRequestMessage.setScopeId(scopeId);
        deployRequestMessage.setDeviceId(deviceId);
        deployRequestMessage.setCapturedOn(new Date());
        deployRequestMessage.setPayload(deployRequestPayload);
        deployRequestMessage.setSemanticChannel(deployRequestChannel);

        //
        // Do exec
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
        DeployResponseMessage responseMessage = (DeployResponseMessage) deviceApplicationCall.send();

        //
        // Create event
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
        deviceEventCreator.setDeviceId(deviceId);
        deviceEventCreator.setPosition(responseMessage.getPosition());
        deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
        deviceEventCreator.setSentOn(responseMessage.getSentOn());
        deviceEventCreator.setResource(DeployAppProperties.APP_NAME.getValue());
        deviceEventCreator.setAction(KapuaMethod.EXECUTE);
        deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
        deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());

        deviceEventService.create(deviceEventCreator);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void install(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageUrl, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(deviceDeploymentPackageUrl, "deviceDeploymentPackageUrl");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        DeployRequestChannel deployRequestChannel = new DeployRequestChannel();
        deployRequestChannel.setAppName(DeployAppProperties.APP_NAME);
        deployRequestChannel.setVersion(DeployAppProperties.APP_VERSION);
        deployRequestChannel.setMethod(KapuaMethod.EXECUTE);

        DeployRequestPayload deployRequestPayload = new DeployRequestPayload();
        deployRequestPayload.setDeployInstallUrl(deviceDeploymentPackageUrl);

        DeployRequestMessage deployRequestMessage = new DeployRequestMessage();
        deployRequestMessage.setScopeId(scopeId);
        deployRequestMessage.setDeviceId(deviceId);
        deployRequestMessage.setCapturedOn(new Date());
        deployRequestMessage.setPayload(deployRequestPayload);
        deployRequestMessage.setSemanticChannel(deployRequestChannel);

        //
        // Do exec
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
        DeployResponseMessage responseMessage = (DeployResponseMessage) deviceApplicationCall.send();

        //
        // Create event
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
        deviceEventCreator.setDeviceId(deviceId);
        deviceEventCreator.setPosition(responseMessage.getPosition());
        deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
        deviceEventCreator.setSentOn(responseMessage.getSentOn());
        deviceEventCreator.setResource(DeployAppProperties.APP_NAME.getValue());
        deviceEventCreator.setAction(KapuaMethod.EXECUTE);
        deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
        deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());

        deviceEventService.create(deviceEventCreator);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void uninstall(KapuaId scopeId, KapuaId deviceId, String deviceDeploymentPackageId, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(deviceDeploymentPackageId, "deviceDeploymentPackageId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        DeployRequestChannel deployRequestChannel = new DeployRequestChannel();
        deployRequestChannel.setAppName(DeployAppProperties.APP_NAME);
        deployRequestChannel.setVersion(DeployAppProperties.APP_VERSION);
        deployRequestChannel.setMethod(KapuaMethod.EXECUTE);

        DeployRequestPayload deployRequestPayload = new DeployRequestPayload();
        deployRequestPayload.setDeployUninstallFileName(deviceDeploymentPackageId);

        DeployRequestMessage deployRequestMessage = new DeployRequestMessage();
        deployRequestMessage.setScopeId(scopeId);
        deployRequestMessage.setDeviceId(deviceId);
        deployRequestMessage.setCapturedOn(new Date());
        deployRequestMessage.setPayload(deployRequestPayload);
        deployRequestMessage.setSemanticChannel(deployRequestChannel);

        //
        // Do exec
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(deployRequestMessage, timeout);
        DeployResponseMessage responseMessage = (DeployResponseMessage) deviceApplicationCall.send();

        //
        // Create event
        DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

        DeviceEventCreator deviceEventCreator = deviceEventFactory.newCreator(scopeId);
        deviceEventCreator.setDeviceId(deviceId);
        deviceEventCreator.setPosition(responseMessage.getPosition());
        deviceEventCreator.setReceivedOn(responseMessage.getReceivedOn());
        deviceEventCreator.setSentOn(responseMessage.getSentOn());
        deviceEventCreator.setResource(DeployAppProperties.APP_NAME.getValue());
        deviceEventCreator.setAction(KapuaMethod.EXECUTE);
        deviceEventCreator.setResponseCode(responseMessage.getResponseCode());
        deviceEventCreator.setEventMessage(responseMessage.getPayload().toDisplayString());

        deviceEventService.create(deviceEventCreator);
    }
}
