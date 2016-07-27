package org.org.eclipse.kapua.translator.kapua.kura;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.DeployMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.deploy.internal.DeployAppProperties;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestChannel;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployRequestPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.org.eclipse.kapua.service.device.management.bundle.internal.BundleAppProperties;

public class TranslatorAppDeployKapuaKura implements Translator<DeployRequestMessage, KuraRequestMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<DeployAppProperties, DeployMetrics> propertiesDictionary;

    private TranslatorAppDeployKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(DeployAppProperties.APP_NAME, DeployMetrics.APP_ID);
        propertiesDictionary.put(DeployAppProperties.APP_VERSION, DeployMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(DeployRequestMessage message)
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

    private KuraRequestChannel translate(DeployRequestChannel channel)
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
            resources.add("packages");
        }
        else if (channel.getMethod().equals(KapuaMethod.EXECUTE) && channel.isInstall()) {
            resources.add("install");
        }
        else {
            resources.add("uninstall");
        }

        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(DeployRequestPayload payload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        String deployFileName = payload.getDeployInstallFileName();
        if (deployFileName != null) {
            kuraRequestPayload.metrics()
                              .put(propertiesDictionary.get(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME).getValue(),
                                   deployFileName);
        }

        String deployInstallUrl = payload.getDeployInstallUrl();
        if (deployInstallUrl != null) {
            kuraRequestPayload.metrics()
                              .put(propertiesDictionary.get(DeployAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL).getValue(),
                                   deployInstallUrl);
        }

        String deployUninstallFileName = payload.getDeployUninstallFileName();
        if (deployUninstallFileName != null) {
            try {
                kuraRequestPayload.setBody(deployUninstallFileName.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              deployUninstallFileName);
            }
        }

        byte[] deployBody = payload.getBody();
        if (deployBody != null) {
            kuraRequestPayload.setBody(deployBody);
        }

        return kuraRequestPayload;
    }

    @Override
    public Class<DeployRequestMessage> getClassFrom()
    {
        return DeployRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }
}
