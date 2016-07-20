package org.eclipse.kapua.service.device.management.configuration.internal;

import java.io.StringWriter;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessageFactory;
import org.eclipse.kapua.message.device.app.request.KapuaRequestPayload;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementMethod;
import org.eclipse.kapua.service.device.management.commons.call.DeviceApplicationCall;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;

public class DeviceConfigurationManagementServiceImpl implements DeviceConfigurationManagementService
{
    private static final String deviceConfigurationManagementAppId = "CONF-V1";

    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String configurationComponentPid)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.read, scopeId));

        //
        // Prepare the request
        String[] resources;
        if (configurationComponentPid != null) {
            resources = new String[] { "configuration", configurationComponentPid };
        }
        else {
            resources = new String[] { "configuration" };
        }

        //
        // Do get
        DeviceApplicationCall<DeviceConfiguration> deviceApplicationCall = new DeviceApplicationCall<DeviceConfiguration>(scopeId,
                                                                                                                          deviceId,
                                                                                                                          deviceConfigurationManagementAppId,
                                                                                                                          DeviceManagementMethod.GET,
                                                                                                                          resources);
        deviceApplicationCall.setResponseHandler(new ConfigurationManagementResponseHandlers.GET());

        //
        // Return result
        return deviceApplicationCall.send();
    }

    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceComponentConfiguration deviceComponentConfiguration)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(deviceComponentConfiguration, "componentConfiguration");
        ArgumentValidator.notEmptyOrNull(deviceComponentConfiguration.getComponentId(), "componentConfiguration.componentId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.write, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "configuration", deviceComponentConfiguration.getComponentId() };
        KapuaMessageFactory messageFactory = locator.getFactory(KapuaMessageFactory.class);
        KapuaRequestPayload requestPayload = messageFactory.newRequestPayload();
        try {
            DeviceConfigurationFactory deviceConfigurationFactory = locator.getFactory(DeviceConfigurationFactory.class);
            DeviceConfiguration deviceConfiguration = deviceConfigurationFactory.newConfigurationInstance();
            deviceConfiguration.getComponentConfigurations().add(deviceComponentConfiguration);

            DeviceManagementSetting deviceManagementConfig = DeviceManagementSetting.getInstance();
            String charEncoding = deviceManagementConfig.getString(DeviceManagementSettingKey.CHAR_ENCODING);

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceConfiguration, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            requestPayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, deviceComponentConfiguration);
        }

        //
        // Do put
        DeviceApplicationCall<Void> deviceApplicationCall = new DeviceApplicationCall<Void>(scopeId,
                                                                                            deviceId,
                                                                                            deviceConfigurationManagementAppId,
                                                                                            DeviceManagementMethod.PUT,
                                                                                            resources);
        deviceApplicationCall.setRequestPayload(requestPayload);
        deviceApplicationCall.setResponseHandler(new ConfigurationManagementResponseHandlers.PUT());

        //
        // Return result
        deviceApplicationCall.send();
    }

    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration deviceConfiguration)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notNull(deviceConfiguration, "componentConfiguration");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.device_management, Actions.write, scopeId));

        //
        // Prepare the request
        String[] resources = new String[] { "configuration" };
        KapuaMessageFactory messageFactory = locator.getFactory(KapuaMessageFactory.class);
        KapuaRequestPayload requestPayload = messageFactory.newRequestPayload();
        try {
            DeviceManagementSetting deviceManagementConfig = DeviceManagementSetting.getInstance();
            String charEncoding = deviceManagementConfig.getString(DeviceManagementSettingKey.CHAR_ENCODING);

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceConfiguration, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            requestPayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, deviceConfiguration);
        }

        //
        // Do put
        DeviceApplicationCall<Void> deviceApplicationCall = new DeviceApplicationCall<Void>(scopeId,
                                                                                            deviceId,
                                                                                            deviceConfigurationManagementAppId,
                                                                                            DeviceManagementMethod.PUT,
                                                                                            resources);
        deviceApplicationCall.setRequestPayload(requestPayload);
        deviceApplicationCall.setResponseHandler(new ConfigurationManagementResponseHandlers.PUT());

        //
        // Make call
        deviceApplicationCall.send();
    }

    // @Override
    // public void apply(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration configuration)
    // throws KapuaException
    // {
    // // TODO Auto-generated method stub
    //
    // }

    // @Override
    // public void rollback(KapuaId scopeId, KapuaId deviceId, String configurationId)
    // throws KapuaException
    // {
    // // TODO Auto-generated method stub
    //
    // }

}
