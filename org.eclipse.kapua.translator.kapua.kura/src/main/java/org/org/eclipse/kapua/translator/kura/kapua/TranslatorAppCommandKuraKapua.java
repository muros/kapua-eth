package org.org.eclipse.kapua.translator.kura.kapua;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.CommandMetrics;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandResponseChannel;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandResponseMessage;
import org.org.eclipse.kapua.service.device.management.command.message.internal.CommandResponsePayload;

public class TranslatorAppCommandKuraKapua implements Translator<KuraResponseMessage, CommandResponseMessage>
{
    private static final String                              CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<CommandMetrics, CommandAppProperties> metricsDictionary;

    private TranslatorAppCommandKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(CommandMetrics.APP_ID, CommandAppProperties.APP_NAME);
        metricsDictionary.put(CommandMetrics.APP_VERSION, CommandAppProperties.APP_VERSION);
        metricsDictionary.put(CommandMetrics.APP_METRIC_STDERR, CommandAppProperties.APP_PROPERTY_STDERR);
        metricsDictionary.put(CommandMetrics.APP_METRIC_STDOUT, CommandAppProperties.APP_PROPERTY_STDOUT);
        metricsDictionary.put(CommandMetrics.APP_METRIC_EXIT_CODE, CommandAppProperties.APP_PROPERTY_EXIT_CODE);
        metricsDictionary.put(CommandMetrics.APP_METRIC_TIMED_OUT, CommandAppProperties.APP_PROPERTY_TIMED_OUT);

    }

    @Override
    public CommandResponseMessage translate(KuraResponseMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(message.getChannel().getScope());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.findByClientId(account.getId(), message.getChannel().getClientId());

        CommandResponseChannel commandResponseChannel = translate(message.getChannel());

        //
        // Kura payload
        CommandResponsePayload responsePayload = translate(message.getPayload());

        //
        // Kura Message
        CommandResponseMessage kapuaMessage = new CommandResponseMessage();
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

    private CommandResponseChannel translate(KuraResponseChannel channel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(channel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          channel.getMessageClassification());
        }

        CommandResponseChannel kapuaChannel = new CommandResponseChannel();

        String[] appIdTokens = channel.getAppId().split("-");

        if (!CommandMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!CommandMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        kapuaChannel.setApp(CommandAppProperties.APP_NAME);
        kapuaChannel.setVersion(CommandAppProperties.APP_VERSION);

        return kapuaChannel;
    }

    private CommandResponsePayload translate(KuraResponsePayload payload)
        throws KapuaException
    {
        CommandResponsePayload commandResponsePayload = new CommandResponsePayload();

        commandResponsePayload.setStderr((String) payload.metrics().get(metricsDictionary.get(CommandMetrics.APP_METRIC_STDERR).getValue()));
        commandResponsePayload.setStdout((String) payload.metrics().get(metricsDictionary.get(CommandMetrics.APP_METRIC_STDOUT).getValue()));
        commandResponsePayload.setExitCode((Integer) payload.metrics().get(metricsDictionary.get(CommandMetrics.APP_METRIC_EXIT_CODE).getValue()));
        commandResponsePayload.setTimedout((Boolean) payload.metrics().get(metricsDictionary.get(CommandMetrics.APP_METRIC_TIMED_OUT).getValue()));

        commandResponsePayload.setExceptionMessage((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        commandResponsePayload.setExceptionStack((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        return commandResponsePayload;
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom()
    {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<CommandResponseMessage> getClassTo()
    {
        return CommandResponseMessage.class;
    }

}
