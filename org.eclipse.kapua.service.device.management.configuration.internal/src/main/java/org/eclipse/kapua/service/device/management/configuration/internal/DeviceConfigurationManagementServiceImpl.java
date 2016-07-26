package org.eclipse.kapua.service.device.management.configuration.internal;

import java.io.StringWriter;
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
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestMessage;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestPayload;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseMessage;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponsePayload;

public class DeviceConfigurationManagementServiceImpl implements DeviceConfigurationManagementService
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String configurationComponentPid, Long timeout)
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
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.READ);
        configurationRequestChannel.setComponentId(configurationComponentPid);

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setSemanticChannel(configurationRequestChannel);

        //
        // Do get
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(configurationRequestMessage, timeout);
        ConfigurationResponseMessage responseMessage = (ConfigurationResponseMessage) deviceApplicationCall.send();

        //
        // Parse the response
        ConfigurationResponsePayload responsePayload = responseMessage.getPayload();

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(responsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, responsePayload.getBody());

        }

        DeviceConfiguration deviceConfiguration = null;
        try {
            deviceConfiguration = XmlUtil.unmarshal(body, DeviceConfiguration.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, body);

        }

        return deviceConfiguration;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceComponentConfiguration deviceComponentConfiguration, Long timeout)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.write, scopeId));

        //
        // Prepare the request
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.WRITE);
        configurationRequestChannel.setComponentId(deviceComponentConfiguration.getComponentId());

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        try {
            DeviceConfigurationFactory deviceConfigurationFactory = locator.getFactory(DeviceConfigurationFactory.class);
            DeviceConfiguration deviceConfiguration = deviceConfigurationFactory.newConfigurationInstance();
            deviceConfiguration.getComponentConfigurations().add(deviceComponentConfiguration);

            DeviceManagementSetting deviceManagementConfig = DeviceManagementSetting.getInstance();
            String charEncoding = deviceManagementConfig.getString(DeviceManagementSettingKey.CHAR_ENCODING);

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceConfiguration, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            configurationRequestPayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, deviceComponentConfiguration);
        }

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setSemanticChannel(configurationRequestChannel);

        //
        // Do put
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(configurationRequestMessage, timeout);
        ConfigurationResponseMessage responseMessage = (ConfigurationResponseMessage) deviceApplicationCall.send();

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration deviceConfiguration, Long timeout)
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
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.write, scopeId));

        //
        // Prepare the request
        ConfigurationRequestChannel configurationRequestChannel = new ConfigurationRequestChannel();
        configurationRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        configurationRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        configurationRequestChannel.setMethod(KapuaMethod.WRITE);

        ConfigurationRequestPayload configurationRequestPayload = new ConfigurationRequestPayload();

        try {
            DeviceManagementSetting deviceManagementConfig = DeviceManagementSetting.getInstance();
            String charEncoding = deviceManagementConfig.getString(DeviceManagementSettingKey.CHAR_ENCODING);

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceConfiguration, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            configurationRequestPayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, deviceConfiguration);
        }

        ConfigurationRequestMessage configurationRequestMessage = new ConfigurationRequestMessage();
        configurationRequestMessage.setScopeId(scopeId);
        configurationRequestMessage.setDeviceId(deviceId);
        configurationRequestMessage.setCapturedOn(new Date());
        configurationRequestMessage.setPayload(configurationRequestPayload);
        configurationRequestMessage.setSemanticChannel(configurationRequestChannel);

        //
        // Do put
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(configurationRequestMessage, timeout);
        ConfigurationResponseMessage responseMessage = (ConfigurationResponseMessage) deviceApplicationCall.send();

    }
}
