package org.org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.SnapshotMetrics;
import org.eclipse.kapua.service.device.call.kura.model.XmlSnapshotIdResult;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.snapshot.internal.SnapshotAppProperties;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotFactory;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotListResult;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponseChannel;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponseMessage;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponsePayload;

public class TranslatorAppSnapshotKuraKapua implements Translator<KuraResponseMessage, SnapshotResponseMessage>
{
    private static final String                                          CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<SnapshotMetrics, SnapshotAppProperties> metricsDictionary;

    private TranslatorAppSnapshotKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(SnapshotMetrics.APP_ID, SnapshotAppProperties.APP_NAME);
        metricsDictionary.put(SnapshotMetrics.APP_VERSION, SnapshotAppProperties.APP_VERSION);
    }

    @Override
    public SnapshotResponseMessage translate(KuraResponseMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(message.getChannel().getScope());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.findByClientId(account.getId(), message.getChannel().getClientId());

        SnapshotResponseChannel commandResponseChannel = translate(message.getChannel());

        //
        // Kura payload
        SnapshotResponsePayload responsePayload = translate(message.getPayload());

        //
        // Kura Message
        SnapshotResponseMessage kapuaMessage = new SnapshotResponseMessage();
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

    private SnapshotResponseChannel translate(KuraResponseChannel channel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(channel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          channel.getMessageClassification());
        }

        SnapshotResponseChannel snapshotResponseChannel = new SnapshotResponseChannel();

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

        snapshotResponseChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        snapshotResponseChannel.setVersion(ConfigurationAppProperties.APP_VERSION);

        return snapshotResponseChannel;
    }

    private SnapshotResponsePayload translate(KuraResponsePayload payload)
        throws KapuaException
    {
        SnapshotResponsePayload snapshotResponsePayload = new SnapshotResponsePayload();

        snapshotResponsePayload.setExceptionMessage((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        snapshotResponsePayload.setExceptionStack((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(snapshotResponsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, snapshotResponsePayload.getBody());

        }

        XmlSnapshotIdResult snapshotIdResult = null;
        try {
            snapshotIdResult = XmlUtil.unmarshal(body, XmlSnapshotIdResult.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, body);
        }
        
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceSnapshotFactory deviceSnapshotFactory = locator.getFactory(DeviceSnapshotFactory.class);
            DeviceSnapshotListResult deviceSnapshots = deviceSnapshotFactory.newDeviceSnapshotListResult();
            
            List<Long> snapshotIds = snapshotIdResult.getSnapshotIds();
            for (Long snapshotId : snapshotIds) {
                DeviceSnapshot snapshot = deviceSnapshotFactory.newDeviceSnapshot(snapshotId);
                deviceSnapshots.add(snapshot);
            }

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceSnapshots, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            snapshotResponsePayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, (Object[]) null); //null for now
        }
        
        return snapshotResponsePayload;
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom()
    {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<SnapshotResponseMessage> getClassTo()
    {
        return SnapshotResponseMessage.class;
    }

}