package org.org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.model.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseChannel;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponseMessage;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationResponsePayload;

public class TranslatorAppConfigurationKuraKapua implements Translator<KuraResponseMessage, ConfigurationResponseMessage>
{
    private static final String                                          CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<ConfigurationMetrics, ConfigurationAppProperties> metricsDictionary;

    private TranslatorAppConfigurationKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(ConfigurationMetrics.APP_ID, ConfigurationAppProperties.APP_NAME);
        metricsDictionary.put(ConfigurationMetrics.APP_VERSION, ConfigurationAppProperties.APP_VERSION);
    }

    @Override
    public ConfigurationResponseMessage translate(KuraResponseMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(message.getChannel().getScope());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.findByClientId(account.getId(), message.getChannel().getClientId());

        ConfigurationResponseChannel commandResponseChannel = translate(message.getChannel());

        //
        // Kura payload
        ConfigurationResponsePayload responsePayload = translate(message.getPayload());

        //
        // Kura Message
        ConfigurationResponseMessage kapuaMessage = new ConfigurationResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setDeviceId(device.getId());
        kapuaMessage.setSemanticChannel(commandResponseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(message.getPayload().getTimestamp());
        kapuaMessage.setSentOn(message.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(message.timestamp());
        kapuaMessage.setResponseCode(KapuaResponseCode.valueOf((String) message.getPayload().metrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return result
        return kapuaMessage;
    }

    private ConfigurationResponseChannel translate(KuraResponseChannel channel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(channel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          channel.getMessageClassification());
        }

        ConfigurationResponseChannel configurationResponseChannel = new ConfigurationResponseChannel();

        String[] appIdTokens = channel.getAppId().split("-");

        if (!ConfigurationMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!ConfigurationMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        configurationResponseChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        configurationResponseChannel.setVersion(ConfigurationAppProperties.APP_VERSION);

        return configurationResponseChannel;
    }

    private ConfigurationResponsePayload translate(KuraResponsePayload payload)
        throws KapuaException
    {
        ConfigurationResponsePayload configurationResponsePayload = new ConfigurationResponsePayload();

        configurationResponsePayload.setExceptionMessage((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        configurationResponsePayload.setExceptionStack((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));
        
        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(configurationResponsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, configurationResponsePayload.getBody());

        }

        KuraDeviceConfiguration kuraDeviceConfiguration = null;
        try {
            kuraDeviceConfiguration = XmlUtil.unmarshal(body, KuraDeviceConfiguration.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, body);
        }
        
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceConfigurationFactory deviceConfigurationFactory = locator.getFactory(DeviceConfigurationFactory.class);
            DeviceConfiguration deviceConfiguration = deviceConfigurationFactory.newConfigurationInstance();
            
            for (KuraDeviceComponentConfiguration kuraDeviceCompConf : kuraDeviceConfiguration.getConfigurations()) {
                String componentId = kuraDeviceCompConf.getComponentId();
                DeviceComponentConfiguration deviceComponentConfiguration = deviceConfigurationFactory.newComponentConfigurationInstance(componentId);
                deviceComponentConfiguration.setComponentName(kuraDeviceCompConf.getComponentName());
                deviceComponentConfiguration.setDefinition(kuraDeviceCompConf.getDefinition());
                deviceComponentConfiguration.setProperties(kuraDeviceCompConf.getProperties());
                
                deviceConfiguration.getComponentConfigurations().add(deviceComponentConfiguration);
            }

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceConfiguration, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            configurationResponsePayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, (Object[]) null); //null for now
        }
        

        return configurationResponsePayload;
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom()
    {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<ConfigurationResponseMessage> getClassTo()
    {
        return ConfigurationResponseMessage.class;
    }

}