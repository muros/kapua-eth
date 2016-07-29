package org.org.eclipse.kapua.translator.kapua.kura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.BundleMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.org.eclipse.kapua.service.device.management.bundle.internal.BundleAppProperties;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestChannel;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestMessage;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleRequestPayload;

public class TranslatorAppBundleKapuaKura implements Translator<BundleRequestMessage, KuraRequestMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<BundleAppProperties, BundleMetrics> propertiesDictionary;

    private TranslatorAppBundleKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(BundleAppProperties.APP_NAME, BundleMetrics.APP_ID);
        propertiesDictionary.put(BundleAppProperties.APP_VERSION, BundleMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(BundleRequestMessage message)
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

    private KuraRequestChannel translate(BundleRequestChannel channel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(BundleAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(BundleAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());
        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(channel.getMethod()));

        List<String> resources = new ArrayList<>();
        if (channel.getMethod().equals(KapuaMethod.READ)) {
            resources.add("bundle");
        } else if (channel.getMethod().equals(KapuaMethod.EXECUTE) && channel.isStart()) {
            resources.add("start");
        } else {
            resources.add("stop");
        }
        
        String bundleId = channel.getBundleId();
        if (bundleId != null) {
            resources.add(bundleId);
        }
        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(BundleRequestPayload payload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        kuraRequestPayload.setBody(payload.getBody());

        return kuraRequestPayload;
    }

    @Override
    public Class<BundleRequestMessage> getClassFrom()
    {
        return BundleRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }

}
