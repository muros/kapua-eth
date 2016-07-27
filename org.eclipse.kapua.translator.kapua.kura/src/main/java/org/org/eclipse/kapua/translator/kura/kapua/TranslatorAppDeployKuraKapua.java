package org.org.eclipse.kapua.translator.kura.kapua;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.app.DeployMetrics;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.deploy.internal.DeployAppProperties;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseChannel;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponsePayload;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

public class TranslatorAppDeployKuraKapua implements Translator<KuraResponseMessage, DeployResponseMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<DeployMetrics, DeployAppProperties> metricsDictionary;

    private TranslatorAppDeployKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(DeployMetrics.APP_ID, DeployAppProperties.APP_NAME);
        metricsDictionary.put(DeployMetrics.APP_VERSION, DeployAppProperties.APP_VERSION);
    }

    @Override
    public DeployResponseMessage translate(KuraResponseMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(message.getChannel().getScope());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.findByClientId(account.getId(), message.getChannel().getClientId());

        DeployResponseChannel responseChannel = translate(message.getChannel());

        //
        // Kura payload
        DeployResponsePayload responsePayload = translate(message.getPayload());

        //
        // Kura Message
        DeployResponseMessage kapuaMessage = new DeployResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setDeviceId(device.getId());
        kapuaMessage.setSemanticChannel(responseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(message.getPayload().getTimestamp());
        kapuaMessage.setSentOn(message.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(message.timestamp());
        kapuaMessage.setResponseCode(KapuaResponseCode.valueOf((String) message.getPayload().metrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return result
        return kapuaMessage;
    }

    private DeployResponseChannel translate(KuraResponseChannel channel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(channel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          channel.getMessageClassification());
        }

        DeployResponseChannel responseChannel = new DeployResponseChannel();

        String[] appIdTokens = channel.getAppId().split("-");

        if (!DeployMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!DeployMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        responseChannel.setAppName(DeployAppProperties.APP_NAME);
        responseChannel.setVersion(DeployAppProperties.APP_VERSION);

        return responseChannel;
    }

    private DeployResponsePayload translate(KuraResponsePayload payload)
        throws KapuaException
    {
        DeployResponsePayload responsePayload = new DeployResponsePayload();

        responsePayload.setExceptionMessage((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        responsePayload.setExceptionStack((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        return responsePayload;
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom()
    {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<DeployResponseMessage> getClassTo()
    {
        return DeployResponseMessage.class;
    }
}