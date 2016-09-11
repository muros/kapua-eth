package org.eclipse.kapua.translator.kapua.kura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.SnapshotMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestChannel;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestPayload;
import org.eclipse.kapua.service.device.management.snapshot.internal.SnapshotAppProperties;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

public class TranslatorAppSnapshotKapuaKura implements Translator<SnapshotRequestMessage, KuraRequestMessage>
{
    private static final String                                CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<SnapshotAppProperties, SnapshotMetrics> propertiesDictionary;

    public TranslatorAppSnapshotKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(SnapshotAppProperties.APP_NAME, SnapshotMetrics.APP_ID);
        propertiesDictionary.put(SnapshotAppProperties.APP_VERSION, SnapshotMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(SnapshotRequestMessage kapuaMessage)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.find(kapuaMessage.getScopeId());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.find(kapuaMessage.getScopeId(), kapuaMessage.getDeviceId());

        KuraRequestChannel kuraRequestChannel = translate(kapuaMessage.getChannel());
        kuraRequestChannel.setScope(account.getName());
        kuraRequestChannel.setClientId(device.getClientId());

        //
        // Kura payload
        KuraRequestPayload kuraPayload = translate(kapuaMessage.getPayload());

        //
        // Return Kura Message
        return new KuraRequestMessage(kuraRequestChannel, kapuaMessage.getReceivedOn(), kuraPayload);

    }

    private KuraRequestChannel translate(SnapshotRequestChannel kapuaChannel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(SnapshotAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(SnapshotAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());

        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(kapuaChannel.getMethod()));

        // Build resources
        List<String> resources = new ArrayList<>();
        switch (kapuaChannel.getMethod()) {
            case EXECUTE:
                resources.add("rollback");
                break;
            case READ:
                resources.add("snapshots");
                break;
            case CREATE:
            case DELETE:
            case OPTIONS:
            case WRITE:
            default:
                break;

        }

        String snapshotId = kapuaChannel.getSnapshotId();
        if (snapshotId != null) {
            resources.add(snapshotId);
        }
        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        //
        // Return Kura Channel
        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(SnapshotRequestPayload kapuaPayload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        if (kapuaPayload.getBody() != null) {
            kuraRequestPayload.setBody(kapuaPayload.getBody());
        }

        //
        // Return Kura Payload
        return kuraRequestPayload;
    }

    @Override
    public Class<SnapshotRequestMessage> getClassFrom()
    {
        return SnapshotRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }

}