package org.org.eclipse.kapua.translator.kapua.kura;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.CommandMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestChannel;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandRequestPayload;

public class TranslatorAppCommandKapuaKura implements Translator<CommandRequestMessage, KuraRequestMessage>
{
    private static final String                              CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX);
    private static Map<CommandAppProperties, CommandMetrics> propertiesDictionary;

    private TranslatorAppCommandKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(CommandAppProperties.APP_NAME, CommandMetrics.APP_ID);
        propertiesDictionary.put(CommandAppProperties.APP_VERSION, CommandMetrics.APP_VERSION);

        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_CMD, CommandMetrics.APP_METRIC_CMD);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_ARG, CommandMetrics.APP_METRIC_ARG);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_ENVP, CommandMetrics.APP_METRIC_ENVP);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_DIR, CommandMetrics.APP_METRIC_DIR);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_STDIN, CommandMetrics.APP_METRIC_STDIN);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_TOUT, CommandMetrics.APP_METRIC_TOUT);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_ASYNC, CommandMetrics.APP_METRIC_ASYNC);
        propertiesDictionary.put(CommandAppProperties.APP_PROPERTY_PASSWORD, CommandMetrics.APP_METRIC_PASSWORD);

    }

    @Override
    public KuraRequestMessage translate(CommandRequestMessage message)
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

    private KuraRequestChannel translate(CommandRequestChannel channel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(CommandAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(CommandAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());
        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(channel.getMethod()));
        kuraRequestChannel.setResources(new String[] { "command" });

        return kuraRequestChannel;
    }

    public KuraRequestPayload translate(CommandRequestPayload payload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_CMD).getValue(), payload.getCommand());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_ARG).getValue(), payload.getArguments());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_ENVP).getValue(), payload.getEnvironmentPairs());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_DIR).getValue(), payload.getWorkingDir());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_STDIN).getValue(), payload.getStdin());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_TOUT).getValue(), payload.getTimeout());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_ASYNC).getValue(), payload.isRunAsync());
        kuraRequestPayload.metrics().put(propertiesDictionary.get(CommandAppProperties.APP_PROPERTY_PASSWORD).getValue(), payload.getPassword());

        return kuraRequestPayload;
    }

    @Override
    public Class<CommandRequestMessage> getClassFrom()
    {
        return CommandRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }

}
