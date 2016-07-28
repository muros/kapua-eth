package org.org.eclipse.kapua.translator.kapua.kura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationAppProperties;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestMessage;
import org.org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestPayload;

public class TranslatorAppConfigurationKapuaKura implements Translator<ConfigurationRequestMessage, KuraRequestMessage>
{
    private static final String                                          CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<ConfigurationAppProperties, ConfigurationMetrics> propertiesDictionary;

    private TranslatorAppConfigurationKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(ConfigurationAppProperties.APP_NAME, ConfigurationMetrics.APP_ID);
        propertiesDictionary.put(ConfigurationAppProperties.APP_VERSION, ConfigurationMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(ConfigurationRequestMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.find(message.getScopeId());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.find(message.getScopeId(), message.getDeviceId());

        KuraRequestChannel kuraRequestChannel = translate(message.getSemanticChannel());
        kuraRequestChannel.setScope(account.getName());
        kuraRequestChannel.setClientId(device.getClientId());

        //
        // Kura payload
        KuraRequestPayload kuraPayload = translate(message.getPayload());

        //
        // Kura Message
        KuraRequestMessage kuraMessage = new KuraRequestMessage(kuraRequestChannel,
                                                                message.getReceivedOn(),
                                                                kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    private KuraRequestChannel translate(ConfigurationRequestChannel channel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(ConfigurationAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(ConfigurationAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());
        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(channel.getMethod()));

        List<String> resources = new ArrayList<>();
        resources.add("configuration");
        String snapshotId = channel.getComponentId();
        if (snapshotId != null) {
            resources.add(snapshotId);
        }
        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(ConfigurationRequestPayload payload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        kuraRequestPayload.setBody(payload.getBody());

        return kuraRequestPayload;
    }

    @Override
    public Class<ConfigurationRequestMessage> getClassFrom()
    {
        return ConfigurationRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }

}
