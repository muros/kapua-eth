package org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.app.SnapshotMetrics;
import org.eclipse.kapua.service.device.call.kura.model.XmlSnapshotIdResult;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponseChannel;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponseMessage;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponsePayload;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotFactory;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotIds;
import org.eclipse.kapua.service.device.management.snapshot.internal.SnapshotAppProperties;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

public class TranslatorAppSnapshotKuraKapua extends Translator<KuraResponseMessage, SnapshotResponseMessage>
{
    private static final String                                CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<SnapshotMetrics, SnapshotAppProperties> metricsDictionary;

    public TranslatorAppSnapshotKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(SnapshotMetrics.APP_ID, SnapshotAppProperties.APP_NAME);
        metricsDictionary.put(SnapshotMetrics.APP_VERSION, SnapshotAppProperties.APP_VERSION);
    }

    @Override
    public SnapshotResponseMessage translate(KuraResponseMessage kuraMessage)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(kuraMessage.getChannel().getScope());

        SnapshotResponseChannel commandResponseChannel = translate(kuraMessage.getChannel());

        //
        // Kura payload
        SnapshotResponsePayload responsePayload = translate(kuraMessage.getPayload());

        //
        // Kura Message
        SnapshotResponseMessage kapuaMessage = new SnapshotResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setChannel(commandResponseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setSentOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(kuraMessage.getTimestamp());
        kapuaMessage.setResponseCode(translate((Integer) kuraMessage.getPayload().getMetrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return Kapua Message
        return kapuaMessage;
    }

    private SnapshotResponseChannel translate(KuraResponseChannel kuraChannel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(kuraChannel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          kuraChannel.getMessageClassification());
        }

        SnapshotResponseChannel snapshotResponseChannel = new SnapshotResponseChannel();

        String[] appIdTokens = kuraChannel.getAppId().split("-");

        if (!SnapshotMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!SnapshotMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        snapshotResponseChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        snapshotResponseChannel.setVersion(ConfigurationAppProperties.APP_VERSION);

        //
        // Return Kapua Channel
        return snapshotResponseChannel;
    }

    private SnapshotResponsePayload translate(KuraResponsePayload kuraPayload)
        throws KapuaException
    {
        SnapshotResponsePayload snapshotResponsePayload = new SnapshotResponsePayload();

        snapshotResponsePayload.setExceptionMessage((String) kuraPayload.getMetrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        snapshotResponsePayload.setExceptionStack((String) kuraPayload.getMetrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        XmlSnapshotIdResult snapshotIdResult = null;
        if (kuraPayload.getBody() != null) {
            String body = null;
            try {
                body = new String(kuraPayload.getBody(), charEncoding);
            }
            catch (Exception e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              snapshotResponsePayload.getBody());
            }

            try {
                snapshotIdResult = XmlUtil.unmarshal(body, XmlSnapshotIdResult.class);
            }
            catch (Exception e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              body);
            }
        }
        translateBody(snapshotResponsePayload, charEncoding, snapshotIdResult);

        //
        // Return Kapua Payload
        return snapshotResponsePayload;
    }

    private void translateBody(SnapshotResponsePayload snapshotResponsePayload, String charEncoding, XmlSnapshotIdResult kuraSnapshotIdResult)
        throws TranslatorException
    {
        try {
            if (kuraSnapshotIdResult != null) {
                KapuaLocator locator = KapuaLocator.getInstance();
                DeviceSnapshotFactory deviceSnapshotFactory = locator.getFactory(DeviceSnapshotFactory.class);
                DeviceSnapshotIds deviceSnapshots = deviceSnapshotFactory.newDeviceSnapshotIds();

                List<Long> snapshotIds = kuraSnapshotIdResult.getSnapshotIds();
                for (Long snapshotId : snapshotIds) {
                    deviceSnapshots.getSnapshotsIds().add(snapshotId);
                }

                StringWriter sw = new StringWriter();
                XmlUtil.marshal(deviceSnapshots, sw);
                byte[] requestBody = sw.toString().getBytes(charEncoding);

                snapshotResponsePayload.setBody(requestBody);
            }
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_BODY,
                                          e,
                                          kuraSnapshotIdResult); // null for now
        }
    }

    private KapuaResponseCode translate(Integer kuraResponseCode)
    {
        KapuaResponseCode responseCode;
        switch (kuraResponseCode) {
            case 200:
                responseCode = KapuaResponseCode.ACCEPTED;
                break;

            case 400:
                responseCode = KapuaResponseCode.BAD_REQUEST;
                break;

            case 404:
                responseCode = KapuaResponseCode.NOT_FOUND;
                break;
            case 500:
            default:
                responseCode = KapuaResponseCode.INTERNAL_ERROR;
                break;
        }

        return responseCode;
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